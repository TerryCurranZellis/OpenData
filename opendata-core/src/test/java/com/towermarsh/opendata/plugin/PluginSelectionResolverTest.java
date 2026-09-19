/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;
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
        assertEquals(List.of(enabled), registry.list().stream()
                .filter(PluginDescriptor::enabled)
                .toList());
    }

    @Test
    void selectedPluginResolvesById() {
        final var ofgem = descriptor("ofgem", true);
        final PluginRegistry registry = registry(ofgem);
        assertEquals(List.of(ofgem), new PluginSelectionResolver().resolve(List.of("ofgem"), registry));
    }


    @Test
    void explicitGuiSelectionResolvesCanonicalIdsInRequestedOrder() {
        final var ofgem = descriptor("ofgem", true);
        final var openmeteo = descriptor("openmeteo", true);
        final PluginRegistry registry = registry(ofgem, openmeteo);

        assertEquals(
                List.of(openmeteo, ofgem),
                new PluginSelectionResolver().resolve(
                        List.of(" OpenMeteo ", "OFGEM"), registry));
    }

    @Test
    void explicitGuiSelectionRejectsEmptySelection() {
        final PluginRegistry registry = registry(descriptor("ofgem", true));

        assertThrows(
                PluginRegistryException.class,
                () -> new PluginSelectionResolver().resolve(List.of(), registry));
    }

    @Test
    void selectedDuplicatePluginIdsAreRejected() {
        final PluginRegistry registry = registry(descriptor("ofgem", true));
        assertThrows(
                PluginRegistryException.class,
                () -> new PluginSelectionResolver().resolve(List.of("ofgem", "ofgem"), registry));
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
