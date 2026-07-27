/*
 * Filename: DataParserFactory.java
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
import java.nio.file.Path;
import java.util.Objects;

/**
 * Creates the framework parser appropriate to a downloaded file.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class DataParserFactory {

    /**
     * Prevents instantiation of this utility class.
     */
    private DataParserFactory() {
    }

    /**
     * Creates a parser using default format settings.
     *
     * @param file downloaded file
     * @return matching parser
     * @throws ImportException if the extension is unsupported
     */
    public static DataParser forFile(Path file) throws ImportException {
        Objects.requireNonNull(file, "file");
        try {
            return switch (DataFormat.fromPath(file)) {
                case CSV -> new CsvDataParser();
                case EXCEL -> new ExcelDataParser();
                case JSON -> new JsonDataParser();
            };
        } catch (IllegalArgumentException ex) {
            throw new ImportException(ex.getMessage(), ex);
        }
    }
}
