/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.parser;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Immutable CSV parser settings.
 *
 * @param charset source character set
 * @param delimiter field delimiter
 * @param trim whether surrounding whitespace is removed
 * @param ignoreEmptyLines whether empty lines are ignored
 */
public record CsvParserOptions(
        Charset charset,
        char delimiter,
        boolean trim,
        boolean ignoreEmptyLines) {

    public CsvParserOptions {
        Objects.requireNonNull(charset, "charset");
        if (delimiter == '\r' || delimiter == '\n' || delimiter == '\0') {
            throw new IllegalArgumentException("Invalid CSV delimiter");
        }
    }

    public static CsvParserOptions defaults() {
        return new CsvParserOptions(StandardCharsets.UTF_8, ',', true, true);
    }
}
