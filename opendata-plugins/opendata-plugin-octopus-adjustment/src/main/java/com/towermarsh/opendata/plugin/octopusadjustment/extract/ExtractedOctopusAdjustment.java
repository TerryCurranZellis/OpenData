/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.extract;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Immutable source identity and provenance for one adjustment PDF.
 *
 * <p>No statement date is derived from the filename. Billing dates are derived
 * from PDF content by the shared Octopus parser during transformation.</p>
 *
 * @param path source path
 * @param fileName source filename
 * @param sha256 SHA-256 content hash
 * @param sizeBytes source size in bytes
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public record ExtractedOctopusAdjustment(
        Path path,
        String fileName,
        String sha256,
        long sizeBytes) {

    /** Validates and normalises source provenance. */
    public ExtractedOctopusAdjustment {
        Objects.requireNonNull(path, "path");
        fileName = requireText(fileName, "fileName");
        sha256 = requireText(sha256, "sha256").toLowerCase(Locale.ROOT);
        if (!sha256.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("sha256 must contain exactly 64 hexadecimal characters");
        }
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("sizeBytes must not be negative");
        }
    }

    private static String requireText(final String value, final String name) {
        Objects.requireNonNull(value, name);
        final var result = value.trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return result;
    }
}
