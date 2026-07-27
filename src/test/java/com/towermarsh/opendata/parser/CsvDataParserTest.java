/*
 * Filename: CsvDataParserTest.java
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class CsvDataParserTest {

    @TempDir
    Path tempDirectory;

    @Test
    void parsesQuotedCommaAndMultilineField() throws Exception {
        Path file = tempDirectory.resolve("sample.csv");
        Files.writeString(file, """
                Name,Description,Value
                Alpha,"Contains, a comma",10
                Beta,"Line one
                line two",20
                """);

        List<Map<String, String>> records = new CsvDataParser().parse(file);

        assertEquals(2, records.size());
        assertEquals("Contains, a comma", records.get(0).get("Description"));
        assertEquals("Line one\nline two", records.get(1).get("Description"));
    }
}
