/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Common date and timestamp parsing for OpenData providers.
 *
 * <p>
 * Built-in local-date formats are ISO ({@code 2023-01-01}), UK numeric
 * ({@code 01/01/2023}), English abbreviated month with four-digit year
 * ({@code 01-JAN-2009}), and English abbreviated month with two-digit year
 * ({@code 01-Jan-23}). Two-digit years are interpreted as 2000-2099. Numeric
 * month/day order is deliberately UK-first; ambiguous US dates must be parsed
 * using an explicitly supplied formatter.</p>
 *
 * <p>
 * All formatters are immutable and safe to reuse across plugin threads. Invalid
 * dates are rejected rather than silently corrected.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public class DateTimeParsers {

    private static final List<DateTimeFormatter> DEFAULT_DATE_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT),
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-MMM-uuuu")
                    .toFormatter(Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-MMM-uu")
                    .toFormatter(Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT)
    );

    private DateTimeParsers() {
        // Utility class.
    }

    /**
     * Parses a local date using the common supported formats, in order.
     *
     * @param text date text; surrounding whitespace is ignored
     * @return parsed date
     * @throws DateTimeParseException when no supported format matches
     */
    public static LocalDate parseDate(String text) {
        return parseDate(text, DEFAULT_DATE_FORMATTERS);
    }

    /**
     * Parses a local date using caller-supplied formatters, in order. Use this
     * when a provider has its own format or ambiguous numeric order.
     *
     * @param text date text; surrounding whitespace is ignored
     * @param formatters non-empty list of accepted formatters
     * @return parsed date
     * @throws DateTimeParseException when no formatter accepts the date
     * @throws IllegalArgumentException when the formatter list is empty
     */
    public static LocalDate parseDate(
            String text, List<DateTimeFormatter> formatters) {
        String value = Objects.requireNonNull(text, "text").trim();
        Objects.requireNonNull(formatters, "formatters");
        if (formatters.isEmpty()) {
            throw new IllegalArgumentException("formatters must not be empty");
        }
        DateTimeParseException lastFailure = null;
        for (var formatter : formatters) {
            try {
                return LocalDate.parse(value, Objects.requireNonNull(formatter, "formatter"));
            } catch (DateTimeParseException exception) {
                lastFailure = exception;
            }
        }
        throw new DateTimeParseException(
                "Unsupported or invalid date: " + value, value, 0, lastFailure);
    }

    /**
     * Parses an ISO instant, offset date/time, or ISO local date/time. Local
     * date/times with no offset are interpreted in UTC, matching the existing
     * electricity-plugin behaviour. A space may replace the T.
     *
     * @param text timestamp text
     * @return UTC instant
     * @throws DateTimeParseException when the value is not supported
     */
    public static Instant parseInstant(String text) {
        var value = Objects.requireNonNull(text, "text").trim();
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException exception) {
            // Try an ISO offset timestamp.
        }
        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (DateTimeParseException exception) {
            // Try a local timestamp, interpreted as UTC.
        }
        try {
            return LocalDateTime.parse(value.replace(' ', 'T'))
                    .toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException exception) {
            throw new DateTimeParseException(
                    "Unsupported or invalid timestamp: " + value,
                    value, 0, exception);
        }
    }

    /**
     * Formats a date in ISO-8601 ({@code uuuu-MM-dd}).
     *
     * @param date date to format
     * @return ISO date text
     */
    public static String formatDate(LocalDate date) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(
                Objects.requireNonNull(date, "date"));
    }

    /**
     * Formats a date with a caller-supplied formatter.
     *
     * @param date date to format
     * @param formatter formatter to use
     * @return formatted date
     */
    public static String formatDate(
            LocalDate date, DateTimeFormatter formatter) {
        return Objects.requireNonNull(formatter, "formatter")
                .format(Objects.requireNonNull(date, "date"));
    }
}
