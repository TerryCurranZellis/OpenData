/*
 * Filename: DatasetFormat.java
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

package com.towermarsh.opendata.config.model;

/**
 * Data formats understood by the OpenData Framework.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public enum DatasetFormat {
    CSV,
    JSON,
    XML,
    XLS,
    XLSX,
    HTML,
    ZIP,
    TEXT,
    BINARY;

    /**
     * Parses a case-insensitive format name.
     *
     * @param value configured value
     * @return matching format
     */
    public static DatasetFormat parse(final String value) {
        return Enum.valueOf(
                DatasetFormat.class,
                value.trim().replace('-', '_').toUpperCase(java.util.Locale.ROOT));
    }
}
