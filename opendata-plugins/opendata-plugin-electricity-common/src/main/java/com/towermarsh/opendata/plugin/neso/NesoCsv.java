/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.neso;

import com.towermarsh.opendata.util.DateTimeParsers;
import java.time.Instant;
import java.time.LocalDate;
import org.apache.commons.csv.CSVRecord;

/**
 * Parsing helpers for NESO CSV files, which use several header and date styles.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class NesoCsv {

    private NesoCsv() {
    }

    /**
     * Returns the first mapped column from the aliases.
     *
     * @param r CSV record
     * @param aliases accepted header names
     * @return trimmed value
     */
    public static String field(final CSVRecord r, final String... aliases) {
        for (var s : aliases) {
            if (r.isMapped(s)) {
                return r.get(s).trim();
            }
        }
        throw new IllegalArgumentException("Missing source column: " + String.join(" / ", aliases));
    }

    /**
     * Reads a nullable number.
     *
     * @param r CSV record
     * @param aliases accepted header names
     * @return number, or {@code null} when blank
     */
    public static Double number(final CSVRecord r, final String... aliases) {
        final var value = field(r, aliases);
        return value.isBlank() || value.equalsIgnoreCase("null") ? null : Double.valueOf(value.replace(",", ""));
    }

    /**
     * Parses a NESO date with the shared OpenData date utility.
     *
     * @param s date text
     * @return parsed date
     */
    public static LocalDate parseDate(final String s) {
        return DateTimeParsers.parseDate(s);
    }

    /**
     * Parses a timestamp with the shared OpenData date utility.
     * Local date/times without a zone are interpreted as UTC.
     *
     * @param s timestamp text
     * @return parsed instant
     */
    public static Instant parseInstant(final String s) {
        return DateTimeParsers.parseInstant(s);
    }

    /**
     * Tests whether a resource name mentions any year in the range.
     *
     * @param resourceName resource name
     * @param start first date
     * @param end last date
     * @return true when a year matches
     */
    public static boolean includesYear(final String resourceName, final LocalDate start, final LocalDate end) {
        for (var year = start.getYear(); year <= end.getYear(); year++) {
            if (resourceName.matches("(?s).*\\b" + year + "\\b.*")) {
                return true;
            }
        }
        return false;
    }
}
