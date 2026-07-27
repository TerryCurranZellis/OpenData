/*
 * Filename: CsvParserOptions.java
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
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record CsvParserOptions(
        Charset charset,
        char delimiter,
        boolean trim,
        boolean ignoreEmptyLines) {

    /** Validates and normalises record components. */
    public CsvParserOptions {
        Objects.requireNonNull(charset, "charset");
        if (delimiter == '\r' || delimiter == '\n' || delimiter == '\0') {
            throw new IllegalArgumentException("Invalid CSV delimiter");
        }
    }

    /**
     * Returns the default CSV parser options.
     *
     * @return default CSV parser options
     */
    public static CsvParserOptions defaults() {
        return new CsvParserOptions(StandardCharsets.UTF_8, ',', true, true);
    }
}
