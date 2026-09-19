/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.download.strategy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import com.towermarsh.opendata.config.model.LinkDiscoveryDefinition;
import com.towermarsh.opendata.exception.DownloadException;

/**
 * Downloads an HTML landing page, discovers a matching file link and streams
 * the resolved resource to disk.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public class HtmlLinkDiscoveryStrategy {

    private final HttpClient httpClient;
    private final HtmlLinkResolver linkResolver;
    private final DirectHttpDownloadStrategy fileDownloader;

    /**
     * Creates the strategy.
     *
     * @param connectTimeout HTTP connection timeout
     */
    public HtmlLinkDiscoveryStrategy(
            Duration connectTimeout) {

        Objects.requireNonNull(connectTimeout, "connectTimeout");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.linkResolver = new HtmlLinkResolver();
        this.fileDownloader
                = new DirectHttpDownloadStrategy(connectTimeout);
    }

    /**
     * Resolves and downloads a file linked from an HTML page.
     *
     * @param landingPageUri landing-page URI
     * @param destination final local file
     * @param headers non-secret request headers
     * @param requestTimeout request timeout
     * @param discovery link-discovery rules
     * @return completed download
     * @throws com.towermarsh.opendata.exception.DownloadException
     */
    public ResolvedDownload download(
            URI landingPageUri,
            Path destination,
            Map<String, String> headers,
            Duration requestTimeout,
            LinkDiscoveryDefinition discovery) throws DownloadException {

        var html = fetchHtml(
                landingPageUri,
                headers,
                requestTimeout);
        var resolvedUri = linkResolver.resolve(
                landingPageUri,
                html,
                discovery);

        return fileDownloader.download(
                resolvedUri,
                destination,
                headers,
                requestTimeout);
    }

    private String fetchHtml(
            URI uri,
            Map<String, String> headers,
            Duration timeout) throws DownloadException {

        var builder
                = HttpRequest.newBuilder(uri)
                        .GET()
                        .timeout(timeout);
        headers.forEach(builder::header);

        try {
            var response = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                throw new DownloadException(
                        "Landing page returned HTTP status %d for %s."
                                .formatted(response.statusCode(), uri));
            }

            return response.body();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DownloadException(
                    "Landing-page request was interrupted: " + uri,
                    exception);
        } catch (IOException exception) {
            throw new DownloadException(
                    "Unable to download landing page: " + uri,
                    exception);
        }
    }
}
