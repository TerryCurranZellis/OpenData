package com.towermarsh.opendata.parser;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable options for XLS and XLSX parsing.
 *
 * @param sheetName specific worksheet name, empty for first visible sheet
 * @param headerRowIndex zero-based header row
 * @param firstDataRowIndex zero-based first data row
 * @param skipHiddenRows whether hidden rows are omitted
 * @param evaluateFormulas whether formula results are evaluated
 * @param ignoreBlankRows whether completely blank rows are omitted
 * @param trimValues whether formatted values are trimmed
 */
public record ExcelParserOptions(
        Optional<String> sheetName,
        int headerRowIndex,
        int firstDataRowIndex,
        boolean skipHiddenRows,
        boolean evaluateFormulas,
        boolean ignoreBlankRows,
        boolean trimValues) {

    public ExcelParserOptions {
        sheetName = Objects.requireNonNull(sheetName, "sheetName");

        if (headerRowIndex < 0) {
            throw new IllegalArgumentException(
                    "headerRowIndex must not be negative.");
        }
        if (firstDataRowIndex <= headerRowIndex) {
            throw new IllegalArgumentException(
                    "firstDataRowIndex must be after headerRowIndex.");
        }
    }

    /**
     * Default workbook layout: first visible sheet, row 0 headers, row 1 data.
     *
     * @return default options
     */
    public static ExcelParserOptions defaults() {
        return new ExcelParserOptions(
                Optional.empty(),
                0,
                1,
                true,
                true,
                true,
                true);
    }
}
