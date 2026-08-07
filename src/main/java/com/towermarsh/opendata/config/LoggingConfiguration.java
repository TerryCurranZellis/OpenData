/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.validation.ValidationRules;
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
 * @version 2.1
 */
public record LoggingConfiguration(Path directory, int fileLimitBytes, int fileCount, boolean append) {
    /** 
     * Validates and normalises record components. 
     */
    public LoggingConfiguration {
        Objects.requireNonNull(directory, "directory");
        try {
            fileLimitBytes = ValidationRules.requireRange(
                    fileLimitBytes, 1024, Integer.MAX_VALUE, "logging.file-limit-bytes");
            fileCount = ValidationRules.requireRange(fileCount, 1, 100, "logging.file-count");
        } catch (IllegalArgumentException exception) {
            throw new OpenDataConfigurationException(exception.getMessage(), exception);
        }
    }
}
