/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 1.0.0
 */
class OverrideConfigurationTest {
    @Test
    void singlePluginRunAcceptsUnscopedPluginProperties() throws Exception {
        final var file = Files.createTempFile("opendata-single-plugin", ".properties");
        try {
            Files.writeString(file, """
                    application.database.password=secret
                    property.start-date.value=2025-01-01
                    """);
            final var configuration = OverrideConfiguration.load(Optional.of(file));

            assertEquals("secret", configuration.applicationValues().get("database.password"));
            assertEquals("2025-01-01",
                    configuration.pluginValues("openmeteo", false).get("property.start-date.value"));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void multiPluginRunRejectsUnscopedPluginProperties() throws Exception {
        final var file = Files.createTempFile("opendata-multi-plugin", ".properties");
        try {
            Files.writeString(file, "property.start-date.value=2025-01-01\n");
            final var configuration = OverrideConfiguration.load(Optional.of(file));

            assertThrows(OpenDataConfigurationException.class,
                    () -> configuration.pluginValues("openmeteo", true));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
