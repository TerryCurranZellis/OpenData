/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopusadjustment.initialise.OctopusAdjustmentConfiguration;
import com.towermarsh.opendata.plugin.octopusadjustment.initialise.OctopusAdjustmentInitialise;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Imports Octopus Energy adjustment bills into adjustment-specific tables.
 *
 * <p>The plugin deliberately keeps recalculated adjustment billing facts
 * separate from ordinary Octopus statement data while reusing the existing
 * public Octopus statement parser and billing model records.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentPlugin implements OpenDataPlugin {

    /** Stable plugin identifier used by configuration and execution. */
    public static final String PLUGIN_ID = "octopus-adjustment";

    private static final Logger LOGGER = Logger.getLogger(OctopusAdjustmentPlugin.class.getName());

    private final OctopusAdjustmentConfiguration configuration;
    private final OctopusAdjustmentInitialise initialise;

    /**
     * Creates the plugin from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     * @since 3.1.0
     */
    public OctopusAdjustmentPlugin(final PluginDefinition definition) {
        this(OctopusAdjustmentConfiguration.from(definition));
    }

    /**
     * Creates the plugin from typed configuration.
     *
     * @param configuration typed adjustment configuration
     * @since 3.1.0
     */
    public OctopusAdjustmentPlugin(final OctopusAdjustmentConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.initialise = new OctopusAdjustmentInitialise(configuration);
    }

    /**
     * Executes the complete adjustment ETL pipeline.
     *
     * @param context plugin execution context
     * @return run metrics
     * @throws Exception when a pipeline stage fails
     * @since 3.1.0
     */
    @Override
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        Objects.requireNonNull(context, "context");
        LOGGER.info(() -> "OctopusAdjustmentPlugin.execute starting (runId=%s, dryRun=%s)"
                .formatted(context.runId(), context.dryRun()));

        final var metrics = initialise.execute(context);

        LOGGER.info(() -> "OctopusAdjustmentPlugin.execute complete: read=%d inserted=%d updated=%d skipped=%d"
                .formatted(metrics.read(), metrics.inserted(), metrics.updated(), metrics.skipped()));
        return metrics;
    }

    /**
     * Returns the typed configuration used by this plugin instance.
     *
     * @return typed configuration
     * @since 3.1.0
     */
    public OctopusAdjustmentConfiguration configuration() {
        return configuration;
    }
}
