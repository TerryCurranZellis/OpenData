/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.load;

import com.towermarsh.opendata.exception.PluginException;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Load step for the Octopus plugin.
 *
 * <p><b>Placeholder implementation.</b> In a full implementation this step
 * will be responsible for:
 * <ol>
 *   <li>Opening a database transaction.</li>
 *   <li>Merging electricity records into the target electricity table
 *       (insert new, update changed, skip unchanged).</li>
 *   <li>Merging gas records into the target gas table.</li>
 *   <li>Committing the transaction and returning row counts.</li>
 * </ol>
 *
 * <p>When the execution context indicates a <em>dry run</em>, this step logs
 * the record counts but does not write to the database. The returned
 * {@link PluginMetrics} will have zero insert and update counts with all
 * records reported as skipped.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OctopusLoad {

    private static final Logger LOGGER = Logger.getLogger(OctopusLoad.class.getName());

    /**
     * Loads the transformed records into the database, or logs them on a dry
     * run.
     *
     * @param parseResult   transformed electricity and gas records
     * @param configuration Octopus plugin configuration
     * @param context       plugin execution context (provides database and dry-run flag)
     * @return plugin metrics summarising the load outcome
     * @throws PluginException if the database write fails
     */
    public PluginMetrics load(
            final OctopusParseResult parseResult,
            final OctopusConfiguration configuration,
            final PluginExecutionContext context) throws PluginException {
        Objects.requireNonNull(parseResult, "parseResult");
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(context, "context");

        final int totalRecords = parseResult.totalRecords();
        final int electricityCount = parseResult.electricityRecords().size();
        final int gasCount = parseResult.gasRecords().size();

        if (context.dryRun()) {
            LOGGER.info(() ->
                    "Octopus load (dry run): would load %d electricity record(s) and %d gas record(s)"
                    .formatted(electricityCount, gasCount));
            return new PluginMetrics(totalRecords, 0, 0, totalRecords);
        }

        // TODO: Implement full database persistence using context.database()
        // Steps:
        //   1. Obtain a connection from context.database()
        //   2. MERGE electricity records into the target electricity table
        //   3. MERGE gas records into the target gas table
        //   4. Return actual insert/update/skip counts
        LOGGER.warning("Octopus load: database persistence not yet implemented");
        return new PluginMetrics(totalRecords, 0, 0, totalRecords);
    }
}
