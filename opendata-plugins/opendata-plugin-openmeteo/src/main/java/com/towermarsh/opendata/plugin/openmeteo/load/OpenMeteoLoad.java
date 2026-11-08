/* 
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0 
 */
package com.towermarsh.opendata.plugin.openmeteo.load;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import java.util.List;
import java.util.Objects;

/**
 * Loads transformed Open-Meteo records into the database.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OpenMeteoLoad {

    /**
     *
     * @param configuration
     * @param records
     * @param context
     * @return
     */
    public PluginMetrics load(OpenMeteoConfiguration configuration,
            List<DailyWeatherRecord> records, PluginExecutionContext context) {
        Objects.requireNonNull(records, "records");
        var read = records.size();
        if (context.dryRun()) {
            return new PluginMetrics(read, 0, 0, read);
        }
        var result = new OpenMeteoRepository(context.database())
                .save(configuration, records, context.runId());
        return new PluginMetrics(read, result.inserted(), result.updated(), result.skipped());
    }
}
