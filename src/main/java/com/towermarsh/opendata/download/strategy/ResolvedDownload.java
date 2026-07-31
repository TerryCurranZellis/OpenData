/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.download.strategy;

import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Result of resolving and downloading one remote resource.
 *
 * @param requestedUri original configured URI
 * @param resolvedUri final downloadable URI
 * @param localFile downloaded file
 * @param byteCount downloaded size
 * @param contentType response content type
 * @param completedAtUtc completion timestamp
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record ResolvedDownload(
        URI requestedUri,
        URI resolvedUri,
        Path localFile,
        long byteCount,
        Optional<String> contentType,
        Instant completedAtUtc) {

    /** 
     * Validates and normalises record components. 
     */
    public ResolvedDownload {
        Objects.requireNonNull(requestedUri, "requestedUri");
        Objects.requireNonNull(resolvedUri, "resolvedUri");
        Objects.requireNonNull(localFile, "localFile");
        contentType = Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(completedAtUtc, "completedAtUtc");

        if (byteCount < 0) {
            throw new IllegalArgumentException(
                    "byteCount must not be negative.");
        }
    }
}
