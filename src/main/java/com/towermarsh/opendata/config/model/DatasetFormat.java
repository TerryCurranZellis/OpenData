package com.towermarsh.opendata.config.model;

/**
 * Data formats understood by the OpenData Framework.
 */
public enum DatasetFormat {
    CSV,
    JSON,
    XML,
    XLS,
    XLSX,
    HTML,
    ZIP,
    TEXT,
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
