/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.core.logging.LoggingManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Reads the current OpenData rotating log for the JavaFX log viewer.
 *
 * @author Terry Curran
 * @since 3.0.0
 * @version 4.1.0
 */
public final class LogViewerService {

    private static final String CURRENT_LOG_NAME = "opendata-0.log";

    /**
     * Flushes JUL handlers and reads the current log file.
     *
     * @return current log snapshot
     * @throws IOException if a discovered log cannot be read
     */
    public LogSnapshot load() throws IOException {
        LoggingManager.flush();
        var directory = LoggingManager.activeLogDirectory()
                .orElseGet(() -> Path.of("logs").toAbsolutePath().normalize());
        var file = locateLogFile(directory).orElse(directory.resolve(CURRENT_LOG_NAME));
        if (!Files.isRegularFile(file)) {
            return new LogSnapshot(
                    file,
                    "No OpenData log file has been created in " + directory + ".");
        }
        return new LogSnapshot(file, Files.readString(file, StandardCharsets.UTF_8));
    }

    /**
     * Locate log file.
     *
     * @param directory target directory
     * @return result of locate log file
     * @throws IOException if locate log file fails
     */
    private static Optional<Path> locateLogFile(Path directory) throws IOException {
        var current = directory.resolve(CURRENT_LOG_NAME);
        if (Files.isRegularFile(current)) {
            return Optional.of(current);
        }
        if (!Files.isDirectory(directory)) {
            return Optional.empty();
        }
        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(LogViewerService::isOpenDataLogFile)
                    .max(Comparator.comparingLong(LogViewerService::lastModified));
        }
    }

    /**
     * Checks whether open data log file.
     *
     * @param path target path
     * @return result of is open data log file
     */
    private static boolean isOpenDataLogFile(Path path) {
        var fileName = path.getFileName();
        return fileName != null
                && fileName.toString().matches("opendata-(?:\\d{14}-)?\\d+\\.log");
    }

    /**
     * Last modified.
     *
     * @param file target file
     * @return result of last modified
     */
    private static long lastModified(Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException exception) {
            return Long.MIN_VALUE;
        }
    }
}
