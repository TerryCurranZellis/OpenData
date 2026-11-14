/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import com.towermarsh.opendata.exception.ImportException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Parser for Excel `.xls` and `.xlsx` workbooks.
  *
 * @author Terry Curran
 * @version 4.2.0
 */
public final class ExcelDataParser implements DataParser<Map<String, String>> {

    private final ExcelParserOptions options;

    /**
     * Creates an Excel parser using default options.
     */
    public ExcelDataParser() {
        this(ExcelParserOptions.defaults());
    }

    /**
     * Creates an Excel parser using the supplied options.
     *
     * @param options Excel parser options
     */
    public ExcelDataParser(ExcelParserOptions options) {
        this.options = Objects.requireNonNull(options, "options");
    }

    /**
     * {@inheritDoc}
     *
     * @param file target file
     * @return result of parse
     * @throws ImportException if parse fails
     */
    @Override
    public List<Map<String, String>> parse(Path file) throws ImportException {
        Objects.requireNonNull(file, "file");
        try (var input = Files.newInputStream(file);
                var workbook = WorkbookFactory.create(input)) {
            var sheet = selectSheet(workbook);
            var headerRow = sheet.getRow(options.headerRowIndex());
            if (headerRow == null) {
                throw new ImportException(
                        "Header row " + options.headerRowIndex()
                        + " does not exist in sheet " + sheet.getSheetName());
            }

            var formatter = new DataFormatter(Locale.UK);
            FormulaEvaluator evaluator = options.evaluateFormulas()
                    ? workbook.getCreationHelper().createFormulaEvaluator()
                    : null;
            var headers = readHeaders(headerRow, formatter, evaluator);
            var records = new ArrayList<Map<String, String>>();

            for (int rowIndex = options.firstDataRowIndex();
                    rowIndex <= sheet.getLastRowNum();
                    rowIndex++) {
                var row = sheet.getRow(rowIndex);
                var record = readRow(row, headers, formatter, evaluator);
                var blank = record.values().stream().allMatch(String::isBlank);
                if (!blank || !options.skipCompletelyBlankRows()) {
                    records.add(record);
                }
            }
            return List.copyOf(records);
        } catch (ImportException ex) {
            throw ex;
        } catch (IOException | RuntimeException ex) {
            throw new ImportException("Unable to parse Excel file: " + file, ex);
        }
    }

    /**
     * Selects the workbook sheet configured for parsing.
     *
     * @param workbook workbook being parsed
     * @return selected sheet
     * @throws ImportException if the configured sheet cannot be resolved
     */
    private Sheet selectSheet(Workbook workbook) throws ImportException {
        if (!options.sheetName().isBlank()) {
            Sheet sheet = workbook.getSheet(options.sheetName());
            if (sheet == null) {
                throw new ImportException(
                        "Workbook does not contain sheet: " + options.sheetName());
            }
            return sheet;
        }
        if (options.sheetIndex() >= workbook.getNumberOfSheets()) {
            throw new ImportException(
                    "Workbook contains " + workbook.getNumberOfSheets()
                    + " sheet(s); requested index " + options.sheetIndex());
        }
        return workbook.getSheetAt(options.sheetIndex());
    }

    /**
     * Reads and normalises the header row.
     *
     * @param headerRow header row
     * @param formatter cell formatter
     * @param evaluator optional formula evaluator
     * @return parsed header names
     * @throws ImportException if the header row contains no cells
     */
    private static List<String> readHeaders(
            Row headerRow,
            DataFormatter formatter,
            FormulaEvaluator evaluator) throws ImportException {
        var columnCount = Math.max(0, headerRow.getLastCellNum());
        if (columnCount == 0) {
            throw new ImportException("Excel header row contains no cells");
        }

        var headers = new ArrayList<String>(columnCount);
        var occurrences = new HashMap<String, Integer>();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            var raw = format(headerRow.getCell(columnIndex), formatter, evaluator).trim();
            var base = raw.isEmpty() ? "COLUMN_" + (columnIndex + 1) : raw;
            var occurrence = occurrences.merge(base, 1, Integer::sum);
            headers.add(occurrence == 1 ? base : base + "_" + occurrence);
        }
        return List.copyOf(headers);
    }

    /**
     * Reads one worksheet row into a name-value map.
     *
     * @param row worksheet row, possibly {@code null}
     * @param headers column headers
     * @param formatter cell formatter
     * @param evaluator optional formula evaluator
     * @return row values keyed by header
     */
    private static Map<String, String> readRow(
            Row row,
            List<String> headers,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        var record = new LinkedHashMap<String, String>();
        for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
            var cell = row == null ? null : row.getCell(columnIndex);
            record.put(headers.get(columnIndex), format(cell, formatter, evaluator));
        }
        return record;
    }

    /**
     * Formats a workbook cell as text.
     *
     * @param cell source cell
     * @param formatter cell formatter
     * @param evaluator optional formula evaluator
     * @return formatted cell value
     */
    private static String format(
            Cell cell,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }
        return evaluator == null
                ? formatter.formatCellValue(cell)
                : formatter.formatCellValue(cell, evaluator);
    }
}
