/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricdemand.initialise;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.neso.NesoExtraction;
import com.towermarsh.opendata.plugin.neso.NesoSettings;
import com.towermarsh.opendata.plugin.electricdemand.extract.ElectricDemandExtract;
import com.towermarsh.opendata.plugin.electricdemand.finalise.ElectricDemandFinalise;
import com.towermarsh.opendata.plugin.electricdemand.load.ElectricDemandLoad;
import com.towermarsh.opendata.plugin.electricdemand.transform.ElectricDemandTransform;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Initialise step and pipeline entry point: runs extract, transform, load and
 * finalise in order. Finalise always runs so working files are removed.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class ElectricDemandInitialise {

    private static final Logger LOG = Logger.getLogger(ElectricDemandInitialise.class.getName());

    private final NesoSettings settings;
    private final ElectricDemandExtract extractor;
    private final ElectricDemandTransform transformer;
    private final ElectricDemandLoad loader;
    private final ElectricDemandFinalise finaliser;

    /**
     * Creates the initialise step.
     *
     * @param settings validated plugin settings
     */
    public ElectricDemandInitialise(final NesoSettings settings) {
        this(settings, new ElectricDemandExtract(), new ElectricDemandTransform(), new ElectricDemandLoad(), new ElectricDemandFinalise());
    }

    ElectricDemandInitialise(
            final NesoSettings settings,
            final ElectricDemandExtract extractor,
            final ElectricDemandTransform transformer,
            final ElectricDemandLoad loader,
            final ElectricDemandFinalise finaliser) {
        this.settings = Objects.requireNonNull(settings, "settings");
        this.extractor = Objects.requireNonNull(extractor, "extractor");
        this.transformer = Objects.requireNonNull(transformer, "transformer");
        this.loader = Objects.requireNonNull(loader, "loader");
        this.finaliser = Objects.requireNonNull(finaliser, "finaliser");
    }

    /**
     * Executes the pipeline.
     *
     * @param context execution context
     * @return run metrics
     * @throws Exception when a step fails
     */
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        Objects.requireNonNull(context, "context");
        LOG.info(() -> "electric-demand initialise: starting pipeline (dryRun=%s, %s to %s)"
                .formatted(context.dryRun(), settings.startDate(), settings.endDate()));
        var extraction = NesoExtraction.empty();
        var metrics = PluginMetrics.ZERO;
        var completed = false;
        try {
            extraction = extractor.extract(settings);
            final var records = transformer.transform(extraction, settings);
            metrics = loader.load(records, context);
            completed = true;
            return metrics;
        } finally {
            finaliser.finalise(extraction, metrics, context.dryRun(), completed);
        }
    }
}
