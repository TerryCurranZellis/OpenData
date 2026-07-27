/*
 * Filename: CsvDataParser.java
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
 * <p>
 * The first record supplies column names. Quoted delimiters, escaped quotes and
 * multiline fields are handled by the library rather than by splitting physical
 * lines.</p>
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class CsvDataParser implements DataParser {

    private final CsvParserOptions options;

    /**
     * Creates a CSV parser using default options.
     */
    public CsvDataParser() {
        this(CsvParserOptions.defaults());
    }

    /**
     * Creates a CSV parser using the supplied options.
     *
     * @param options CSV parser options
     */
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

        try (Reader reader = Files.newBufferedReader(file, options.charset()); CSVParser parser = format.parse(reader)) {
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
