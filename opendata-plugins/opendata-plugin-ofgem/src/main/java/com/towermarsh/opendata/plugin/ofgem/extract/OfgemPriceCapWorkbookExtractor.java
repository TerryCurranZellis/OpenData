/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.extract;

import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapLevel;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapPeriod;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPaymentMethod;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemRegion;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemTariffType;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemConsumptionBasis;
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
import java.util.List;
import java.util.Locale;
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
 * @version 4.2.0
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

    private static final List<ColumnMeaning> OUTPUT_COLUMNS = List.of(
            new ColumnMeaning(2, OfgemTariffType.ELECTRICITY_SINGLE_RATE, OfgemConsumptionBasis.NIL),
            new ColumnMeaning(3, OfgemTariffType.ELECTRICITY_SINGLE_RATE, OfgemConsumptionBasis.BENCHMARK),
            new ColumnMeaning(4, OfgemTariffType.ELECTRICITY_MULTI_REGISTER, OfgemConsumptionBasis.NIL),
            new ColumnMeaning(5, OfgemTariffType.ELECTRICITY_MULTI_REGISTER, OfgemConsumptionBasis.BENCHMARK),
            new ColumnMeaning(6, OfgemTariffType.GAS, OfgemConsumptionBasis.NIL),
            new ColumnMeaning(7, OfgemTariffType.GAS, OfgemConsumptionBasis.BENCHMARK),
            new ColumnMeaning(8, OfgemTariffType.DUAL_FUEL, OfgemConsumptionBasis.NIL),
            new ColumnMeaning(9, OfgemTariffType.DUAL_FUEL, OfgemConsumptionBasis.BENCHMARK));

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
        try (var input = Files.newInputStream(workbookFile);
                var workbook = WorkbookFactory.create(input)) {
            var sheet = workbook.getSheet(OUTPUT_SHEET);
            if (sheet == null) {
                throw new ImportException(
                        "Ofgem workbook does not contain worksheet: " + OUTPUT_SHEET);
            }

            var formatter = new DataFormatter(Locale.UK);
            var evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            var periodName = findPeriodName(sheet, formatter, evaluator);
            var period = parsePeriod(
                    periodName,
                    findSourceColumnReference(sheet, formatter, evaluator));
            var levels = new ArrayList<OfgemPriceCapLevel>();

            for (var payment : OfgemPaymentMethod.values()) {
                var sectionRow = findRow(sheet, payment.heading(), formatter, evaluator);
                var regionHeaderRow = findFollowingRow(
                        sheet,
                        sectionRow,
                        "Charge Restriction Region",
                        formatter,
                        evaluator);
                extractSection(
                        sheet,
                        regionHeaderRow + 2,
                        payment.code(),
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
        for (var rowIndex = firstDataRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            var row = sheet.getRow(rowIndex);
            var regionText = value(row, 1, formatter, evaluator).trim();
            if (regionText.isBlank()) {
                return;
            }

            var vatIncluded = regionText.toLowerCase(Locale.ROOT)
                    .contains("inc vat");
            var regionCode = OfgemRegion.fromDisplayName(regionText).code();
            for (var meaning : OUTPUT_COLUMNS) {
                var cell = row == null ? null : row.getCell(meaning.columnIndex());
                var formatted = format(cell, formatter, evaluator).trim();
                if (formatted.isBlank() || formatted.equals("-")) {
                    continue;
                }
                var amount = parseAmount(formatted, cell);
                destination.add(new OfgemPriceCapLevel(
                        regionCode,
                        paymentMethodCode,
                        meaning.tariffType().name(),
                        meaning.consumptionBasis().name(),
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
        var rowIndex = findRow(
                sheet,
                "28AD Charge Restriction Period:",
                formatter,
                evaluator);
        var periodName = value(sheet.getRow(rowIndex), 2, formatter, evaluator).trim();
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
        var parts = periodName.split("\\s+-\\s+", 2);
        if (parts.length != 2) {
            throw new ImportException("Unrecognised Ofgem period: " + periodName);
        }
        try {
            var firstMonth = YearMonth.parse(parts[0].trim(), MONTH_YEAR);
            var lastMonth = YearMonth.parse(parts[1].trim(), MONTH_YEAR);
            var from = firstMonth.atDay(1);
            var to = lastMonth.atEndOfMonth();
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
        var marker = "Column reference, current charging period:";
        for (var rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            var row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            var lastColumn = Math.max(0, row.getLastCellNum());
            for (var columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
                var text = value(row, columnIndex, formatter, evaluator).trim();
                if (!text.equalsIgnoreCase(marker)) {
                    continue;
                }
                var reference = value(
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
        for (var rowIndex = afterRow + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
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
        for (var rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
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
        var normalised = formatted
                .replace("£", "")
                .replace(",", "")
                .trim();
        try {
            return new BigDecimal(normalised);
        } catch (NumberFormatException exception) {
            var location = cell == null
                    ? "unknown cell"
                    : cell.getAddress().formatAsString();
            throw new ImportException(
                    "Invalid Ofgem monetary value at " + location + ": " + formatted,
                    exception);
        }
    }

    /**
     * Creates a column meaning instance.
     *
     * @param columnIndex input column index
     * @param tariffType input tariff type
     * @param consumptionBasis input consumption basis
     */
    private record ColumnMeaning(
            int columnIndex,
            OfgemTariffType tariffType,
            OfgemConsumptionBasis consumptionBasis) {
    }
}
