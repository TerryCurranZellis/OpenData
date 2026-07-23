/*
 * Filename: JsonDataParser.java
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towermarsh.opendata.exception.ImportException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Parser for JSON datasets.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class JsonDataParser
        implements DataParser {

    private final ObjectMapper mapper;

    public JsonDataParser() {

        mapper
                = new ObjectMapper();
    }

    @Override
    public List<Map<String, String>> parse(
            Path file)
            throws ImportException {

        try {

            return mapper.readValue(
                    file.toFile(),
                    new TypeReference<
                        List<Map<String, String>>>() {
            });

        } catch (IOException ex) {

            throw new ImportException(
                    "Unable to parse JSON file",
                    ex);
        }
    }
}
