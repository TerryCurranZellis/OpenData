/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.openmeteo.load;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import java.util.List;
import java.util.Objects;

/** Loads transformed Open-Meteo records into the database. */
public final class OpenMeteoLoad {

    /**
     *
     * @param configuration
     * @param records
     * @param context
     * @return
     */
    public PluginMetrics load(final OpenMeteoConfiguration configuration,
            final List<DailyWeatherRecord> records, final PluginExecutionContext context) {
        Objects.requireNonNull(records, "records");
        final int read = records.size();
        if (context.dryRun()) return new PluginMetrics(read, 0, 0, read);
        final OpenMeteoPersistenceResult result = new OpenMeteoRepository(context.database())
                .save(configuration, records, context.runId());
        return new PluginMetrics(read, result.inserted(), result.updated(), result.skipped());
    }
}
