/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricdemand.finalise;

import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.neso.NesoExtraction;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Finalise step: removes downloaded working files and reports run statistics.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public class ElectricDemandFinalise {

    private static final Logger LOG = Logger.getLogger(ElectricDemandFinalise.class.getName());

    /**
     * Cleans up and reports.
     *
     * @param extraction extraction to clean up
     * @param metrics load metrics
     * @param dryRun whether this was a dry run
     * @param completed whether all previous steps completed
     */
    public void finalise(
            NesoExtraction extraction,
            PluginMetrics metrics,
            boolean dryRun,
            boolean completed) {
        Objects.requireNonNull(extraction, "extraction");
        Objects.requireNonNull(metrics, "metrics");
        extraction.cleanUp();
        LOG.info(() -> "electric-demand finalise: completed=%s, dryRun=%s, read=%d, inserted=%d, updated=%d, skipped=%d"
                .formatted(completed, dryRun, metrics.read(), metrics.inserted(),
                        metrics.updated(), metrics.skipped()));
    }
}
