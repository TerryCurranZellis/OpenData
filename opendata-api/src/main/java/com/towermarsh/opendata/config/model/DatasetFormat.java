/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config.model;

/**
 * Data formats understood by the OpenData Framework.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public enum DatasetFormat {

    /**
     * CSV file
     */
    CSV,

    /**
     * JSON file
     */
    JSON,

    /**
     * XML file
     */
    XML,

    /**
     * Excel prior to 2017
     */
    XLS,

    /**
     * Excel 2017+
     */
    XLSX,

    /**
     * HTML page
     */
    HTML,

    /**
     * ZIP file
     */
    ZIP,

    /**
     * raw text
     */
    TEXT,

    /**
     * some sort of binary file, could be anything
     */
    BINARY;

    /**
     * Parses a case-insensitive format name.
     *
     * @param value configured value
     * @return matching format
     */
    public static DatasetFormat parse(final String value) {
        return Enum.valueOf(
                DatasetFormat.class,
                value.trim().replace('-', '_').toUpperCase(java.util.Locale.ROOT));
    }
}
