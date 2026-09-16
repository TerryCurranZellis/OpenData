/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
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
 * @version 1.0.0
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
