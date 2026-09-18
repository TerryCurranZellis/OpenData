/* 
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0 
 * 
 */
package com.towermarsh.opendata.plugin.octopus.load;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Load the octopus data
 *
 * @author terry
 * @version 2.0.0
 */
public final class OctopusLoad {

    private static final Logger LOGGER = Logger.getLogger(OctopusLoad.class.getName());

    /**
     * Load the data
     *
     * @param result load results
     * @param configuration configuration for the loader
     * @param context current context
     * @return metrics for the load
     */
    public PluginMetrics load(OctopusParseResult result, OctopusConfiguration configuration, PluginExecutionContext context) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(context);
        if (context.dryRun()) {
            LOGGER.info(() -> "Octopus load (dry run): files=%d, records=%d".formatted(result.statements().size(), result.totalRecords()));
            return new PluginMetrics(result.totalRecords(), 0, 0, result.totalRecords());
        }
        if (result.statements().isEmpty()) {
            return PluginMetrics.ZERO;
        }
        var saved = new OctopusPersistenceRepository(context.database()).save(result, context.runId());
        return new PluginMetrics(result.totalRecords(), saved.inserted(), saved.updated(), saved.skipped());
    }
}
