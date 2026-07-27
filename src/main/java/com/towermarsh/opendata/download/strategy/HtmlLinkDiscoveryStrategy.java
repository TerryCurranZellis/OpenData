/*
 * Filename: HtmlLinkDiscoveryStrategy.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
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
 * @version 17 July 2026
 */
public final class HtmlLinkDiscoveryStrategy {

    private final HttpClient httpClient;
    private final HtmlLinkResolver linkResolver;
    private final DirectHttpDownloadStrategy fileDownloader;

    /**
     * Creates the strategy.
     *
     * @param connectTimeout HTTP connection timeout
     */
    public HtmlLinkDiscoveryStrategy(
            final Duration connectTimeout) {

        Objects.requireNonNull(connectTimeout, "connectTimeout");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.linkResolver = new HtmlLinkResolver();
        this.fileDownloader =
                new DirectHttpDownloadStrategy(connectTimeout);
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
            final URI landingPageUri,
            final Path destination,
            final Map<String, String> headers,
            final Duration requestTimeout,
            final LinkDiscoveryDefinition discovery) throws DownloadException {

        final String html = fetchHtml(
                landingPageUri,
                headers,
                requestTimeout);
        final URI resolvedUri = linkResolver.resolve(
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
            final URI uri,
            final Map<String, String> headers,
            final Duration timeout) throws DownloadException {

        final HttpRequest.Builder builder =
                HttpRequest.newBuilder(uri)
                        .GET()
                        .timeout(timeout);
        headers.forEach(builder::header);

        try {
            final HttpResponse<String> response = httpClient.send(
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
