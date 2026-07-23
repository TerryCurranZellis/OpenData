/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import java.util.Objects;

/**
 * Immutable Excel parser settings.
 *
 * @param sheetName sheet name; blank means use {@code sheetIndex}
 * @param sheetIndex zero-based fallback sheet index
 * @param headerRowIndex zero-based header row
 * @param firstDataRowIndex zero-based first data row
 * @param evaluateFormulas whether formula results should be evaluated
 * @param skipCompletelyBlankRows whether fully blank rows are omitted
 */
public record ExcelParserOptions(
        String sheetName,
        int sheetIndex,
        int headerRowIndex,
        int firstDataRowIndex,
        boolean evaluateFormulas,
        boolean skipCompletelyBlankRows) {

    public ExcelParserOptions {
        sheetName = Objects.requireNonNullElse(sheetName, "").trim();
        if (sheetIndex < 0 || headerRowIndex < 0 || firstDataRowIndex < 0) {
            throw new IllegalArgumentException("Excel row and sheet indexes must not be negative");
        }
        if (firstDataRowIndex <= headerRowIndex) {
            throw new IllegalArgumentException(
                    "firstDataRowIndex must be after headerRowIndex");
        }
    }

    public static ExcelParserOptions defaults() {
        return new ExcelParserOptions("", 0, 0, 1, true, true);
    }
}
