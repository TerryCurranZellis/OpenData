/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.neso;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
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
     * Parses NESO dates in the following formats:
     *
     * ISO: 2009-01-01 UK numeric: 01/01/2009 NESO historic: 01-JAN-2009
     *
     * Month names are case-insensitive.
     *
     * @param s date text
     * @return parsed date
     */
    public static LocalDate parseDate(String s) {
        var value = s.trim();
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            // Try UK numeric format.
        }
        try {
            return LocalDate.parse(
                    value,
                    DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        } catch (DateTimeParseException ex) {
            // Try NESO historic format.
        }

        var historicFormat
                = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("dd-MMM-uuuu")
                        .toFormatter(Locale.ENGLISH);

        return LocalDate.parse(value, historicFormat);
    }

    /**
     * Parses an instant in ISO, offset or local (UTC) form.
     *
     * @param s text
     * @return instant
     */
    public static Instant parseInstant(String s) {
        try {
            return Instant.parse(s);
        } catch (DateTimeParseException ex) {
            try {
                return OffsetDateTime.parse(s).toInstant();
            } catch (DateTimeParseException ex2) {
                return LocalDateTime.parse(s.replace(' ', 'T')).toInstant(ZoneOffset.UTC);
            }
        }
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
