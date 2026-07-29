/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.model;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a downloaded OpenData file.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class DataFile {

    private final Path filePath;
    private final long size;
    private final LocalDateTime downloadedAt;

    /**
     * Creates a data file object.
     *
     * @param filePath physical file location
     * @param size file size in bytes
     * @param downloadedAt download timestamp
     */
    public DataFile(
            Path filePath,
            long size,
            LocalDateTime downloadedAt) {

        this.filePath
                = Objects.requireNonNull(
                        filePath,
                        "filePath");

        this.size = size;

        this.downloadedAt
                = Objects.requireNonNull(
                        downloadedAt,
                        "downloadedAt");
    }

    /**
     * Returns the downloaded file path.
     *
     * @return file path
     *
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Returns the downloaded file size in bytes.
     *
     * @return file size in bytes
     *
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns the download timestamp.
     *
     * @return download timestamp
     *
     */
    public LocalDateTime getDownloadedAt() {
        return downloadedAt;
    }
}
