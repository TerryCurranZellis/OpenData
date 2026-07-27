/*
 * Filename: DirectHttpDownloadStrategy.java
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
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import com.towermarsh.opendata.exception.DownloadException;

/**
 * Streams a remote HTTP resource to a local file.
 *
 * <p>The response is written to a temporary part file and moved into place only
 * after a successful 2xx response. Interrupted requests preserve the thread's
 * interrupt status.</p>
  *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class DirectHttpDownloadStrategy {

    private final HttpClient httpClient;

    /**
     * Creates a downloader that follows normal redirects.
     *
     * @param connectTimeout connection timeout
     */
    public DirectHttpDownloadStrategy(
            final Duration connectTimeout) {

        Objects.requireNonNull(connectTimeout, "connectTimeout");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Downloads a URI.
     *
     * @param requestedUri remote URI
     * @param destination final destination file
     * @param headers non-secret HTTP headers
     * @param requestTimeout request timeout
     * @return download result
     * @throws com.towermarsh.opendata.exception.DownloadException
     */
    public ResolvedDownload download(
            final URI requestedUri,
            final Path destination,
            final Map<String, String> headers,
            final Duration requestTimeout) throws DownloadException {

        Objects.requireNonNull(requestedUri, "requestedUri");
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(headers, "headers");
        Objects.requireNonNull(requestTimeout, "requestTimeout");

        final Path absoluteDestination =
                destination.toAbsolutePath().normalize();
        final Path parent = absoluteDestination.getParent();
        final Path partFile =
                absoluteDestination.resolveSibling(
                        absoluteDestination.getFileName() + ".part");

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.deleteIfExists(partFile);

            final HttpRequest.Builder requestBuilder =
                    HttpRequest.newBuilder(requestedUri)
                            .GET()
                            .timeout(requestTimeout);

            headers.forEach(requestBuilder::header);

            final HttpResponse<Path> response = httpClient.send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofFile(partFile));

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                Files.deleteIfExists(partFile);
                throw new DownloadException(
                        "HTTP request returned status %d for %s."
                                .formatted(
                                        response.statusCode(),
                                        requestedUri));
            }

            moveCompletedFile(partFile, absoluteDestination);

            return new ResolvedDownload(
                    requestedUri,
                    response.uri(),
                    absoluteDestination,
                    Files.size(absoluteDestination),
                    response.headers()
                            .firstValue("Content-Type"),
                    Instant.now());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            deleteQuietly(partFile);
            throw new DownloadException(
                    "HTTP download was interrupted: " + requestedUri,
                    exception);
        } catch (IOException exception) {
            deleteQuietly(partFile);
            throw new DownloadException(
                    "Unable to download: " + requestedUri,
                    exception);
        }
    }

    private static void moveCompletedFile(
            final Path partFile,
            final Path destination) throws IOException {

        try {
            Files.move(
                    partFile,
                    destination,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    partFile,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteQuietly(final Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
            // Preserve the original failure.
        }
    }
}
