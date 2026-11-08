/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricsupply.initialise;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.neso.NesoExtraction;
import com.towermarsh.opendata.plugin.neso.NesoSettings;
import com.towermarsh.opendata.plugin.electricsupply.extract.ElectricSupplyExtract;
import com.towermarsh.opendata.plugin.electricsupply.finalise.ElectricSupplyFinalise;
import com.towermarsh.opendata.plugin.electricsupply.load.ElectricSupplyLoad;
import com.towermarsh.opendata.plugin.electricsupply.transform.ElectricSupplyTransform;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Initialise step and pipeline entry point: runs extract, transform, load and
 * finalise in order. Finalise always runs so working files are removed.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class ElectricSupplyInitialise {

    private static final Logger LOG = Logger.getLogger(ElectricSupplyInitialise.class.getName());

    private final NesoSettings settings;
    private final ElectricSupplyExtract extractor;
    private final ElectricSupplyTransform transformer;
    private final ElectricSupplyLoad loader;
    private final ElectricSupplyFinalise finaliser;

    /**
     * Creates the initialise step.
     *
     * @param settings validated plugin settings
     */
    public ElectricSupplyInitialise(NesoSettings settings) {
        this(settings, new ElectricSupplyExtract(), new ElectricSupplyTransform(), new ElectricSupplyLoad(), new ElectricSupplyFinalise());
    }

    ElectricSupplyInitialise(
            NesoSettings settings,
            ElectricSupplyExtract extractor,
            ElectricSupplyTransform transformer,
            ElectricSupplyLoad loader,
            final ElectricSupplyFinalise finaliser) {
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
    public PluginMetrics execute(PluginExecutionContext context) throws Exception {
        Objects.requireNonNull(context, "context");
        LOG.info(() -> "electric-supply initialise: starting pipeline (dryRun=%s, %s to %s)"
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
