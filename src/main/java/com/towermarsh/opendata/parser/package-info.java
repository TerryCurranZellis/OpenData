/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Parsers that convert downloaded files into ordered string-keyed records.
 *
 * <p>The shared {@link com.towermarsh.opendata.parser.DataParser} contract is
 * retained for the initial framework phase. CSV is implemented with Apache
 * Commons CSV and Excel workbooks are implemented with Apache POI. Plugins may
 * supply format-specific option records when publisher files require a named
 * sheet, non-default delimiter or displaced header row.</p>
 */
package com.towermarsh.opendata.parser;
