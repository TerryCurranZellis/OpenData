package com.towermarsh.opendata.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExcelDataParserTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void parsesTextNumericAndFormulaCells() throws Exception {
        final Path file = temporaryDirectory.resolve("sample.xlsx");

        try (var workbook = new XSSFWorkbook();
             OutputStream output = Files.newOutputStream(file)) {

            final var sheet = workbook.createSheet("Data");
            final var header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("amount");
            header.createCell(2).setCellValue("doubleAmount");

            final var row = sheet.createRow(1);
            row.createCell(0).setCellValue("Example");
            row.createCell(1).setCellValue(12.5);
            row.createCell(2).setCellFormula("B2*2");

            workbook.write(output);
        }

        final var rows = new ExcelDataParser().parse(file);

        assertEquals(1, rows.size());
        assertEquals("Example", rows.get(0).get("name"));
        assertEquals("12.5", rows.get(0).get("amount"));
        assertEquals("25", rows.get(0).get("doubleAmount"));
    }
}
