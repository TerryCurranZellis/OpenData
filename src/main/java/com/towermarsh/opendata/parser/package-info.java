/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Framework data parsers and parser configuration options.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link CsvDataParser} &mdash; Apache Commons CSV implementation of {@link DataParser}.</li>
 * <li>{@link DataParserFactory} &mdash; Creates the framework parser appropriate to a downloaded file.</li>
 * <li>{@link ExcelDataParser} &mdash; Parser for Excel `.xls` and `.xlsx` workbooks.</li>
 * <li>{@link JsonDataParser} &mdash; Parser for JSON datasets.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link CsvParserOptions} &mdash; Immutable CSV parser settings.</li>
 * <li>{@link ExcelParserOptions} &mdash; Immutable Excel parser settings.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link DataParser} &mdash; Defines a common interface for data parsers.</li>
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link DataFormat} &mdash; Tabular and structured file formats understood by the parser factory.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.parser;
