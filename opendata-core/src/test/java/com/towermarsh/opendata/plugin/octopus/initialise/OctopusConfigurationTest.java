/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.initialise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyType;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests shared typed property parsing for Octopus configuration.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
class OctopusConfigurationTest {

    @Test
    void buildsPathsUsingSharedPropertyReader() {
        final var configuration = OctopusConfiguration.from(definition(
                "octopus",
                Map.of(
                        OctopusConfiguration.PROP_INPUT_DIRECTORY, property(
                                OctopusConfiguration.PROP_INPUT_DIRECTORY,
                                " C:/Attachments/octopus "),
                        OctopusConfiguration.PROP_WORKING_DIRECTORY, property(
                                OctopusConfiguration.PROP_WORKING_DIRECTORY,
                                "work/octopus"),
                        OctopusConfiguration.PROP_ARCHIVE_DIRECTORY, property(
                                OctopusConfiguration.PROP_ARCHIVE_DIRECTORY,
                                "archive/octopus"))));

        assertEquals(Path.of("C:/Attachments/octopus"), configuration.inputDirectory());
        assertEquals(Path.of("work/octopus"), configuration.workingDirectory());
        assertEquals(Path.of("archive/octopus"), configuration.archiveDirectory());
    }

    @Test
    void rejectsWrongPluginId() {
        assertThrows(IllegalArgumentException.class, () -> OctopusConfiguration.from(
                definition("openmeteo", validProperties())));
    }

    @Test
    void rejectsMissingRequiredPath() {
        final var properties = Map.of(
                OctopusConfiguration.PROP_INPUT_DIRECTORY,
                property(OctopusConfiguration.PROP_INPUT_DIRECTORY, "input"),
                OctopusConfiguration.PROP_WORKING_DIRECTORY,
                property(OctopusConfiguration.PROP_WORKING_DIRECTORY, "work"));

        assertThrows(IllegalArgumentException.class, () -> OctopusConfiguration.from(
                definition("octopus", properties)));
    }

    @Test
    void rejectsBlankRequiredPath() {
        final var properties = Map.of(
                OctopusConfiguration.PROP_INPUT_DIRECTORY,
                property(OctopusConfiguration.PROP_INPUT_DIRECTORY, "   "),
                OctopusConfiguration.PROP_WORKING_DIRECTORY,
                property(OctopusConfiguration.PROP_WORKING_DIRECTORY, "work"),
                OctopusConfiguration.PROP_ARCHIVE_DIRECTORY,
                property(OctopusConfiguration.PROP_ARCHIVE_DIRECTORY, "archive"));

        assertThrows(IllegalArgumentException.class, () -> OctopusConfiguration.from(
                definition("octopus", properties)));
    }

    private static Map<String, PluginPropertyDefinition> validProperties() {
        return Map.of(
                OctopusConfiguration.PROP_INPUT_DIRECTORY,
                property(OctopusConfiguration.PROP_INPUT_DIRECTORY, "input"),
                OctopusConfiguration.PROP_WORKING_DIRECTORY,
                property(OctopusConfiguration.PROP_WORKING_DIRECTORY, "work"),
                OctopusConfiguration.PROP_ARCHIVE_DIRECTORY,
                property(OctopusConfiguration.PROP_ARCHIVE_DIRECTORY, "archive"));
    }

    private static PluginPropertyDefinition property(
            final String name,
            final String value) {
        return new PluginPropertyDefinition(
                name,
                value,
                PluginPropertyType.PATH,
                false,
                "test property");
    }

    private static PluginDefinition definition(
            final String id,
            final Map<String, PluginPropertyDefinition> properties) {
        return new PluginDefinition(
                id,
                "Octopus Energy",
                "Test definition",
                "com.towermarsh.opendata.plugin.octopus.OctopusPlugin",
                true,
                1,
                "octopus-statements",
                List.of(),
                properties,
                Map.of());
    }
}
