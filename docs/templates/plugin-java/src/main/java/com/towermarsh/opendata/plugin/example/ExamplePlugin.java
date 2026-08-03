/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.example.config.ExampleConfiguration;
import com.towermarsh.opendata.plugin.example.initialise.ExampleInitialise;
import java.util.Objects;

/** Thin framework entry point for one example plugin execution. */
public final class ExamplePlugin implements OpenDataPlugin {

    public static final String PLUGIN_ID = "example";

    private final ExampleInitialise initialise;

    /** Constructor used by {@code ReflectionPluginFactory}. */
    public ExamplePlugin(final PluginDefinition definition) {
        this(ExampleConfiguration.from(definition));
    }

    public ExamplePlugin(final ExampleConfiguration configuration) {
        this.initialise = new ExampleInitialise(
                Objects.requireNonNull(configuration, "configuration"));
    }

    @Override
    public PluginMetrics execute(final PluginExecutionContext context)
            throws Exception {
        return initialise.execute(
                Objects.requireNonNull(context, "context"));
    }
}
