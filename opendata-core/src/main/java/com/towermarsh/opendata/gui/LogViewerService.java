/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.logging.LoggingManager;
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
 * @version 3.0.0
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
        final var directory = LoggingManager.activeLogDirectory()
                .orElseGet(() -> Path.of("logs").toAbsolutePath().normalize());
        final var file = locateLogFile(directory).orElse(directory.resolve(CURRENT_LOG_NAME));
        if (!Files.isRegularFile(file)) {
            return new LogSnapshot(
                    file,
                    "No OpenData log file has been created in " + directory + ".");
        }
        return new LogSnapshot(file, Files.readString(file, StandardCharsets.UTF_8));
    }

    private static Optional<Path> locateLogFile(final Path directory) throws IOException {
        final var current = directory.resolve(CURRENT_LOG_NAME);
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

    private static boolean isOpenDataLogFile(final Path path) {
        final var fileName = path.getFileName();
        return fileName != null
                && fileName.toString().matches("opendata-(?:\\d{14}-)?\\d+\\.log");
    }

    private static long lastModified(final Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException exception) {
            return Long.MIN_VALUE;
        }
    }
}
