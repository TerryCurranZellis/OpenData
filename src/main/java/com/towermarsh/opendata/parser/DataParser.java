/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import com.towermarsh.opendata.exception.ImportException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Defines a common interface for data parsers.
 *
 * @author Terry Curran
 * @version 17 July 2026
 *
 *
 */
public interface DataParser {

    /**
     * Parses a data file.
     *
     * @param file input file
     * @return list of records
     * @throws ImportException if parsing fails
     */
    List<Map<String, String>> parse(
            Path file)
            throws ImportException;
}
