/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.plugin.ofgem.extract.OfgemPriceCapWorkbookExtractor;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author Terry Curran
 * @version 1.0.0
 */
class OfgemPriceCapWorkbookExtractorTest {

    @TempDir
    Path tempDirectory;

    @Test
    void extractsPeriodAndAllMappedDimensions() throws Exception {
        Path workbookFile = tempDirectory.resolve("annex-9.xlsx");
        createWorkbook(workbookFile);

        OfgemPriceCapWorkbookData result =
                new OfgemPriceCapWorkbookExtractor().extract(workbookFile);

        assertEquals(LocalDate.of(2026, 7, 1), result.period().effectiveFrom());
        assertEquals(LocalDate.of(2026, 9, 30), result.period().effectiveTo());
        assertEquals(48, result.levels().size());
        assertTrue(result.levels().stream().anyMatch(level ->
                level.paymentMethodCode().equals("PPM")
                && level.regionCode().equals("GB_AVERAGE")
                && level.tariffTypeCode().equals("GAS")
                && level.consumptionBasisCode().equals("BENCHMARK")
                && level.vatIncluded()));
    }

    private static void createWorkbook(Path target) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(OfgemPriceCapWorkbookExtractor.OUTPUT_SHEET);
            Row period = sheet.createRow(5);
            period.createCell(1).setCellValue("28AD Charge Restriction Period:");
            period.createCell(2).setCellValue("July 2026 - September 2026");

            int rowIndex = 8;
            rowIndex = addSection(sheet, rowIndex, "Other Payment Method", 100.0);
            rowIndex = addSection(sheet, rowIndex, "Standard Credit", 200.0);
            addSection(sheet, rowIndex, "PPM", 300.0);

            try (OutputStream output = Files.newOutputStream(target)) {
                workbook.write(output);
            }
        }
    }

    private static int addSection(
            Sheet sheet,
            int startRow,
            String title,
            double baseValue) {
        sheet.createRow(startRow).createCell(1).setCellValue(title);
        sheet.createRow(startRow + 1);
        Row header = sheet.createRow(startRow + 2);
        header.createCell(1).setCellValue("Charge Restriction Region");
        sheet.createRow(startRow + 3);

        Row region = sheet.createRow(startRow + 4);
        region.createCell(1).setCellValue("North West");
        fillValues(region, baseValue);

        Row vatAverage = sheet.createRow(startRow + 5);
        vatAverage.createCell(1).setCellValue("GB average, inc VAT (at 5%)");
        fillValues(vatAverage, baseValue + 50.0);

        sheet.createRow(startRow + 6);
        return startRow + 8;
    }

    private static void fillValues(Row row, double baseValue) {
        for (int column = 2; column <= 9; column++) {
            row.createCell(column).setCellValue(baseValue + column);
        }
    }
}
