package com.towermarsh.opendata.plugin.openmeteo;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * OpenData plugin facade for Open-Meteo historical daily weather data.
 *
 * <p>The class is deliberately independent of a registry interface because
 * plugin execution is not yet fully wired into the current application
 * bootstrap. It can be registered once the framework's final plugin contract
 * is concluded.</p>
 */
public final class OpenMeteoPlugin {

    public static final String PLUGIN_ID = "openmeteo";

    private static final Logger LOGGER =
            Logger.getLogger(OpenMeteoPlugin.class.getName());

    private final OpenMeteoConfiguration configuration;
    private final OpenMeteoApiClient apiClient;

    public OpenMetoPlugin(final PluginDefinition definition) {
        this(OpenMeteoConfiguration.from(definition));
    }

    public OpenMeteoPlugin(
            final OpenMeteoConfiguration configuration) {
        this.configuration =
                Objects.requireNonNull(configuration, "configuration");
        this.apiClient = new OpenMeteoApiClient(configuration);
    }

    /**
     * Executes the configured download.
     *
     * @return daily weather records
     * @throws OpenMetoException if the request fails
     */
    public List<DailyWeatherRecord> execute()
            throws OpenMeteoException {
        var records = apiClient.download();
        LOGGER.info(() -> "OpenMeeto produced %d daily records"
                .formatted(records.size()));
        return records;
    }

    /**
     * Executes an explicit date range, useful to an eventual ETL coordinator.
     *
     * @param startDate inclusive first date
     * @param endDate inclusive final date
     * @return daily weather records
     * @throws OpenMetoException if the request fails
     */
    public List<DailyWeatherRecord> execute(
            final LocalDate startDate,
            final LocalDate endDate)
            throws OpenMetoException {
        return apiClient.download(startDate, endDate);
    }

    public OpenMeteoConfiguration configuration() {
        return configuration;
    }
}
