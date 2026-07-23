/*
 * Filename: DataFile.java
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
package com.towermarsh.opendata.model;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a downloaded OpenData file.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
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

    public Path getFilePath() {
        return filePath;
    }

    public long getSize() {
        return size;
    }

    public LocalDateTime getDownloadedAt() {
        return downloadedAt;
    }
}
