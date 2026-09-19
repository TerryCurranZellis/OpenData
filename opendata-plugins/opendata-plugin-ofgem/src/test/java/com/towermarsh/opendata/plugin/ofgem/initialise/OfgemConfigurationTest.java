/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.initialise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginEndpointDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyType;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests the Ofgem configuration migration to shared validation.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
class OfgemConfigurationTest {

    @Test
    void usesSharedTypedPropertyParsing() {
        final PluginDefinition definition = definition();
        property(definition, "download.output-filename", "  price-cap.xlsx  ");
        property(definition, "download.connect-timeout", "PT45S");
        property(definition, "download.request-timeout", "PT3M");
        property(definition, "archive.original-file", "yes");
        property(definition, "download.working-directory", "work/custom-ofgem");
        property(definition, "archive.directory", "archive/custom-ofgem");

        final OfgemConfiguration configuration = OfgemConfiguration.from(definition);

        assertEquals("price-cap.xlsx", configuration.outputFilename());
        assertEquals(Duration.ofSeconds(45), configuration.connectTimeout());
        assertEquals(Duration.ofMinutes(3), configuration.requestTimeout());
        assertEquals(true, configuration.archiveOriginalFile());
        assertEquals(Path.of("work/custom-ofgem"), configuration.workingDirectory());
        assertEquals(Path.of("archive/custom-ofgem"), configuration.archiveDirectory());
        assertEquals(
                Path.of("work/custom-ofgem", "price-cap.xlsx").normalize(),
                configuration.downloadPath());
    }

    @Test
    void appliesExistingDefaultsWhenPropertiesAreAbsent() {
        final OfgemConfiguration configuration = OfgemConfiguration.from(definition());

        assertEquals(
                "ofgem-final-levelised-cap-rates.xlsx",
                configuration.outputFilename());
        assertEquals(Duration.ofSeconds(30), configuration.connectTimeout());
        assertEquals(Duration.ofSeconds(120), configuration.requestTimeout());
        assertEquals(true, configuration.archiveOriginalFile());
        assertEquals(Path.of("work/ofgem"), configuration.workingDirectory());
        assertEquals(Path.of("archive/ofgem"), configuration.archiveDirectory());
    }

    @Test
    void reportsInvalidBooleanUsingPluginAndPropertyNames() {
        final PluginDefinition definition = definition();
        property(definition, "archive.original-file", "sometimes");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> OfgemConfiguration.from(definition));

        assertEquals(
                "Plugin 'ofgem' property 'archive.original-file' must be a boolean.",
                exception.getMessage());
    }

    @Test
    void rejectsNonPositiveTimeoutsThroughSharedValidationRules() {
        final PluginEndpointDefinition endpoint = mock(PluginEndpointDefinition.class);

        assertThrows(
                IllegalArgumentException.class,
                () -> new OfgemConfiguration(
                        endpoint,
                        "price-cap.xlsx",
                        Duration.ZERO,
                        Duration.ofSeconds(1),
                        true,
                        Path.of("work/ofgem"),
                        Path.of("archive/ofgem")));
    }

    private static PluginDefinition definition() {
        final PluginDefinition definition = mock(PluginDefinition.class);
        final PluginEndpointDefinition endpoint = mock(PluginEndpointDefinition.class);
        when(definition.id()).thenReturn("ofgem");
        when(definition.requireEndpoint(OfgemConfiguration.ENDPOINT_NAME)).thenReturn(endpoint);
        when(definition.findProperty(anyString())).thenReturn(Optional.empty());
        return definition;
    }

    private static void property(
            final PluginDefinition definition,
            final String name,
            final String value) {
        when(definition.findProperty(name)).thenReturn(Optional.of(
                new PluginPropertyDefinition(
                        name,
                        value,
                        PluginPropertyType.STRING,
                        false,
                        "test property")));
    }
}
