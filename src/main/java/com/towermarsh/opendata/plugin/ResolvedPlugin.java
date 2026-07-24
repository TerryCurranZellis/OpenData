/*
 * Filename: ResolvedPlugin.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.util.Objects;

/** Installed plugin metadata paired with its invocation-specific definition. */
public record ResolvedPlugin(PluginDescriptor descriptor, PluginDefinition definition) {
    public ResolvedPlugin {
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(definition, "definition");
        if (!descriptor.id().equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException("Plugin descriptor and definition ids do not match.");
        }
    }
}
