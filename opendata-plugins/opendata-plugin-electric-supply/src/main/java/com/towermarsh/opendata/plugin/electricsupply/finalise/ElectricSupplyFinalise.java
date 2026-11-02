/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricsupply.finalise;

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
public final class ElectricSupplyFinalise {

    private static final Logger LOG = Logger.getLogger(ElectricSupplyFinalise.class.getName());

    /**
     * Cleans up and reports.
     *
     * @param extraction extraction to clean up
     * @param metrics load metrics
     * @param dryRun whether this was a dry run
     * @param completed whether all previous steps completed
     */
    public void finalise(
            final NesoExtraction extraction,
            final PluginMetrics metrics,
            final boolean dryRun,
            final boolean completed) {
        Objects.requireNonNull(extraction, "extraction");
        Objects.requireNonNull(metrics, "metrics");
        extraction.cleanUp();
        LOG.info(() -> "electric-supply finalise: completed=%s, dryRun=%s, read=%d, inserted=%d, updated=%d, skipped=%d"
                .formatted(completed, dryRun, metrics.read(), metrics.inserted(),
                        metrics.updated(), metrics.skipped()));
    }
}
