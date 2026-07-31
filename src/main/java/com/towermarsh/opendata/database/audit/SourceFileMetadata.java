/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

import java.net.URI;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

/**
 * Auditable metadata for a downloaded source file.
 * @param sourceUri source URI from which the file was downloaded
 * @param fileName downloaded file name
 * @param contentType reported content type, if available
 * @param sizeBytes downloaded file size in bytes
 * @param sha256 SHA-256 digest of the downloaded file
 * @param downloadedAt time when the file was downloaded
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record SourceFileMetadata(
        URI sourceUri,
        String fileName,
        String contentType,
        long sizeBytes,
        String sha256,
        Instant downloadedAt) {

    /** 
     * Validates and normalises record components. 
     */
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

    /**
     * Returns a required non-blank text value.
     *
     * @param value value to validate
     * @param name field name for error reporting
     * @return trimmed text value
     */
    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " cannot be blank");
        }
        return value.trim();
    }
}
