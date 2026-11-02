
/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.neso;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NESO CKAN client: discovers CSV resources and downloads them using HTTPS.
 * Handles CKAN's relative, HTTP and DataStore resource URL variants.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public class NesoCkanClient {

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
    public NesoCkanClient(NesoSettings settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
        this.http = HttpClient.newBuilder()
                .connectTimeout(settings.connectTimeout())
                // Follow HTTPS redirects, but never downgrade HTTPS to HTTP.
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Lists CSV resources in the configured dataset matching the name filter.
     *
     * @param nameFilter resource name filter
     * @return matching resources, without local files
     * @throws IOException on network or catalogue failure
     * @throws InterruptedException if interrupted
     */
    public List<NesoResource> listCsvResources(Predicate<String> nameFilter)
            throws IOException, InterruptedException {
        Objects.requireNonNull(nameFilter, "nameFilter");

        var separator = settings.baseUrl().contains("?") ? "&" : "?";
        var uri = URI.create(settings.baseUrl() + separator + "id="
                + URLEncoder.encode(settings.datasetId(), StandardCharsets.UTF_8));
        var response = http.send(request(uri), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("NESO catalogue request failed with HTTP %d for %s"
                    .formatted(response.statusCode(), uri));
        }
        var manifest = JSON.readTree(response.body());
        if (!manifest.path("success").asBoolean()) {
            throw new IOException("CKAN dataset lookup failed: " + settings.datasetId()
                    + "; details: " + manifest.path("error"));
        }
        List<NesoResource> resources = new ArrayList<>();
        for (var resource : manifest.path("result").path("resources")) {
            var name = resource.path("name").asText("");

            if (!"CSV".equalsIgnoreCase(resource.path("format").asText(""))
                    || !nameFilter.test(name)) {
                continue;
            }
            var downloadUrl = resolveDownloadUrl(resource, name);
            if (downloadUrl == null) {
                LOG.log(Level.WARNING,
                        "Skipping NESO CSV resource without usable URL: {0}", name);
                continue;
            }
            resources.add(new NesoResource(name, downloadUrl.toString(), null));
        }
        return List.copyOf(resources);
    }

    /**
     * Resolves resource metadata to a secure CSV download URL. CKAN can publish
     * HTTP URLs even when HTTPS works on its API host. DataStore resources have
     * a canonical HTTPS export path based on their ID.
     */
    private URI resolveDownloadUrl(JsonNode resource, String name)
            throws IOException {

        var base = URI.create(settings.baseUrl()).resolve("/");
        var id = resource.path("id").asText("").trim();
        var rawUrl = resource.path("url").asText("").trim();
        var datastore = "datastore".equalsIgnoreCase(
                resource.path("url_type").asText(""));
        var datastoreActive
                = resource.path("datastore_active").asBoolean(false);
        var datastoreUrl = (datastore || datastoreActive) && isUuid(id)
                ? base.resolve("datastore/dump/" + id) : null;
        // DataStore exports have a reliable canonical URL; CKAN may return
        // an outdated or insecure URL for this kind of resource.
        if (datastore && datastoreUrl != null) {
            return datastoreUrl;
        }
        if (rawUrl.isEmpty()) {
            return datastoreUrl;
        }
        URI parsed;
        try {
            parsed = URI.create(rawUrl);
        } catch (IllegalArgumentException exception) {
            if (datastoreUrl != null) {
                LOG.log(Level.WARNING,
                        "Invalid NESO URL for {0}; using HTTPS DataStore export instead",
                        name);
                return datastoreUrl;
            }
            throw new IOException("Invalid NESO CSV URL for resource '" + name
                    + "' (id=" + id + "): " + rawUrl, exception);
        }
        var candidate = parsed;
        if (!candidate.isAbsolute()) {
            // Resolve relative paths from the NESO API root,
            // not from /api/3/action/.
            candidate = base.resolve(candidate.getRawAuthority() == null
                    && !rawUrl.startsWith("/") ? "/" + rawUrl : rawUrl);
        }
        if ("http".equalsIgnoreCase(candidate.getScheme())
                && candidate.getHost() != null
                && candidate.getHost().equalsIgnoreCase(base.getHost())
                && candidate.getRawUserInfo() == null) {
            // Upgrade HTTP only for the same host as the HTTPS catalogue.
            candidate = URI.create("https:" + candidate.toString().substring(
                    candidate.getScheme().length() + 1));
            LOG.log(Level.INFO, "Upgraded NESO resource URL to HTTPS: {0}", name);
        }
        if (!"https".equalsIgnoreCase(candidate.getScheme())
                || candidate.getHost() == null
                || candidate.getRawUserInfo() != null) {
            if (datastoreUrl != null) {
                LOG.log(Level.WARNING,
                        "Non-HTTPS NESO URL for {0}; using HTTPS DataStore export instead",
                        name);
                return datastoreUrl;
            }
            throw new IOException("No safe HTTPS download URL for NESO CSV resource '"
                    + name + "' (id=" + id + "): " + rawUrl);
        }
        return candidate;
    }

    private static boolean isUuid(String id) {
        try {
            return UUID.fromString(id).toString().equalsIgnoreCase(id);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * Downloads a resource into a working directory.
     *
     * @param resource resource to download
     * @param directory target directory
     * @param index sequence number for the temporary filename
     * @return resource with local file attached
     * @throws IOException on download or filesystem failure
     * @throws InterruptedException if interrupted
     */
    public NesoResource download(
            NesoResource resource, Path directory, int index)
            throws IOException, InterruptedException {
        Objects.requireNonNull(resource, "resource");
        var uri = URI.create(resource.url());
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IOException("Refusing non-HTTPS NESO download URL: " + uri);
        }
        LOG.log(Level.INFO, "Downloading {0} from NESO", resource.name());
        var target = directory.resolve("resource-%03d.csv".formatted(index));
        var response = http.send(
                request(uri), HttpResponse.BodyHandlers.ofInputStream());
        try (var stream = response.body()) {
            if (!"https".equalsIgnoreCase(response.uri().getScheme())) {
                throw new IOException("NESO redirected download to a non-HTTPS URL: "
                        + response.uri());
            }
            if (response.statusCode() != 200) {
                throw new IOException("NESO download failed with HTTP %d for %s"
                        .formatted(response.statusCode(), uri));
            }
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return resource.withFile(target);
    }

    private HttpRequest request(URI uri) {
        return HttpRequest.newBuilder(uri)
                .timeout(settings.requestTimeout())
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();
    }
}
