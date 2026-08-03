/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.ofgem;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.ofgem.initialise.OfgemConfiguration;
import com.towermarsh.opendata.plugin.ofgem.initialise.OfgemInitialise;
import java.util.Objects;

/** Ofgem plugin entry point; delegates all flow control to Initialise. */
public final class OfgemPlugin implements OpenDataPlugin {
    public static final String PLUGIN_ID = "ofgem";
    private final OfgemInitialise initialise;
    public OfgemPlugin(final PluginDefinition definition) { this(OfgemConfiguration.from(definition)); }
    public OfgemPlugin(final OfgemConfiguration configuration) { this.initialise = new OfgemInitialise(configuration); }
    @Override public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        return initialise.execute(Objects.requireNonNull(context, "context"));
    }
    public OfgemConfiguration configuration() { return initialise.configuration(); }
}
