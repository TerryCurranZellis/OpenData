/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.plugin.PluginDescriptor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 1.0.0
 */
class ClasspathPluginRegistryTest {

    @Test
    void listsAllBundledPluginsFromSingleClasspathIndex() {
        final PluginRegistry registry =
                new ClasspathPluginRegistry();

        assertEquals(
                List.of("electric-demand", "electric-supply", "octopus", "octopus-adjustment", "ofgem", "openmeteo"),
                registry.list().stream()
                        .map(PluginDescriptor::id)
                        .toList());

        assertTrue(registry.requireEnabled("OPENMETEO").enabled());
        assertTrue(registry.requireEnabled("OCTOPUS").enabled());
    }
}
