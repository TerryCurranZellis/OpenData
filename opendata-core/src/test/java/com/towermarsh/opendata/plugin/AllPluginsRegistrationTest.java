package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.towermarsh.opendata.common.database.SqlScriptExecutor;
import com.towermarsh.opendata.config.PluginRegistrationResolver;
import com.towermarsh.opendata.core.plugin.PluginRegistrarLocator;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class AllPluginsRegistrationTest {

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
                assertFalse(SqlScriptExecutor.batches(script).isEmpty());
                assertFalse(script.contains("USE [OpenData]"));
            }
        }
    }

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
