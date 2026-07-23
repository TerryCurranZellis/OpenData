package com.towermarsh.opendata.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvDataParserTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void parsesQuotedCommaEmbeddedNewlineAndEscapedQuote()
            throws Exception {

        final Path file = temporaryDirectory.resolve("advanced.csv");
        Files.writeString(file, """
                id,name,notes,optional
                1,"Smith, Jane","Line one
                Line two",
                2,"O'Brien","He said ""hello"",present
                """);

        final var rows = new CsvDataParser().parse(file);

        assertEquals(2, rows.size());
        assertEquals("Smith, Jane", rows.get(0).get("name"));
        assertEquals(
                "Line one\nLine two",
                rows.get(0).get("notes"));
        assertEquals("", rows.get(0).get("optional"));
        assertEquals(
                "He said \"hello\"",
                rows.get(1).get("notes"));
    }
}
