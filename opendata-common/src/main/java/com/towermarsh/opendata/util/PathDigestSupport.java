/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Shared filename and SHA-256 helpers for filesystem-backed plugin sources.
 *
 * @author Terry Curran
 * @version 3.3.0
 * @since 3.3.0
 */
public final class PathDigestSupport {

    private PathDigestSupport() {
        // Utility class.
    }

    /**
     * Returns the terminal filename component for the supplied path.
     *
     * @param path source path
     * @return filename text
     */
    public static String fileName(final Path path) {
        final var fileName = Objects.requireNonNull(path, "path").getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("Path must include a file name: " + path);
        }
        return fileName.toString();
    }

    /**
     * Computes the SHA-256 digest for one file.
     *
     * @param path file to hash
     * @return lower-case hexadecimal digest
     * @throws IOException if the file cannot be read
     */
    public static String sha256(final Path path) throws IOException {
        try {
            final var digest = MessageDigest.getInstance("SHA-256");
            try (var input = Files.newInputStream(path)) {
                final var buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) >= 0) {
                    if (count > 0) {
                        digest.update(buffer, 0, count);
                    }
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
