/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.cli.PluginCommand;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class PluginSelectionResolverTest {
    @Test
    void allReturnsOnlyEnabledPluginsInRegistryOrder() {
        final var enabled = descriptor("openmeteo", true);
        final var disabled = descriptor("future", false);
        final PluginRegistry registry = new PluginRegistry() {
            @Override
            public List<PluginDescriptor> list() {
                return List.of(enabled, disabled);
            }

            @Override
            public Optional<PluginDescriptor> find(final String pluginId) {
                return list().stream().filter(item -> item.id().equals(pluginId)).findFirst();
            }

            @Override
            public PluginDescriptor requireEnabled(final String pluginId) {
                return find(pluginId).filter(PluginDescriptor::enabled).orElseThrow();
            }
        };
        final var arguments = arguments(List.of(), true);
        assertEquals(List.of(enabled), new PluginSelectionResolver().resolve(arguments, registry));
    }

    @Test
    void selectedPluginResolvesById() {
        final var ofgem = descriptor("ofgem", true);
        final PluginRegistry registry = registry(ofgem);
        final var arguments = arguments(List.of("ofgem"), false);
        assertEquals(List.of(ofgem), new PluginSelectionResolver().resolve(arguments, registry));
    }

    @Test
    void selectedDuplicatePluginIdsAreRejected() {
        final PluginRegistry registry = registry(descriptor("ofgem", true));
        final var arguments = arguments(List.of("ofgem", "ofgem"), false);
        assertThrows(PluginRegistryException.class, () -> new PluginSelectionResolver().resolve(arguments, registry));
    }

    private static CommandLineArguments arguments(
            final List<String> pluginIds,
            final boolean all) {
        return new CommandLineArguments(
                pluginIds,
                all,
                Optional.empty(),
                OptionalInt.empty(),
                false,
                true,
                false,
                false,
                false,
                false,
                PluginCommand.RUN);
    }

    private static PluginRegistry registry(final PluginDescriptor... descriptors) {
        final List<PluginDescriptor> plugins = List.of(descriptors);
        return new PluginRegistry() {
            @Override
            public List<PluginDescriptor> list() {
                return plugins;
            }

            @Override
            public Optional<PluginDescriptor> find(final String pluginId) {
                return plugins.stream().filter(item -> item.id().equals(pluginId)).findFirst();
            }

            @Override
            public PluginDescriptor requireEnabled(final String pluginId) {
                return find(pluginId).filter(PluginDescriptor::enabled).orElseThrow();
            }
        };
    }

    private static PluginDescriptor descriptor(final String id, final boolean enabled) {
        return new PluginDescriptor(id, id, "", "example." + id, enabled, 1);
    }
}
