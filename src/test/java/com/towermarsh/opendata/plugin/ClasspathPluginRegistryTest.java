/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class ClasspathPluginRegistryTest {

    @Test
    void listsOfgemOpenMeteoAndOctopusFromClasspathIndex() {
        final PluginRegistry registry =
                new ClasspathPluginRegistry();

        assertEquals(
                List.of("octopus", "ofgem", "openmeteo"),
                registry.list().stream()
                        .map(PluginDescriptor::id)
                        .toList());

        assertTrue(registry.requireEnabled("OPENMETEO").enabled());
        assertTrue(registry.requireEnabled("OCTOPUS").enabled());
    }
}
