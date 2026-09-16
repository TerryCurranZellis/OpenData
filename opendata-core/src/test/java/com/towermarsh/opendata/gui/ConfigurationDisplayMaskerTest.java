/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests masking of sensitive values before they reach JavaFX dialogs.
 *
 * @author Terry Curran
 * @version 3.0.0
 */
class ConfigurationDisplayMaskerTest {

    @Test
    void masksValueWhenPluginDefinitionMarksPropertySensitive() {
        final var values = Map.of(
                "property.api.value", "super-secret-value",
                "property.api.sensitive", "true",
                "property.api.description", "API credential");

        final var entries = ConfigurationDisplayMasker.entries(values);

        assertEquals(ConfigurationDisplayMasker.MASKED_VALUE,
                value(entries, "property.api.value"));
        assertEquals("true", value(entries, "property.api.sensitive"));
    }

    @Test
    void masksConventionalSecretPropertyNamesButNotOrdinaryKeyNames() {
        final var values = Map.of(
                "service.token", "abc",
                "property.location-key.value", "home",
                "database.password", "password");

        assertTrue(ConfigurationDisplayMasker.shouldMask("service.token", values));
        assertTrue(ConfigurationDisplayMasker.shouldMask("database.password", values));
        assertFalse(ConfigurationDisplayMasker.shouldMask(
                "property.location-key.value", values));
    }

    private static String value(
            final java.util.List<ConfigurationDisplayEntry> entries,
            final String property) {
        return entries.stream()
                .filter(entry -> entry.property().equals(property))
                .findFirst()
                .orElseThrow()
                .value();
    }
}
