/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.openmeteo.initialise;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.openmeteo.extract.OpenMeteoExtract;
import com.towermarsh.opendata.plugin.openmeteo.finalise.OpenMeteoFinalise;
import com.towermarsh.opendata.plugin.openmeteo.load.OpenMeteoLoad;
import com.towermarsh.opendata.plugin.openmeteo.transform.OpenMeteoTransform;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Sets up Open-Meteo configuration and controls the plugin flow. */
public final class OpenMeteoInitialise {
    private final OpenMeteoConfiguration configuration;
    private final OpenMeteoExtract extract;
    private final OpenMeteoTransform transform;
    private final OpenMeteoLoad load = new OpenMeteoLoad();
    private final OpenMeteoFinalise finalise = new OpenMeteoFinalise();
    public OpenMeteoInitialise(final OpenMeteoConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.extract = new OpenMeteoExtract(configuration);
        this.transform = new OpenMeteoTransform(configuration);
    }
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        PluginMetrics metrics = PluginMetrics.ZERO;
        try {
            final List<DailyWeatherRecord> records = transform.transform(extract.extract());
            metrics = load.load(configuration, records, context);
            return metrics;
        } finally { finalise.complete(metrics); }
    }
    public List<DailyWeatherRecord> execute(final LocalDate start, final LocalDate end) throws Exception {
        return transform.transform(extract.extract(start, end));
    }
    public OpenMeteoConfiguration configuration() { return configuration; }
}
