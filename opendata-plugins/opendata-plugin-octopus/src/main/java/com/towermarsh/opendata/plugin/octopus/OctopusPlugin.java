/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusInitialise;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Octopus Energy personal billing import plugin.
 *
 * <p>Parses Octopus Energy statement PDF files and persists electricity and
 * gas billing records into the OpenData database schema.
 *
 * <p>The plugin follows the five-step ETL pipeline defined by the OpenData
 * framework:
 * <ol>
 *   <li><b>Initialise</b> ({@link OctopusInitialise}) – loads configuration
 *       and orchestrates the remaining steps.</li>
 *   <li><b>Extract</b> – discovers and makes PDF files available in the input
 *       directory (placeholder: currently lists existing files).</li>
 *   <li><b>Transform</b> – parses PDF text into structured
 *       {@link com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord}
 *       and {@link com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord}
 *       instances.</li>
 *   <li><b>Load</b> – persists records to the database; passes through on a
 *       dry run (placeholder: database write pending).</li>
 *   <li><b>Finalise</b> – archives processed files and reports statistics
 *       (placeholder: archiving pending).</li>
 * </ol>
 *
 * <p>Phase failures are normalised by the framework PluginExceptionHandler.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class OctopusPlugin implements OpenDataPlugin {

    /** 
     * Stable plugin identifier used in configuration and CLI selection. 
     */
    public static final String PLUGIN_ID = "octopus";

    private static final Logger LOGGER = Logger.getLogger(OctopusPlugin.class.getName());

    private final OctopusConfiguration configuration;
    private final OctopusInitialise initialise;

    /**
     * Creates the Octopus plugin from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     */
    public OctopusPlugin(final PluginDefinition definition) {
        this(OctopusConfiguration.from(definition));
    }

    /**
     * Creates the Octopus plugin from typed configuration.
     *
     * @param configuration typed Octopus configuration
     */
    public OctopusPlugin(final OctopusConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.initialise = new OctopusInitialise(configuration);
    }

    /**
     * Executes the complete Octopus ETL pipeline for one plugin task.
     *
     * @param context plugin execution context
     * @return plugin metrics summarising the run
     * @throws Exception if a pipeline phase fails
     */
    @Override
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        Objects.requireNonNull(context, "context");
        LOGGER.info(() -> "OctopusPlugin.execute starting (runId=%s, dryRun=%s)"
                .formatted(context.runId(), context.dryRun()));

        final var metrics = initialise.execute(context);

        LOGGER.info(() -> "OctopusPlugin.execute complete: read=%d inserted=%d updated=%d skipped=%d"
                .formatted(metrics.read(), metrics.inserted(), metrics.updated(), metrics.skipped()));

        return metrics;
    }

    /**
     * Returns the typed Octopus configuration.
     *
     * @return typed Octopus configuration
     */
    public OctopusConfiguration configuration() {
        return configuration;
    }
}
