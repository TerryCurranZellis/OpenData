/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/** Verifies shared date/time conversions across providers. */
class DateTimeParsersTest {

    @Test
    void acceptsIsoUkAndCaseInsensitiveMonthNames() {
        var expected = LocalDate.of(2023, 1, 1);
        assertEquals(expected, DateTimeParsers.parseDate("2023-01-01"));
        assertEquals(expected, DateTimeParsers.parseDate("01/01/2023"));
        assertEquals(expected, DateTimeParsers.parseDate("01-JAN-2023"));
        assertEquals(expected, DateTimeParsers.parseDate("01-Jan-23"));
        assertEquals(expected, DateTimeParsers.parseDate(" 01-jan-23 "));
        assertEquals(LocalDate.of(2009, 1, 1), DateTimeParsers.parseDate("01-JAN-2009"));
        assertEquals(LocalDate.of(2024, 2, 29), DateTimeParsers.parseDate("29-FEB-2024"));
    }

    @Test
    void rejectsInvalidAndUnknownDates() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParsers.parseDate("31-FEB-2024"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParsers.parseDate("2023-02-29"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParsers.parseDate("12/31/2023"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParsers.parseDate(""));
    }

    @Test
    void allowsExplicitProviderSpecificFormatters() {
        var usFormat = DateTimeFormatter.ofPattern("MM/dd/uuuu", Locale.US);
        assertEquals(LocalDate.of(2023, 12, 31),
                DateTimeParsers.parseDate("12/31/2023", List.of(usFormat)));
        assertThrows(IllegalArgumentException.class,
                () -> DateTimeParsers.parseDate("01/01/2023", List.of()));
    }

    @Test
    void handlesInstantOffsetAndUtcLocalTimestamps() {
        var expected = Instant.parse("2025-01-01T00:30:00Z");
        assertEquals(expected, DateTimeParsers.parseInstant("2025-01-01T00:30:00Z"));
        assertEquals(expected, DateTimeParsers.parseInstant("2025-01-01T01:30:00+01:00"));
        assertEquals(expected, DateTimeParsers.parseInstant("2025-01-01 00:30:00"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParsers.parseInstant("nonsense"));
    }

    @Test
    void formatsIsoOrCustom() {
        var date = LocalDate.of(2023, 1, 1);
        assertEquals("2023-01-01", DateTimeParsers.formatDate(date));
        assertEquals("01/01/2023", DateTimeParsers.formatDate(date,
                DateTimeFormatter.ofPattern("dd/MM/uuuu")));
    }
}
