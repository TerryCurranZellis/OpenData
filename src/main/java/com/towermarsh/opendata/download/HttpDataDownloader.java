/*
 * Filename: HttpDataDownloader.java
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
package com.towermarsh.opendata.download;

import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.model.DataFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

/**
 * HTTP implementation of the data downloader.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class HttpDataDownloader
        implements DataDownloader {

    private final HttpClient client;

    /**
     * Creates an HTTP downloader.
     */
    public HttpDataDownloader() {

        client
                = HttpClient.newBuilder()
                        .followRedirects(
                                HttpClient.Redirect.NORMAL)
                        .build();
    }

    /**
     * Downloads a remote file.
     *
     * @param sourceUri remote URI
     * @param destination output file
     * @return downloaded file information
     */
    @Override
    public DataFile download(
            URI sourceUri,
            Path destination)
            throws DownloadException {

        try {

            HttpRequest request
                    = HttpRequest.newBuilder()
                            .uri(sourceUri)
                            .GET()
                            .build();

            HttpResponse<byte[]> response
                    = client.send(
                            request,
                            HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 400) {

                throw new DownloadException(
                        "HTTP download failed: "
                        + response.statusCode());
            }

            Files.write(
                    destination,
                    response.body(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            return new DataFile(
                    destination,
                    response.body().length,
                    LocalDateTime.now());

        } catch (IOException | InterruptedException ex) {

            Thread.currentThread()
                    .interrupt();

            throw new DownloadException(
                    "Unable to download file",
                    ex);
        }
    }
}
