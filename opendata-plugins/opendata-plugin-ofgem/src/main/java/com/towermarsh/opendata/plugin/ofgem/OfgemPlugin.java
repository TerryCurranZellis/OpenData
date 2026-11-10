/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.ofgem;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.ofgem.initialise.OfgemConfiguration;
import com.towermarsh.opendata.plugin.ofgem.initialise.OfgemInitialise;
import java.util.Objects;

/**
 * Ofgem plugin entry point; delegates all flow control to Initialise.
 * @author Terry Curran
 * @version 4.1.0
 */
public final class OfgemPlugin implements OpenDataPlugin {

    /** Stable identifier for the Ofgem plugin. */
    public static final String PLUGIN_ID = "ofgem";
    private final OfgemInitialise initialise;

    /**
     * Creates the plugin from a resolved plugin definition.
     *
     * @param definition source and database settings for the Ofgem plugin
     */
    public OfgemPlugin(PluginDefinition definition) { this(OfgemConfiguration.from(definition)); }

    /**
     * Creates the plugin from validated Ofgem configuration.
     *
     * @param configuration validated Ofgem configuration
     */
    public OfgemPlugin(OfgemConfiguration configuration) { this.initialise = new OfgemInitialise(configuration); }

    /**
     * Executes the Ofgem pipeline using the provided run context.
     *
     * @param context plugin execution context and database resources
     * @return row counts for this execution
     * @throws Exception if extraction, transformation or persistence fails
     */
    @Override public PluginMetrics execute(PluginExecutionContext context) throws Exception {
        return initialise.execute(Objects.requireNonNull(context, "context"));
    }

    /**
     * Returns the validated configuration for this plugin instance.
     *
     * @return current Ofgem configuration
     */
    public OfgemConfiguration configuration() { return initialise.configuration(); }
}
