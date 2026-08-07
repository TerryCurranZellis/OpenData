/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests runtime configuration loading from abstract property sources.
 *
 * @author Terry Curran
 * @version 2.1
 */
class ApplicationRuntimeConfigurationTest {

    @Test
    void usesBuiltInDefaultsWhenBootstrapFileIsMinimal() {
        final var source = new ConfigurationPropertiesSource() {
            @Override
            public Map<String, String> loadApplicationProperties() {
                return Map.of(
                        "database.url", "jdbc:sqlserver://localhost;databaseName=OpenData",
                        "database.user", "OpenData",
                        "database.password", "");
            }

            @Override
            public Map<String, String> loadPluginProperties(final String pluginId) {
                return Map.of();
            }
        };

        final var runtime = ApplicationRuntimeConfiguration.load(source, Map.of());

        assertEquals("OpenData", runtime.database().poolName());
        assertEquals(Path.of("logs"), runtime.logging().directory());
        assertEquals(4, runtime.execution().maxParallelPlugins());
    }

    @Test
    void rejectsInvalidTypedApplicationValue() {
        final var source = new ConfigurationPropertiesSource() {
            @Override
            public Map<String, String> loadApplicationProperties() {
                return Map.of(
                        "database.url", "jdbc:sqlserver://localhost;databaseName=OpenData",
                        "database.user", "OpenData",
                        "database.password", "",
                        "execution.max-parallel-plugins", "many");
            }

            @Override
            public Map<String, String> loadPluginProperties(final String pluginId) {
                return Map.of();
            }
        };

        assertThrows(OpenDataConfigurationException.class,
                () -> ApplicationRuntimeConfiguration.load(source, Map.of()));
    }
}
