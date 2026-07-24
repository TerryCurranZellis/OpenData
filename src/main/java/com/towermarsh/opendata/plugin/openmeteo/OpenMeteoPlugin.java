/*
 * Filename: OpenMeteoPlugin.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/** Complete Open-Meteo download and SQL Server persistence plugin. */
public final class OpenMeteoPlugin implements OpenDataPlugin {
    public static final String PLUGIN_ID = "openmeteo";
    private static final Logger LOGGER = Logger.getLogger(OpenMeteoPlugin.class.getName());

    private final OpenMeteoConfiguration configuration;
    private final OpenMeteoApiClient apiClient;

    public OpenMeteoPlugin(final PluginDefinition definition) {
        this(OpenMeteoConfiguration.from(definition));
    }

    public OpenMeteoPlugin(final OpenMeteoConfiguration configuration) {
        this(configuration, new OpenMeteoApiClient(configuration));
    }

    OpenMeteoPlugin(
            final OpenMeteoConfiguration configuration,
            final OpenMeteoApiClient apiClient) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient");
    }

    @Override
    public PluginMetrics execute(final PluginExecutionContext context) throws OpenMeteoException {
        Objects.requireNonNull(context, "context");
        final List<DailyWeatherRecord> records = apiClient.download();
        LOGGER.info(() -> "OpenMeteo produced %d daily records".formatted(records.size()));
        if (context.dryRun()) {
            return new PluginMetrics(records.size(), 0, 0, records.size());
        }
        final var persistence = new OpenMeteoRepository(context.database())
                .save(configuration, records, context.runId());
        return new PluginMetrics(
                records.size(),
                persistence.inserted(),
                persistence.updated(),
                persistence.skipped());
    }

    /** Legacy download-only entry point retained for focused tests and callers. */
    public List<DailyWeatherRecord> execute() throws OpenMeteoException {
        return apiClient.download();
    }

    public List<DailyWeatherRecord> execute(
            final LocalDate startDate,
            final LocalDate endDate) throws OpenMeteoException {
        return apiClient.download(startDate, endDate);
    }

    public OpenMeteoConfiguration configuration() {
        return configuration;
    }
}
