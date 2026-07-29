/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import com.towermarsh.opendata.exception.ImportException;
import java.io.IOException;
import java.io.InputStream;
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
 * @version 17 July 2026
 */
public final class ExcelDataParser implements DataParser {

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

    @Override
    public List<Map<String, String>> parse(Path file) throws ImportException {
        Objects.requireNonNull(file, "file");
        try (InputStream input = Files.newInputStream(file);
                Workbook workbook = WorkbookFactory.create(input)) {
            Sheet sheet = selectSheet(workbook);
            Row headerRow = sheet.getRow(options.headerRowIndex());
            if (headerRow == null) {
                throw new ImportException(
                        "Header row " + options.headerRowIndex()
                        + " does not exist in sheet " + sheet.getSheetName());
            }

            DataFormatter formatter = new DataFormatter(Locale.UK);
            FormulaEvaluator evaluator = options.evaluateFormulas()
                    ? workbook.getCreationHelper().createFormulaEvaluator()
                    : null;
            List<String> headers = readHeaders(headerRow, formatter, evaluator);
            List<Map<String, String>> records = new ArrayList<>();

            for (int rowIndex = options.firstDataRowIndex();
                    rowIndex <= sheet.getLastRowNum();
                    rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Map<String, String> record = readRow(row, headers, formatter, evaluator);
                boolean blank = record.values().stream().allMatch(String::isBlank);
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
        int columnCount = Math.max(0, headerRow.getLastCellNum());
        if (columnCount == 0) {
            throw new ImportException("Excel header row contains no cells");
        }

        List<String> headers = new ArrayList<>(columnCount);
        Map<String, Integer> occurrences = new HashMap<>();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            String raw = format(headerRow.getCell(columnIndex), formatter, evaluator).trim();
            String base = raw.isEmpty() ? "COLUMN_" + (columnIndex + 1) : raw;
            int occurrence = occurrences.merge(base, 1, Integer::sum);
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
        Map<String, String> record = new LinkedHashMap<>();
        for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
            Cell cell = row == null ? null : row.getCell(columnIndex);
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
