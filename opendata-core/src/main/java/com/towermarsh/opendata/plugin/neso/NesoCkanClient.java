/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.neso;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Minimal NESO CKAN client: lists CSV resources and downloads them to disk.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class NesoCkanClient {

    private static final Logger LOG = Logger.getLogger(NesoCkanClient.class.getName());
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String USER_AGENT = "OpenData/3.1";

    private final NesoSettings settings;
    private final HttpClient http;

    /**
     * Creates a client.
     *
     * @param settings NESO settings
     */
    public NesoCkanClient(final NesoSettings settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
        this.http = HttpClient.newBuilder().connectTimeout(settings.connectTimeout()).build();
    }

    /**
     * Lists the CSV resources of the configured dataset whose names are accepted.
     *
     * @param nameFilter resource name filter
     * @return matching resources, without local files
     * @throws IOException on network or format failure
     * @throws InterruptedException when interrupted
     */
    public List<NesoResource> listCsvResources(final Predicate<String> nameFilter)
            throws IOException, InterruptedException {
        final var uri = URI.create(settings.baseUrl() + "?id=" + settings.datasetId());
        final var response = http.send(request(uri), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("NESO catalogue request failed with HTTP %d for %s"
                    .formatted(response.statusCode(), uri));
        }
        final var manifest = JSON.readTree(response.body());
        if (!manifest.path("success").asBoolean()) {
            throw new IOException("CKAN dataset lookup failed: " + settings.datasetId());
        }
        final List<NesoResource> resources = new ArrayList<>();
        for (var r : manifest.path("result").path("resources")) {
            final var name = r.path("name").asText("");
            if ("CSV".equalsIgnoreCase(r.path("format").asText("")) && nameFilter.test(name)) {
                final var url = r.path("url").asText();
                if (!url.toLowerCase(Locale.ROOT).startsWith("https://")) {
                    throw new IOException("Non-HTTPS resource URL: " + url);
                }
                resources.add(new NesoResource(name, url, null));
            }
        }
        return List.copyOf(resources);
    }

    /**
     * Downloads one resource into a directory.
     *
     * @param resource resource to download
     * @param directory target directory
     * @param index sequence number used to build a unique file name
     * @return resource bound to its local file
     * @throws IOException on network or file failure
     * @throws InterruptedException when interrupted
     */
    public NesoResource download(
            final NesoResource resource, final Path directory, final int index)
            throws IOException, InterruptedException {
        LOG.log(Level.INFO, "Downloading {0} from NESO", resource.name());
        final var target = directory.resolve("resource-%03d.csv".formatted(index));
        final var response = http.send(
                request(URI.create(resource.url())), HttpResponse.BodyHandlers.ofInputStream());
        try (var stream = response.body()) {
            if (response.statusCode() != 200) {
                throw new IOException("NESO download failed with HTTP %d for %s"
                        .formatted(response.statusCode(), resource.url()));
            }
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return resource.withFile(target);
    }

    private HttpRequest request(final URI uri) {
        return HttpRequest.newBuilder(uri)
                .timeout(settings.requestTimeout())
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();
    }
}
