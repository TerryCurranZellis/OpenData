/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

import java.net.URI;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

/**
 * Auditable metadata for a downloaded source file.
 */
public record SourceFileMetadata(
        URI sourceUri,
        String fileName,
        String contentType,
        long sizeBytes,
        String sha256,
        Instant downloadedAt) {

    public SourceFileMetadata {
        Objects.requireNonNull(sourceUri, "sourceUri");
        fileName = requireText(fileName, "fileName");
        contentType = contentType == null ? null : contentType.trim();
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("sizeBytes cannot be negative");
        }
        sha256 = requireText(sha256, "sha256").toLowerCase(Locale.ROOT);
        if (!sha256.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("sha256 must contain 64 hexadecimal characters");
        }
        Objects.requireNonNull(downloadedAt, "downloadedAt");
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " cannot be blank");
        }
        return value.trim();
    }
}
