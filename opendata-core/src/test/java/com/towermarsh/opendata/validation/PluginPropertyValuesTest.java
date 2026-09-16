/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyType;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests shared typed plugin-property parsing.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
class PluginPropertyValuesTest {

    @Test
    void parsesSupportedCommonTypes() {
        final PluginPropertyValues values = new PluginPropertyValues(definition(Map.of(
                "count", property("count", "42", PluginPropertyType.INTEGER),
                "enabled", property("enabled", "yes", PluginPropertyType.BOOLEAN),
                "timeout", property("timeout", "PT30S", PluginPropertyType.DURATION),
                "date", property("date", "2026-08-04", PluginPropertyType.STRING))));

        assertEquals(42, values.integer("count", 0));
        assertTrue(values.booleanValue("enabled", false));
        assertEquals(Duration.ofSeconds(30), values.duration("timeout", Duration.ZERO));
        assertEquals(LocalDate.of(2026, 8, 4), values.requiredDate("date"));
        assertFalse(values.booleanValue("missing", false));
    }

    @Test
    void reportsPluginAndPropertyWithoutExposingInvalidValue() {
        final PluginPropertyValues values = new PluginPropertyValues(definition(Map.of(
                "enabled", property("enabled", "secret-invalid-value", PluginPropertyType.BOOLEAN))));

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> values.booleanValue("enabled", false));

        assertTrue(exception.getMessage().contains("Plugin 'example'"));
        assertTrue(exception.getMessage().contains("property 'enabled'"));
        assertFalse(exception.getMessage().contains("secret-invalid-value"));
    }

    private static PluginDefinition definition(
            final Map<String, PluginPropertyDefinition> properties) {
        return new PluginDefinition(
                "example",
                "Example",
                "",
                "example.Plugin",
                true,
                1,
                "example-data",
                List.of(),
                properties,
                Map.of());
    }

    private static PluginPropertyDefinition property(
            final String name,
            final String value,
            final PluginPropertyType type) {
        return new PluginPropertyDefinition(name, value, type, false, "");
    }
}
