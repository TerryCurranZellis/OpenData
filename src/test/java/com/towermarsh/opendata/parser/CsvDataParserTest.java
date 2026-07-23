/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
