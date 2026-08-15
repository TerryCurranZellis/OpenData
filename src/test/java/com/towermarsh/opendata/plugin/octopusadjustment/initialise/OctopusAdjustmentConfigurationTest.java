/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.initialise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyType;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests typed Version 3.1.0 adjustment configuration. */
class OctopusAdjustmentConfigurationTest {

    @Test
    void createsConfigurationFromResolvedDefinition() {
        final var configuration = OctopusAdjustmentConfiguration.from(definition(
                "octopus-adjustment", "A-5F191685"));

        assertEquals("A-5F191685", configuration.accountNumber());
        assertEquals(Path.of("input"), configuration.inputDirectory());
        assertEquals(Path.of("work"), configuration.workingDirectory());
        assertEquals(Path.of("archive"), configuration.archiveDirectory());
    }

    @Test
    void rejectsWrongPluginId() {
        assertThrows(IllegalArgumentException.class,
                () -> OctopusAdjustmentConfiguration.from(
                        definition("octopus", "A-5F191685")));
    }

    @Test
    void rejectsUnsafeAccountNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> OctopusAdjustmentConfiguration.from(
                        definition("octopus-adjustment", "A-5F191685/other")));
    }

    private static PluginDefinition definition(
            final String pluginId,
            final String accountNumber) {
        final Map<String, PluginPropertyDefinition> properties = Map.of(
                "account.number", property("account.number", accountNumber, PluginPropertyType.STRING),
                "input.directory", property("input.directory", "input", PluginPropertyType.PATH),
                "working.directory", property("working.directory", "work", PluginPropertyType.PATH),
                "archive.directory", property("archive.directory", "archive", PluginPropertyType.PATH));
        return new PluginDefinition(
                pluginId,
                "Octopus Energy Adjustments",
                "",
                "com.towermarsh.opendata.plugin.octopusadjustment.OctopusAdjustmentPlugin",
                true,
                1,
                "octopus-energy-adjustments",
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
