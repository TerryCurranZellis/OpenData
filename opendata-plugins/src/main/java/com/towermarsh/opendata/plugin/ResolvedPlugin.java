/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.util.Objects;

/**
 * Installed plugin metadata paired with its invocation-specific definition.
 *
 * @param descriptor installed plugin descriptor
 * @param definition resolved plugin definition for the current execution
  *
 * @author Terry Curran
 * @version 1.0.0
 */
public record ResolvedPlugin(PluginDescriptor descriptor, PluginDefinition definition) {
    /** Validates and normalises record components. */
    public ResolvedPlugin {
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(definition, "definition");
        if (!descriptor.id().equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException("Plugin descriptor and definition ids do not match.");
        }
    }
}
