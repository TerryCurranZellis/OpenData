/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import com.towermarsh.opendata.exception.ImportException;

import java.nio.file.Path;
import java.util.List;

/**
 * Defines a typed interface for parsing source files into records.
 *
 * @param <T> record type returned by this parser
 *
 * @author Terry Curran
 * @version 4.2.0
 *
 *
 */
public interface DataParser<T> {

    /**
     * Parses a data file.
     *
     * @param file input file
     * @return list of parsed records of type T
     * @throws ImportException if parsing fails
     */
    List<T> parse(
            Path file)
            throws ImportException;
}
