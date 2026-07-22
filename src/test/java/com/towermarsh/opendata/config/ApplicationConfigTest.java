package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.towermarsh.opendata.config.model.BootstrapConfig;

class ApplicationConfigTest {

    @Test
    void exposesSelectedPluginIdentifier() {
        final var plugin =
                new PropertiesPluginDefinitionLoader().load("ofgem", Map.of());

        final var config = new ApplicationConfig(
                new BootstrapConfig(
                        "OpenData",
                        "test",
                        Path.of("work"),
                        Path.of("archive"),
                        Path.of("failed"),
                        Map.of()),
                plugin,
                Map.of(),
                Optional.empty(),
                true,
                false);

        assertEquals("ofgem", config.pluginId());
    }
}
