/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests shared packaged/external plugin registration resolution.
 *
 * @author Terry Curran
 * @version 3.0.0
 */
class PluginRegistrationResolverTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void listsPackagedPluginCatalogue() {
        final var resolver = new PluginRegistrationResolver();

        final var ids = resolver.packagedPlugins().stream()
                .map(plugin -> plugin.id())
                .toList();

        assertEquals(List.of("octopus", "ofgem", "openmeteo"), ids);
    }

    @Test
    void resolvesNamedPackagedRegistration() {
        final var resolver = new PluginRegistrationResolver();

        final var registrations = resolver.resolvePackaged(List.of("ofgem"));

        assertEquals(1, registrations.size());
        assertEquals("ofgem", registrations.get(0).descriptor().id());
        assertEquals("ofgem", registrations.get(0).properties().get("plugin.id"));
        assertFalse(registrations.get(0).properties().isEmpty());
    }

    @Test
    void resolvesExternalFileUsingItsPluginId() throws IOException {
        final var file = copyPackagedPlugin("ofgem");
        final var resolver = new PluginRegistrationResolver();

        final var registration = resolver.resolveFile(file);

        assertEquals("ofgem", registration.descriptor().id());
        assertTrue(registration.properties().containsKey("dataset.id"));
    }

    @Test
    void preservesCliRequestedIdValidationForExternalFile() throws IOException {
        final var file = copyPackagedPlugin("ofgem");
        final var resolver = new PluginRegistrationResolver();

        assertThrows(
                PluginDefinitionException.class,
                () -> resolver.resolveFile("openmeteo", file));
    }

    private Path copyPackagedPlugin(final String pluginId) throws IOException {
        final var resource = "config/plugins/" + pluginId + ".properties";
        final var target = temporaryDirectory.resolve(pluginId + ".properties");
        try (var input = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(resource)) {
            if (input == null) {
                throw new IOException("Test resource was not found: " + resource);
            }
            Files.copy(input, target);
        }
        return target;
    }
}
