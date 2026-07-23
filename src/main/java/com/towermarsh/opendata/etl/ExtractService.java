/*
 * Filename: ExtractService.java
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
package com.towermarsh.opendata.etl;

import com.towermarsh.opendata.download.DataDownloader;
import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.model.DataFile;
import com.towermarsh.opendata.parser.DataParser;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Extracts data from external OpenData sources.
 *
 * <p>
 * The extract phase downloads a dataset and converts it into an internal record
 * structure.</p>
 *
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class ExtractService {

    private final DataDownloader downloader;
    private final DataParser parser;

    /**
     * Creates an extract service.
     *
     * @param downloader file downloader
     * @param parser data parser
     */
    public ExtractService(
            DataDownloader downloader,
            DataParser parser) {

        this.downloader = downloader;
        this.parser = parser;
    }

    /**
     * Extracts records from a remote dataset.
     *
     * @param source source URL
     * @param destination local file
     * @return extracted records
     *
     * @throws DownloadException download failure
     * @throws ImportException parsing failure
     */
    public List<Map<String, String>> extract(
            URI source,
            Path destination)
            throws DownloadException, ImportException {

        DataFile file
                = downloader.download(
                        source,
                        destination);

        return parser.parse(
                file.getFilePath());
    }
}
