/*
 * Filename: ExcelDataParserTest.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class ExcelDataParserTest {

    @TempDir
    Path tempDirectory;

    @Test
    void parsesNamedSheetAndEvaluatesFormula() throws Exception {
        Path file = tempDirectory.resolve("sample.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Amount");
            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue("Alpha");
            data.createCell(1).setCellFormula("10+5");
            try (OutputStream output = Files.newOutputStream(file)) {
                workbook.write(output);
            }
        }

        ExcelParserOptions options = new ExcelParserOptions(
                "Data", 0, 0, 1, true, true);
        List<Map<String, String>> records = new ExcelDataParser(options).parse(file);

        assertEquals(1, records.size());
        assertEquals("Alpha", records.get(0).get("Name"));
        assertEquals("15", records.get(0).get("Amount"));
    }

    @Test
    void makesBlankAndDuplicateHeadersUsable() throws Exception {
        Path file = tempDirectory.resolve("headers.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("");
            header.createCell(2).setCellValue("Name");
            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue("A");
            data.createCell(1).setCellValue("B");
            data.createCell(2).setCellValue("C");
            try (OutputStream output = Files.newOutputStream(file)) {
                workbook.write(output);
            }
        }

        Map<String, String> record = new ExcelDataParser().parse(file).get(0);
        assertEquals(List.of("Name", "COLUMN_2", "Name_2"),
                List.copyOf(record.keySet()));
    }
}
