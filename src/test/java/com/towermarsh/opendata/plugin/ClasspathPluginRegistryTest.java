package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ClasspathPluginRegistryTest {

    @Test
    void listsOfgemAndOpenMeteoFromClasspathIndex() {
        final PluginRegistry registry =
                new ClasspathPluginRegistry();

        assertEquals(
                List.of("ofgem", "openmeteo"),
                registry.list().stream()
                        .map(PluginDescriptor::id)
                        .toList());

        assertTrue(registry.requireEnabled("OPENMETEO").enabled());
    }
}
