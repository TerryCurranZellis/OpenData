/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.initialise;

import com.towermarsh.opendata.exception.PluginException;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopus.extract.OctopusExtract;
import com.towermarsh.opendata.plugin.octopus.finalise.OctopusFinalise;
import com.towermarsh.opendata.plugin.octopus.load.OctopusLoad;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusTransform;

import java.nio.file.Path;
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
 * <p>All exceptions from sub-steps are propagated as {@link PluginException}
 * with the plugin name {@code "octopus"} so they can be identified in logs and
 * audit records without inspecting the stack trace.
 *
 * @author Terry Curran
 * @version 01 Aug 2026
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
     * @throws PluginException if any pipeline step fails
     */
    public PluginMetrics execute(final PluginExecutionContext context) throws PluginException {
        Objects.requireNonNull(context, "context");

        LOGGER.info(() -> "Octopus initialise: starting pipeline (dryRun=%s)"
                .formatted(context.dryRun()));

        // Extract – make PDF files available in the input directory
        final List<Path> pdfFiles = extractor.extract(configuration);

        // Transform – parse PDFs into structured records
        final OctopusParseResult parseResult = transformer.transform(configuration);

        // Load – persist (or skip on dry run)
        final PluginMetrics metrics = loader.load(parseResult, configuration, context);

        // Finalise – clean up and report
        finaliser.finalise(configuration, pdfFiles, parseResult, metrics);

        return metrics;
    }
}
