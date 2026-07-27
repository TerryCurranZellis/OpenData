/*
 * Filename: HttpDataDownloaderTest.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sun.net.httpserver.HttpServer;
import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.model.DataFile;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class HttpDataDownloaderTest {

    @TempDir
    Path tempDirectory;

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void downloadsToFinalPath() throws Exception {
        byte[] body = "sample-data".getBytes(StandardCharsets.UTF_8);
        URI uri = serve(body);
        Path destination = tempDirectory.resolve("nested/data.csv");

        DataFile dataFile = new HttpDataDownloader().download(uri, destination);

        assertEquals(body.length, dataFile.getSize());
        assertEquals("sample-data", Files.readString(destination));
        assertFalse(Files.exists(destination.resolveSibling("data.csv.part")));
    }

    @Test
    void enforcesMaximumSize() throws Exception {
        URI uri = serve("too-large".getBytes(StandardCharsets.UTF_8));
        HttpDownloadOptions options = new HttpDownloadOptions(
                Duration.ofSeconds(5), Duration.ofSeconds(5),
                "OpenData-Test", true, 3);
        Path destination = tempDirectory.resolve("data.csv");

        assertThrows(DownloadException.class,
                () -> new HttpDataDownloader(options).download(uri, destination));
        assertFalse(Files.exists(destination));
    }

    private URI serve(byte[] body) throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/data", exchange -> {
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/data");
    }
}
