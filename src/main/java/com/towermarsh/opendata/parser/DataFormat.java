/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Tabular and structured file formats understood by the parser factory.
  *
 * @author Terry Curran
 * @version 1.0.0
 */
public enum DataFormat {

    /**
     *
     */
    CSV,

    /**
     *
     */
    EXCEL,

    /**
     *
     */
    JSON;

    /**
     * Determines a format from a filename extension.
     *
     * @param file source file
     * @return detected format
     */
    public static DataFormat fromPath(Path file) {
        Objects.requireNonNull(file, "file");
        final var fileName = file.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("Path must include a file name: " + file);
        }
        String name = fileName.toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".csv")) {
            return CSV;
        }
        if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
            return EXCEL;
        }
        if (name.endsWith(".json")) {
            return JSON;
        }
        throw new IllegalArgumentException("Unsupported data file format: " + file);
    }
}
