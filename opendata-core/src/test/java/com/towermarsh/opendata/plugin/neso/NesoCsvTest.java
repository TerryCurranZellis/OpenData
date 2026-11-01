package com.towermarsh.opendata.plugin.neso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.Test;

class NesoCsvTest {

    @Test
    void dateFormats() {
        assertEquals(LocalDate.of(2025, 1, 1), NesoCsv.parseDate("01/01/2025"));
        assertEquals(LocalDate.of(2025, 1, 1), NesoCsv.parseDate("2025-01-01"));
    }

    @Test
    void timestampUtc() {
        assertEquals(Instant.parse("2025-01-01T00:30:00Z"), NesoCsv.parseInstant("2025-01-01T00:30:00+00:00"));
        assertEquals(Instant.parse("2025-01-01T00:30:00Z"), NesoCsv.parseInstant("2025-01-01 00:30:00"));
    }

    @Test
    void yearSelection() {
        assertTrue(NesoCsv.includesYear("Historic Demand Data 2025", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));
        assertFalse(NesoCsv.includesYear("Historic Demand Data 2024", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));
    }

    @Test
    void fieldsAndNumbers() throws Exception {
        try (var parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get()
                .parse(new StringReader("ND,TSD\n\"1,000.5\",\n"))) {
            var record = parser.iterator().next();
            assertEquals(1000.5, NesoCsv.number(record, "ND"));
            assertNull(NesoCsv.number(record, "TSD"));
            assertThrows(IllegalArgumentException.class, () -> NesoCsv.field(record, "MISSING"));
        }
    }
}
