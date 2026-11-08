/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.extract;

import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapLevel;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapPeriod;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapWorkbookData;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
import org.apache.poi.ss.util.CellReference;

/**
 * Extracts the levelised annual default-tariff-cap values from Ofgem Annex 9.
 *
 * <p>The extractor locates structural labels instead of relying solely on fixed
 * row numbers, while retaining the known C:J output-column meanings.</p>
  *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OfgemPriceCapWorkbookExtractor {

    /**
     *
     */
    public static final String OUTPUT_SHEET = "1a Levelised DTC";

    private static final DateTimeFormatter MONTH_YEAR =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("MMMM uuuu")
                    .toFormatter(Locale.UK);

    private static final Map<String, String> PAYMENT_METHODS = paymentMethods();
    private static final List<ColumnMeaning> OUTPUT_COLUMNS = List.of(
            new ColumnMeaning(2, "ELECTRICITY_SINGLE_RATE", "NIL"),
            new ColumnMeaning(3, "ELECTRICITY_SINGLE_RATE", "BENCHMARK"),
            new ColumnMeaning(4, "ELECTRICITY_MULTI_REGISTER", "NIL"),
            new ColumnMeaning(5, "ELECTRICITY_MULTI_REGISTER", "BENCHMARK"),
            new ColumnMeaning(6, "GAS", "NIL"),
            new ColumnMeaning(7, "GAS", "BENCHMARK"),
            new ColumnMeaning(8, "DUAL_FUEL", "NIL"),
            new ColumnMeaning(9, "DUAL_FUEL", "BENCHMARK"));

    /**
     * Extracts a typed period and all price-cap-level cells.
     *
     * @param workbookFile Ofgem workbook
     * @return extracted data
     * @throws ImportException if the workbook structure or values are invalid
     */
    public OfgemPriceCapWorkbookData extract(Path workbookFile)
            throws ImportException {
        Objects.requireNonNull(workbookFile, "workbookFile");
        try (InputStream input = Files.newInputStream(workbookFile);
                Workbook workbook = WorkbookFactory.create(input)) {
            Sheet sheet = workbook.getSheet(OUTPUT_SHEET);
            if (sheet == null) {
                throw new ImportException(
                        "Ofgem workbook does not contain worksheet: " + OUTPUT_SHEET);
            }

            DataFormatter formatter = new DataFormatter(Locale.UK);
            FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            String periodName = findPeriodName(sheet, formatter, evaluator);
            OfgemPriceCapPeriod period = parsePeriod(
                    periodName,
                    findSourceColumnReference(sheet, formatter, evaluator));
            List<OfgemPriceCapLevel> levels = new ArrayList<>();

            for (Map.Entry<String, String> payment : PAYMENT_METHODS.entrySet()) {
                int sectionRow = findRow(sheet, payment.getKey(), formatter, evaluator);
                int regionHeaderRow = findFollowingRow(
                        sheet,
                        sectionRow,
                        "Charge Restriction Region",
                        formatter,
                        evaluator);
                extractSection(
                        sheet,
                        regionHeaderRow + 2,
                        payment.getValue(),
                        formatter,
                        evaluator,
                        levels);
            }

            if (levels.isEmpty()) {
                throw new ImportException("No Ofgem price-cap levels were extracted");
            }
            return new OfgemPriceCapWorkbookData(period, levels);
        } catch (ImportException exception) {
            throw exception;
        } catch (IOException | RuntimeException exception) {
            throw new ImportException(
                    "Unable to extract Ofgem workbook: " + workbookFile,
                    exception);
        }
    }

    /**
     * Extract section.
     *
     * @param sheet input sheet
     * @param firstDataRow input first data row
     * @param paymentMethodCode input payment method code
     * @param formatter input formatter
     * @param evaluator input evaluator
     * @param destination input destination
     * @throws ImportException if extract section fails
     */
    private static void extractSection(
            Sheet sheet,
            int firstDataRow,
            String paymentMethodCode,
            DataFormatter formatter,
            FormulaEvaluator evaluator,
            List<OfgemPriceCapLevel> destination) throws ImportException {
        for (int rowIndex = firstDataRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            String regionText = value(row, 1, formatter, evaluator).trim();
            if (regionText.isBlank()) {
                return;
            }

            boolean vatIncluded = regionText.toLowerCase(Locale.ROOT)
                    .contains("inc vat");
            String regionCode = regionCode(regionText);
            for (ColumnMeaning meaning : OUTPUT_COLUMNS) {
                Cell cell = row == null ? null : row.getCell(meaning.columnIndex());
                String formatted = format(cell, formatter, evaluator).trim();
                if (formatted.isBlank() || formatted.equals("-")) {
                    continue;
                }
                BigDecimal amount = parseAmount(formatted, cell);
                destination.add(new OfgemPriceCapLevel(
                        regionCode,
                        paymentMethodCode,
                        meaning.tariffTypeCode(),
                        meaning.consumptionBasisCode(),
                        amount,
                        vatIncluded,
                        OUTPUT_SHEET,
                        new CellReference(rowIndex, meaning.columnIndex())
                                .formatAsString()));
            }
        }
    }

    /**
     * Returns find period name.
     *
     * @param sheet input sheet
     * @param formatter input formatter
     * @param evaluator input evaluator
     * @return result of find period name
     * @throws ImportException if find period name fails
     */
    private static String findPeriodName(
            Sheet sheet,
            DataFormatter formatter,
            FormulaEvaluator evaluator) throws ImportException {
        int rowIndex = findRow(
                sheet,
                "28AD Charge Restriction Period:",
                formatter,
                evaluator);
        String periodName = value(sheet.getRow(rowIndex), 2, formatter, evaluator).trim();
        if (periodName.isBlank()) {
            throw new ImportException("Ofgem charge-restriction period is blank");
        }
        return periodName;
    }

    /**
     * Parses or formats period.
     *
     * @param periodName requested period name
     * @param sourceColumnReference input source column reference
     * @return result of parse period
     * @throws ImportException if parse period fails
     */
    private static OfgemPriceCapPeriod parsePeriod(
            String periodName,
            Integer sourceColumnReference) throws ImportException {
        String[] parts = periodName.split("\\s+-\\s+", 2);
        if (parts.length != 2) {
            throw new ImportException("Unrecognised Ofgem period: " + periodName);
        }
        try {
            YearMonth firstMonth = YearMonth.parse(parts[0].trim(), MONTH_YEAR);
            YearMonth lastMonth = YearMonth.parse(parts[1].trim(), MONTH_YEAR);
            LocalDate from = firstMonth.atDay(1);
            LocalDate to = lastMonth.atEndOfMonth();
            return new OfgemPriceCapPeriod(
                    periodName, from, to, sourceColumnReference, true);
        } catch (DateTimeParseException exception) {
            throw new ImportException("Unrecognised Ofgem period: " + periodName, exception);
        }
    }

    /**
     * Returns find source column reference.
     *
     * @param sheet input sheet
     * @param formatter input formatter
     * @param evaluator input evaluator
     * @return result of find source column reference
     * @throws ImportException if find source column reference fails
     */
    private static Integer findSourceColumnReference(
            Sheet sheet,
            DataFormatter formatter,
            FormulaEvaluator evaluator) throws ImportException {
        String marker = "Column reference, current charging period:";
        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            int lastColumn = Math.max(0, row.getLastCellNum());
            for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
                String text = value(row, columnIndex, formatter, evaluator).trim();
                if (!text.equalsIgnoreCase(marker)) {
                    continue;
                }
                String reference = value(
                        row, columnIndex + 1, formatter, evaluator).trim();
                if (reference.isBlank()) {
                    return null;
                }
                try {
                    return Integer.valueOf(reference.replace(",", ""));
                } catch (NumberFormatException exception) {
                    throw new ImportException(
                            "Invalid current Ofgem column reference: " + reference,
                            exception);
                }
            }
        }
        return null;
    }

    /**
     * Returns find following row.
     *
     * @param sheet input sheet
     * @param afterRow input after row
     * @param expectedText input expected text
     * @param formatter input formatter
     * @param evaluator input evaluator
     * @return result of find following row
     * @throws ImportException if find following row fails
     */
    private static int findFollowingRow(
            Sheet sheet,
            int afterRow,
            String expectedText,
            DataFormatter formatter,
            FormulaEvaluator evaluator) throws ImportException {
        for (int rowIndex = afterRow + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            if (value(sheet.getRow(rowIndex), 1, formatter, evaluator)
                    .trim().equalsIgnoreCase(expectedText)) {
                return rowIndex;
            }
        }
        throw new ImportException(
                "Unable to locate '" + expectedText + "' after row " + (afterRow + 1));
    }

    /**
     * Returns find row.
     *
     * @param sheet input sheet
     * @param expectedText input expected text
     * @param formatter input formatter
     * @param evaluator input evaluator
     * @return result of find row
     * @throws ImportException if find row fails
     */
    private static int findRow(
            Sheet sheet,
            String expectedText,
            DataFormatter formatter,
            FormulaEvaluator evaluator) throws ImportException {
        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            if (value(sheet.getRow(rowIndex), 1, formatter, evaluator)
                    .trim().equalsIgnoreCase(expectedText)) {
                return rowIndex;
            }
        }
        throw new ImportException("Unable to locate Ofgem label: " + expectedText);
    }

    /**
     * Value.
     *
     * @param row input row
     * @param columnIndex input column index
     * @param formatter input formatter
     * @param evaluator input evaluator
     * @return result of value
     */
    private static String value(
            Row row,
            int columnIndex,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        return format(row == null ? null : row.getCell(columnIndex), formatter, evaluator);
    }

    /**
     * Format.
     *
     * @param cell input cell
     * @param formatter input formatter
     * @param evaluator input evaluator
     * @return result of format
     */
    private static String format(
            Cell cell,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        return cell == null ? "" : formatter.formatCellValue(cell, evaluator);
    }

    /**
     * Parses or formats amount.
     *
     * @param formatted input formatted
     * @param cell input cell
     * @return result of parse amount
     * @throws ImportException if parse amount fails
     */
    private static BigDecimal parseAmount(String formatted, Cell cell)
            throws ImportException {
        String normalised = formatted
                .replace("£", "")
                .replace(",", "")
                .trim();
        try {
            return new BigDecimal(normalised);
        } catch (NumberFormatException exception) {
            String location = cell == null
                    ? "unknown cell"
                    : cell.getAddress().formatAsString();
            throw new ImportException(
                    "Invalid Ofgem monetary value at " + location + ": " + formatted,
                    exception);
        }
    }

    /**
     * Region code.
     *
     * @param displayName requested display name
     * @return result of region code
     * @throws ImportException if region code fails
     */
    private static String regionCode(String displayName) throws ImportException {
        String normalised = displayName.trim().toLowerCase(Locale.ROOT);
        if (normalised.startsWith("gb average")) {
            return "GB_AVERAGE";
        }
        return switch (normalised) {
            case "north west" -> "NORTH_WEST";
            case "northern" -> "NORTHERN";
            case "yorkshire" -> "YORKSHIRE";
            case "northern scotland" -> "NORTHERN_SCOTLAND";
            case "southern" -> "SOUTHERN";
            case "southern scotland" -> "SOUTHERN_SCOTLAND";
            case "n wales and mersey" -> "N_WALES_AND_MERSEY";
            case "london" -> "LONDON";
            case "south east" -> "SOUTH_EAST";
            case "eastern" -> "EASTERN";
            case "east midlands" -> "EAST_MIDLANDS";
            case "midlands" -> "MIDLANDS";
            case "southern western" -> "SOUTHERN_WESTERN";
            case "south wales" -> "SOUTH_WALES";
            default -> throw new ImportException(
                    "Unrecognised Ofgem charge-restriction region: " + displayName);
        };
    }

    /**
     * Payment methods.
     *
     * @return result of payment methods
     */
    private static Map<String, String> paymentMethods() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("Other Payment Method", "OTHER");
        result.put("Standard Credit", "STANDARD_CREDIT");
        result.put("PPM", "PPM");
        return Map.copyOf(result);
    }

    /**
     * Creates a column meaning instance.
     *
     * @param columnIndex input column index
     * @param tariffTypeCode input tariff type code
     * @param consumptionBasisCode input consumption basis code
     */
    private record ColumnMeaning(
            int columnIndex,
            String tariffTypeCode,
            String consumptionBasisCode) {
    }
}
