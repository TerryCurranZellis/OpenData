package com.towermarsh.opendata.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.towermarsh.opendata.exception.ImportException;

/**
 * XLS and XLSX parser implemented using Apache POI.
 *
 * <p>Cell values are returned using the workbook's formatted display values,
 * which preserves dates, percentages and calculated formula results more
 * faithfully than reading raw numeric values.</p>
 */
public final class ExcelDataParser implements DataParser {

    private final ExcelParserOptions options;

    /**
     * Creates a parser for the first visible worksheet.
     */
    public ExcelDataParser() {
        this(ExcelParserOptions.defaults());
    }

    /**
     * Creates a configured workbook parser.
     *
     * @param options parser options
     */
    public ExcelDataParser(final ExcelParserOptions options) {
        this.options = Objects.requireNonNull(options, "options");
    }

    @Override
    public List<Map<String, String>> parse(final Path file)
            throws ImportException {

        Objects.requireNonNull(file, "file");

        try (InputStream input = Files.newInputStream(file);
             Workbook workbook = WorkbookFactory.create(input)) {

            final Sheet sheet = selectSheet(workbook);
            final FormulaEvaluator evaluator =
                    options.evaluateFormulas()
                            ? workbook.getCreationHelper()
                                    .createFormulaEvaluator()
                            : null;
            final DataFormatter formatter =
                    new DataFormatter(Locale.UK);

            final List<String> headers =
                    readHeaders(sheet, formatter, evaluator);
            final List<Map<String, String>> rows = new ArrayList<>();

            for (int rowIndex = options.firstDataRowIndex();
                    rowIndex <= sheet.getLastRowNum();
                    rowIndex++) {

                final Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                if (options.skipHiddenRows()
                        && row.getZeroHeight()) {
                    continue;
                }

                final Map<String, String> values =
                        readRow(row, headers, formatter, evaluator);

                if (options.ignoreBlankRows()
                        && values.values().stream()
                                .allMatch(String::isBlank)) {
                    continue;
                }

                rows.add(Map.copyOf(values));
            }

            return List.copyOf(rows);
        } catch (IOException | IllegalArgumentException exception) {
            throw new ImportException(
                    "Unable to parse Excel workbook: " + file,
                    exception);
        }
    }

    private Sheet selectSheet(final Workbook workbook) {
        if (options.sheetName().isPresent()) {
            final String name = options.sheetName().orElseThrow();
            final Sheet sheet = workbook.getSheet(name);
            if (sheet == null) {
                throw new IllegalArgumentException(
                        "Workbook does not contain sheet: " + name);
            }
            return sheet;
        }

        for (int index = 0;
                index < workbook.getNumberOfSheets();
                index++) {

            if (!workbook.isSheetHidden(index)
                    && !workbook.isSheetVeryHidden(index)) {
                return workbook.getSheetAt(index);
            }
        }

        throw new IllegalArgumentException(
                "Workbook contains no visible worksheets.");
    }

    private List<String> readHeaders(
            final Sheet sheet,
            final DataFormatter formatter,
            final FormulaEvaluator evaluator) {

        final Row headerRow = sheet.getRow(options.headerRowIndex());
        if (headerRow == null) {
            throw new IllegalArgumentException(
                    "Header row does not exist: "
                            + options.headerRowIndex());
        }

        final int finalCell = headerRow.getLastCellNum();
        if (finalCell <= 0) {
            throw new IllegalArgumentException(
                    "Header row is empty.");
        }

        final List<String> headers = new ArrayList<>(finalCell);
        final Set<String> uniqueHeaders = new HashSet<>();

        for (int cellIndex = 0;
                cellIndex < finalCell;
                cellIndex++) {

            final String header = format(
                    headerRow.getCell(
                            cellIndex,
                            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL),
                    formatter,
                    evaluator);

            if (header.isBlank()) {
                throw new IllegalArgumentException(
                        "Blank column heading at column " + cellIndex);
            }
            if (!uniqueHeaders.add(header)) {
                throw new IllegalArgumentException(
                        "Duplicate column heading: " + header);
            }

            headers.add(header);
        }

        return List.copyOf(headers);
    }

    private Map<String, String> readRow(
            final Row row,
            final List<String> headers,
            final DataFormatter formatter,
            final FormulaEvaluator evaluator) {

        final Map<String, String> values = new LinkedHashMap<>();

        for (int cellIndex = 0;
                cellIndex < headers.size();
                cellIndex++) {

            final Cell cell = row.getCell(
                    cellIndex,
                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

            values.put(
                    headers.get(cellIndex),
                    format(cell, formatter, evaluator));
        }

        return values;
    }

    private String format(
            final Cell cell,
            final DataFormatter formatter,
            final FormulaEvaluator evaluator) {

        if (cell == null) {
            return "";
        }

        final String value = evaluator == null
                ? formatter.formatCellValue(cell)
                : formatter.formatCellValue(cell, evaluator);

        return options.trimValues() ? value.trim() : value;
    }
}
