/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests external plugin definition registration sources. */
class ConfigurationServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void externalPluginFileUsesPackagedPluginDefinitionFormat() throws Exception {
        final Path pluginFile = temporaryDirectory.resolve("example.properties");
        Files.writeString(pluginFile, """
                plugin.id=example
                plugin.display-name=Example Plugin
                plugin.description=External plugin definition
                plugin.implementation-class=com.towermarsh.opendata.plugin.example.ExamplePlugin
                plugin.enabled=false
                plugin.configuration-version=2
                dataset.id=example-data
                property.batch-size.value=250
                property.batch-size.type=integer
                """);

        final var source = new PropertiesFileConfigurationPropertiesSource(pluginFile);
        final var definition = new PropertiesPluginDefinitionLoader(source)
                .load("example", Map.of());

        assertEquals("example", definition.id());
        assertEquals("Example Plugin", definition.displayName());
        assertEquals(2, definition.configurationVersion());
        assertFalse(definition.enabled());
        assertEquals("250", source.loadPluginProperties("example")
                .get("property.batch-size.value"));
    }

    @Test
    void externalPluginFileMustMatchRequestedPluginId() throws Exception {
        final Path pluginFile = temporaryDirectory.resolve("example.properties");
        Files.writeString(pluginFile, """
                plugin.id=example
                plugin.display-name=Example Plugin
                plugin.implementation-class=example.ExamplePlugin
                dataset.id=example-data
                """);

        final var source = new PropertiesFileConfigurationPropertiesSource(pluginFile);
        assertThrows(PluginDefinitionException.class,
                () -> new PropertiesPluginDefinitionLoader(source)
                        .load("different", Map.of()));
    }

    @Test
    void missingExternalPluginFileIsRejected() {
        final var source = new PropertiesFileConfigurationPropertiesSource(
                temporaryDirectory.resolve("missing.properties"));
        assertThrows(PluginDefinitionException.class,
                () -> source.loadPluginProperties("missing"));
    }
}
