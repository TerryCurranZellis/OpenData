/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.neso;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Result of a NESO extract step: downloaded CSV files inside a working directory.
 *
 * @param workingDirectory temporary directory holding the downloads
 * @param resources downloaded resources
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public record NesoExtraction(Path workingDirectory, List<NesoResource> resources) {

    /** Validates the extraction. */
    public NesoExtraction {
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        resources = List.copyOf(Objects.requireNonNull(resources, "resources"));
    }

    /** Empty extraction used before the extract step has run. */
    public static NesoExtraction empty() {
        return new NesoExtraction(Path.of("."), List.of());
    }

    /**
     * Deletes the working directory and its files if it was created by an extract step.
     */
    public void cleanUp() {
        if (resources.isEmpty() && !Files.isDirectory(workingDirectory)) {
            return;
        }
        if (".".equals(workingDirectory.toString())) {
            return;
        }
        try (var files = Files.walk(workingDirectory)) {
            files.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });
        } catch (IOException | UncheckedIOException exception) {
            java.util.logging.Logger.getLogger(NesoExtraction.class.getName())
                    .log(java.util.logging.Level.WARNING,
                            "Unable to remove NESO working directory " + workingDirectory, exception);
        }
    }
}
