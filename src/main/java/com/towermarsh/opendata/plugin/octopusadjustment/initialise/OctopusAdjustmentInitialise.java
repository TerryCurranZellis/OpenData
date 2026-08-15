/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.initialise;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopusadjustment.extract.ExtractedOctopusAdjustment;
import com.towermarsh.opendata.plugin.octopusadjustment.extract.OctopusAdjustmentExtract;
import com.towermarsh.opendata.plugin.octopusadjustment.finalise.OctopusAdjustmentFinalise;
import com.towermarsh.opendata.plugin.octopusadjustment.load.OctopusAdjustmentLoad;
import com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentParseResult;
import com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentTransform;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Coordinates the five-stage Octopus adjustment ETL pipeline.
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentInitialise {

    private static final Logger LOGGER = Logger.getLogger(OctopusAdjustmentInitialise.class.getName());

    private final OctopusAdjustmentConfiguration configuration;
    private final OctopusAdjustmentExtract extractor;
    private final OctopusAdjustmentTransform transformer;
    private final OctopusAdjustmentLoad loader;
    private final OctopusAdjustmentFinalise finaliser;

    /**
     * Creates the pipeline using the standard adjustment stages.
     *
     * @param configuration typed adjustment configuration
     * @since 3.1.0
     */
    public OctopusAdjustmentInitialise(final OctopusAdjustmentConfiguration configuration) {
        this(
                configuration,
                new OctopusAdjustmentExtract(),
                new OctopusAdjustmentTransform(),
                new OctopusAdjustmentLoad(),
                new OctopusAdjustmentFinalise());
    }

    OctopusAdjustmentInitialise(
            final OctopusAdjustmentConfiguration configuration,
            final OctopusAdjustmentExtract extractor,
            final OctopusAdjustmentTransform transformer,
            final OctopusAdjustmentLoad loader,
            final OctopusAdjustmentFinalise finaliser) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.extractor = Objects.requireNonNull(extractor, "extractor");
        this.transformer = Objects.requireNonNull(transformer, "transformer");
        this.loader = Objects.requireNonNull(loader, "loader");
        this.finaliser = Objects.requireNonNull(finaliser, "finaliser");
    }

    /**
     * Executes extract, transform, load and finalise for one plugin run.
     *
     * @param context execution context
     * @return run metrics
     * @throws Exception when a stage fails
     * @since 3.1.0
     */
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        Objects.requireNonNull(context, "context");
        LOGGER.info(() -> "Octopus adjustment initialise: starting pipeline (dryRun=%s)"
                .formatted(context.dryRun()));

        List<ExtractedOctopusAdjustment> sources = List.of();
        var parseResult = new OctopusAdjustmentParseResult(List.of(), List.of(), List.of());
        var metrics = PluginMetrics.ZERO;
        var completed = false;
        try {
            sources = extractor.extract(configuration, context);
            parseResult = transformer.transform(sources);
            metrics = loader.load(parseResult, configuration, context);
            completed = true;
            return metrics;
        } finally {
            finaliser.finalise(
                    configuration,
                    sources,
                    parseResult,
                    metrics,
                    context.dryRun(),
                    completed);
        }
    }
}
