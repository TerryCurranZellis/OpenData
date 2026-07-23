/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import com.towermarsh.opendata.exception.ImportException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Creates the framework parser appropriate to a downloaded file.
 */
public final class DataParserFactory {

    private DataParserFactory() {
    }

    /**
     * Creates a parser using default format settings.
     *
     * @param file downloaded file
     * @return matching parser
     * @throws ImportException if the extension is unsupported
     */
    public static DataParser forFile(Path file) throws ImportException {
        Objects.requireNonNull(file, "file");
        try {
            return switch (DataFormat.fromPath(file)) {
                case CSV -> new CsvDataParser();
                case EXCEL -> new ExcelDataParser();
                case JSON -> new JsonDataParser();
            };
        } catch (IllegalArgumentException ex) {
            throw new ImportException(ex.getMessage(), ex);
        }
    }
}
