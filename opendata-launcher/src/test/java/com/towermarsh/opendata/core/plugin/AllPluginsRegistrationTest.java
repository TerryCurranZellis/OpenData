package com.towermarsh.opendata.core.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.towermarsh.opendata.database.SqlBatchParser;
import com.towermarsh.opendata.config.PluginRegistrationResolver;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Integration checks for packaged plugin registrars and their SQL resources.
 *
 * <p>This test runs in the executable launcher where all provider JARs and
 * their registration resources are present.</p>
 *
 * @author Terry Curran
 * @since 4.0.0
 * @version 4.1.0
 */
class AllPluginsRegistrationTest {

    /** Checks the registration hook and SQL script for every bundled plugin. */
    @Test
    void everyBundledPluginResolvesAndShipsRegistrationSql() throws Exception {
        var resolver = new PluginRegistrationResolver();
        var registrations = resolver.resolveAllPackaged();
        assertEquals(6, registrations.size());
        var locator = new PluginRegistrarLocator();
        for (var registration : registrations) {
            var descriptor = registration.descriptor();
            var registrar = locator.find(descriptor).orElseThrow(
                    () -> new AssertionError("No RegisterPlugin for " + descriptor.id()));
            assertEquals(descriptor.id(), registrar.pluginId());
            var resource = "sql/plugins/" + descriptor.id() + ".sql";
            try (var in = getClass().getClassLoader().getResourceAsStream(resource)) {
                assertNotNull(in, resource);
                var script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                assertFalse(SqlBatchParser.batches(script).isEmpty());
                assertFalse(script.contains("USE [OpenData]"));
            }
        }
    }

    /** Checks selected plugin descriptors are resolvable from resources. */
    @Test
    void everyPluginDefinitionIsAvailableFromItsProject() throws Exception {
        var resolver = new PluginRegistrationResolver();
        for (var id : new String[]{"electric-demand", "electric-supply", "octopus-adjustment"}) {
            var resource = "config/plugins/" + id + ".properties";
            try (var input = getClass().getClassLoader().getResourceAsStream(resource)) {
                assertNotNull(input, resource);
            }
            assertEquals(id, resolver.resolvePackaged(List.of(id)).get(0).descriptor().id());
        }
    }
}
