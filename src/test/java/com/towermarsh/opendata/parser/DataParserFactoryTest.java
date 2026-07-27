/*
 * Filename: DataParserFactoryTest.java
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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.exception.ImportException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
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
