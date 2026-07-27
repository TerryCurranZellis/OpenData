/*
 * Filename: DataFormat.java
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

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Tabular and structured file formats understood by the parser factory.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public enum DataFormat {
    CSV,
    EXCEL,
    JSON;

    /**
     * Determines a format from a filename extension.
     *
     * @param file source file
     * @return detected format
     */
    public static DataFormat fromPath(Path file) {
        Objects.requireNonNull(file, "file");
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".csv")) {
            return CSV;
        }
        if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
            return EXCEL;
        }
        if (name.endsWith(".json")) {
            return JSON;
        }
        throw new IllegalArgumentException("Unsupported data file format: " + file);
    }
}
