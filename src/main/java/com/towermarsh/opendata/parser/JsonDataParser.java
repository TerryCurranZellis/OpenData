/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
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
 * @version 17 July 2026
 */
public final class JsonDataParser
        implements DataParser {

    private final ObjectMapper mapper;

    /**
     * Creates a JSON parser backed by a default Jackson object mapper.
     */
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
