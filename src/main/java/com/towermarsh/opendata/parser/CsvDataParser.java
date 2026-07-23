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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser for comma separated files.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class CsvDataParser
        implements DataParser {

    @Override
    public List<Map<String, String>> parse(
            Path file)
            throws ImportException {

        try {

            List<String> lines
                    = Files.readAllLines(file);

            if (lines.isEmpty()) {
                return List.of();
            }

            String[] headers
                    = lines.get(0)
                            .split(",");

            return lines.stream()
                    .skip(1)
                    .map(line
                            -> createRecord(
                            headers,
                            line.split(",")))
                    .collect(Collectors.toList());

        } catch (IOException ex) {

            throw new ImportException(
                    "Unable to parse CSV file",
                    ex);
        }
    }

    private Map<String, String> createRecord(
            String[] headers,
            String[] values) {

        Map<String, String> record
                = new LinkedHashMap<>();

        for (int i = 0;
                i < headers.length;
                i++) {

            String value
                    = i < values.length
                            ? values[i]
                            : "";

            record.put(
                    headers[i],
                    value);
        }

        return record;
    }
}
