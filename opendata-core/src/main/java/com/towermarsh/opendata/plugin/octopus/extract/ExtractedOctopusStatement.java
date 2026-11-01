/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable content and provenance for one extracted Octopus statement.
 *
 * @param path path to file location
 * @param fileName name of the file
 * @param statementDate date of this statement (taken from filename)
 * @param sha256 file hash
 * @param sizeBytes size of the file in bytes
 * @param text text extracted from the file
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public record ExtractedOctopusStatement(
        Path path,
        String fileName,
        LocalDate statementDate,
        String sha256,
        long sizeBytes,
        String text) {

    /**
     * Load the record
     *
     * @param path path to file location
     * @param fileName name of the file
     * @param statementDate date of this statement (taken from filename)
     * @param sha256 file hash
     * @param sizeBytes size of the file in bytes
     * @param text text extracted from the file
     */
    public ExtractedOctopusStatement {
        Objects.requireNonNull(path, "path");
        fileName = requireText(fileName, "fileName");
        Objects.requireNonNull(statementDate, "statementDate");
        sha256 = requireText(sha256, "sha256");
        text = Objects.requireNonNull(text, "text");
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("sizeBytes must not be negative");
        }
    }

    /**
     * Confirm that a string is not empty
     * @param value string to check
     * @param name name of string
     * @return a valid string
     */
    private static String requireText(final String value, final String name) {
        Objects.requireNonNull(value, name);
        final var result = value.trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return result;
    }
}
