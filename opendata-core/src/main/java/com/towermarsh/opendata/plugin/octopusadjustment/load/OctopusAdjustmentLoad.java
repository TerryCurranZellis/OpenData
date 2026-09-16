/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.load;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopusadjustment.initialise.OctopusAdjustmentConfiguration;
import com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentParseResult;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Load boundary for Octopus adjustment records.
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentLoad {

    private static final Logger LOGGER = Logger.getLogger(OctopusAdjustmentLoad.class.getName());

    /**
     * Persists adjustment data, or reports it without writes in dry-run mode.
     *
     * @param result transformed adjustment batch
     * @param configuration typed configuration
     * @param context execution context
     * @return plugin metrics
     * @since 3.1.0
     */
    public PluginMetrics load(
            final OctopusAdjustmentParseResult result,
            final OctopusAdjustmentConfiguration configuration,
            final PluginExecutionContext context) {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(context, "context");

        if (context.dryRun()) {
            LOGGER.info(() -> "Octopus adjustment load (dry run): files=%d, records=%d"
                    .formatted(result.sources().size(), result.totalRecords()));
            return new PluginMetrics(
                    result.totalRecords(),
                    0,
                    0,
                    result.totalRecords());
        }

        if (result.sources().isEmpty()) {
            return PluginMetrics.ZERO;
        }

        final var saved = new OctopusAdjustmentPersistenceRepository(context.database())
                .save(result, context.runId());
        return new PluginMetrics(
                result.totalRecords(),
                saved.inserted(),
                saved.updated(),
                saved.skipped());
    }
}
