/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.plugin.PluginDescriptor;
import java.util.Map;
import java.util.Objects;

/**
 * Validated plugin metadata and the complete property set to persist.
 *
 * @param descriptor plugin registry metadata
 * @param properties complete plugin definition properties
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public record PluginRegistration(
        PluginDescriptor descriptor,
        Map<String, String> properties) {

    /** Validates and copies components. */
    public PluginRegistration {
        descriptor = Objects.requireNonNull(descriptor, "descriptor");
        properties = Map.copyOf(Objects.requireNonNull(properties, "properties"));
    }
}
