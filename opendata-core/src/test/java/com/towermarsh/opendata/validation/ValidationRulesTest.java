/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Tests reusable validation rules.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
class ValidationRulesTest {

    @Test
    void trimsTextAndChecksLength() {
        assertEquals("OpenData", ValidationRules.requireText(" OpenData ", "name", 20));
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidationRules.requireText("too long", "name", 3));
    }

    @Test
    void validatesRangesDurationsAndDateOrder() {
        assertEquals(500, ValidationRules.requireRange(500, 1, 10_000, "batchSize"));
        assertEquals(
                Duration.ofSeconds(30),
                ValidationRules.requirePositive(Duration.ofSeconds(30), "timeout"));
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidationRules.requireDateOrder(
                        LocalDate.of(2026, 8, 5),
                        LocalDate.of(2026, 8, 4),
                        "range"));
    }

    @Test
    void validatesAndQualifiesSqlIdentifiers() {
        assertEquals("[openmeteo].[DailyWeather]",
                SqlIdentifiers.qualify("openmeteo", "DailyWeather"));
        assertThrows(
                IllegalArgumentException.class,
                () -> SqlIdentifiers.requireSafe("core;drop table", "schema"));
    }
}
