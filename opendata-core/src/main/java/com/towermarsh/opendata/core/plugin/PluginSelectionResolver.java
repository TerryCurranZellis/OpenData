/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.plugin.PluginDescriptor;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Resolves named plugins or every enabled plugin from the authoritative registry.
 *
 * @author Terry Curran
 * @since 3.0.0
 * @version 4.1.0
 */
public class PluginSelectionResolver {

    /**
     * Resolves an explicit plugin-id snapshot against the authoritative
     * registry.
     *
     * @param pluginIds explicitly selected plugin identifiers
     * @param registry authoritative registered-plugin source
     * @return enabled plugin descriptors in requested order
     */
    public List<PluginDescriptor> resolve(
            List<String> pluginIds,
            PluginRegistry registry) {
        Objects.requireNonNull(pluginIds, "pluginIds");
        Objects.requireNonNull(registry, "registry");
        final List<String> requested = pluginIds.stream()
                .map(PluginSelectionResolver::canonicalId)
                .toList();
        if (requested.isEmpty()) {
            throw new PluginRegistryException("No plugins were selected.");
        }
        if (new LinkedHashSet<>(requested).size() != requested.size()) {
            throw new PluginRegistryException("A plugin was selected more than once.");
        }
        return requested.stream().map(registry::requireEnabled).toList();
    }

    /**
     * Canonical id.
     *
     * @param pluginId requested plugin id
     * @return result of canonical id
     */
    private static String canonicalId(final String pluginId) {
        return pluginId.trim().toLowerCase(Locale.ROOT);
    }
}
