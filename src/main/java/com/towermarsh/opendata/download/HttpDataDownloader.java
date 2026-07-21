/*
 *  Filename: HttpDataDownloader.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
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
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
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
