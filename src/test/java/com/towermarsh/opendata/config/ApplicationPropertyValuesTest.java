/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests typed application-property access. @version 2.1 */
class ApplicationPropertyValuesTest {

    @Test
    void parsesTypesCaseInsensitively() {
        final var values = new ApplicationPropertyValues(Map.of(
                "COUNT", " 12 ",
                "enabled", "yes",
                "timeout", "PT45S"));
        assertEquals(12, values.integer("count", 1));
        assertTrue(values.bool("ENABLED", false));
        assertEquals(Duration.ofSeconds(45), values.duration("timeout", Duration.ZERO));
        assertFalse(values.bool("missing", false));
    }

    @Test
    void rejectsInvalidTypedValues() {
        final var values = new ApplicationPropertyValues(Map.of("count", "twelve"));
        assertThrows(OpenDataConfigurationException.class, () -> values.integer("count", 1));
    }

    @Test
    void allowsBlankOptionalDefaultsButRequiresNamedValues() {
        final var values = new ApplicationPropertyValues(Map.of());
        assertEquals("", values.text("database.password", ""));
        assertThrows(OpenDataConfigurationException.class, () -> values.requiredText("database.url"));
    }
}
