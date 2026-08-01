/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.nio.file.Path;
import java.util.Objects;

/** 
 * java.util.logging file-handler settings.
 * @param directory directory containing application log files
 * @param fileLimitBytes maximum size of each log file in bytes
 * @param fileCount number of rotating log files to retain
 * @param append whether existing log files are appended to
  *
 * @author Terry Curran
 * @version 1.0.0
 */
public record LoggingConfiguration(Path directory, int fileLimitBytes, int fileCount, boolean append) {
    /** 
     * Validates and normalises record components. 
     */
    public LoggingConfiguration {
        Objects.requireNonNull(directory, "directory");
        if (fileLimitBytes < 1024) {
            throw new OpenDataConfigurationException("logging.file-limit-bytes must be at least 1024.");
        }
        if (fileCount < 1 || fileCount > 100) {
            throw new OpenDataConfigurationException("logging.file-count must be between 1 and 100.");
        }
    }
}
