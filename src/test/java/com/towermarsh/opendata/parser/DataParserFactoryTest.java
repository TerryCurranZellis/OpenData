/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.exception.ImportException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DataParserFactoryTest {

    @Test
    void selectsCsvAndExcelParsers() throws Exception {
        assertInstanceOf(CsvDataParser.class,
                DataParserFactory.forFile(Path.of("data.csv")));
        assertInstanceOf(ExcelDataParser.class,
                DataParserFactory.forFile(Path.of("data.xlsx")));
        assertInstanceOf(ExcelDataParser.class,
                DataParserFactory.forFile(Path.of("data.xls")));
    }

    @Test
    void rejectsUnsupportedExtension() {
        assertThrows(ImportException.class,
                () -> DataParserFactory.forFile(Path.of("data.pdf")));
    }
}
