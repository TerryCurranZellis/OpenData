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
public class NesoCsv {

    private NesoCsv() {
    }

    /**
     * Returns the first mapped column from the aliases.
     *
     * @param r CSV record
     * @param aliases accepted header names
     * @return trimmed value
     */
    public static String field(CSVRecord r, String... aliases) {
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
    public static Double number(CSVRecord r, String... aliases) {
        var value = field(r, aliases);
        return value.isBlank() || value.equalsIgnoreCase("null") ? null : Double.valueOf(value.replace(",", ""));
    }

    /**
     * Parses a NESO date with the shared OpenData date utility.
     *
     * @param s date text
     * @return parsed date
     */
    public static LocalDate parseDate(String s) {
        return DateTimeParsers.parseDate(s);
    }

    /**
     * Parses a timestamp with the shared OpenData date utility. Local
     * date/times without a zone are interpreted as UTC.
     *
     * @param s timestamp text
     * @return parsed instant
     */
    public static Instant parseInstant(String s) {
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
    public static boolean includesYear(String resourceName, LocalDate start, LocalDate end) {
        for (var year = start.getYear(); year <= end.getYear(); year++) {
            if (resourceName.matches("(?s).*\\b" + year + "\\b.*")) {
                return true;
            }
        }
        return false;
    }
}
