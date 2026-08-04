/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.initialise;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopus.extract.OctopusExtract;
import com.towermarsh.opendata.plugin.octopus.extract.ExtractedOctopusStatement;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusTransform;
import com.towermarsh.opendata.plugin.octopus.load.OctopusLoad;
import com.towermarsh.opendata.plugin.octopus.finalise.OctopusFinalise;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Initialise step for the Octopus plugin.
 *
 * <p>This is the pipeline entry point. It is responsible for:
 * <ol>
 *   <li>Loading and validating the plugin configuration.</li>
 *   <li>Calling the Extract step to make PDF files available.</li>
 *   <li>Calling the Transform step to parse the PDFs into records.</li>
 *   <li>Calling the Load step to persist the records (or dry-run bypass).</li>
 *   <li>Calling the Finalise step to clean up and report statistics.</li>
 * </ol>
 *
 * <p>Phase exceptions are allowed to propagate to the framework, where the
 * central PluginExceptionHandler adds the plugin identity for logging and audit.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class OctopusInitialise {

    private static final Logger LOGGER = Logger.getLogger(OctopusInitialise.class.getName());

    private final OctopusConfiguration configuration;
    private final OctopusExtract extractor;
    private final OctopusTransform transformer;
    private final OctopusLoad loader;
    private final OctopusFinalise finaliser;

    /**
     * Creates the initialise step using the supplied configuration.
     *
     * @param configuration typed Octopus plugin configuration
     */
    public OctopusInitialise(final OctopusConfiguration configuration) {
        this(
                configuration,
                new OctopusExtract(),
                new OctopusTransform(),
                new OctopusLoad(),
                new OctopusFinalise());
    }

    OctopusInitialise(
            final OctopusConfiguration configuration,
            final OctopusExtract extractor,
            final OctopusTransform transformer,
            final OctopusLoad loader,
            final OctopusFinalise finaliser) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.extractor = Objects.requireNonNull(extractor, "extractor");
        this.transformer = Objects.requireNonNull(transformer, "transformer");
        this.loader = Objects.requireNonNull(loader, "loader");
        this.finaliser = Objects.requireNonNull(finaliser, "finaliser");
    }

    /**
     * Executes the full Octopus ETL pipeline.
     *
     * @param context plugin execution context providing the database, run ID,
     *                dry-run flag and other runtime dependencies
     * @return plugin metrics summarising the run
     * @throws Exception if any pipeline phase fails
     */
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        Objects.requireNonNull(context, "context");

        LOGGER.info(() -> "Octopus initialise: starting pipeline (dryRun=%s)"
                .formatted(context.dryRun()));

        List<ExtractedOctopusStatement> statements = List.of();
        var parseResult = new OctopusParseResult(List.of(), List.of(), List.of());
        var metrics = PluginMetrics.ZERO;
        var completed = false;
        try {
            statements = extractor.extract(configuration, context);
            parseResult = transformer.transform(statements);
            metrics = loader.load(parseResult, configuration, context);
            completed = true;
            return metrics;
        } finally {
            finaliser.finalise(configuration, statements, parseResult, metrics, context.dryRun(), completed);
        }
    }
}
