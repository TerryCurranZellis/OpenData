/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import com.towermarsh.opendata.exception.ImportException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Apache Commons CSV implementation of {@link DataParser}.
 *
 * <p>The first record supplies column names. Quoted delimiters, escaped quotes
 * and multiline fields are handled by the library rather than by splitting
 * physical lines.</p>
 */
public final class CsvDataParser implements DataParser {

    private final CsvParserOptions options;

    public CsvDataParser() {
        this(CsvParserOptions.defaults());
    }

    public CsvDataParser(CsvParserOptions options) {
        this.options = Objects.requireNonNull(options, "options");
    }

    @Override
    public List<Map<String, String>> parse(Path file) throws ImportException {
        Objects.requireNonNull(file, "file");
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(options.delimiter())
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(options.trim())
                .setIgnoreEmptyLines(options.ignoreEmptyLines())
                .get();

        try (Reader reader = Files.newBufferedReader(file, options.charset());
                CSVParser parser = format.parse(reader)) {
            List<Map<String, String>> records = new ArrayList<>();
            for (CSVRecord csvRecord : parser) {
                Map<String, String> record = new LinkedHashMap<>();
                for (String header : parser.getHeaderNames()) {
                    record.put(header, csvRecord.isMapped(header)
                            ? csvRecord.get(header) : "");
                }
                records.add(record);
            }
            return List.copyOf(records);
        } catch (IOException | IllegalArgumentException ex) {
            throw new ImportException("Unable to parse CSV file: " + file, ex);
        }
    }
}
