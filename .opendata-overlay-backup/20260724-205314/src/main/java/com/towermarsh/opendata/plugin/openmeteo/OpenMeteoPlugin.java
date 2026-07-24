/*
 *  Filename: OpenMeteoPlugin.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.plugin.openmeteo;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * OpenData plugin facade for Open-Meteo historical daily weather data.
 *
 * <p>
 * The class is deliberately independent of a registry interface because plugin
 * execution is not yet fully wired into the current application bootstrap. It
 * can be registered once the framework's final plugin contract is
 * concluded.</p>
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class OpenMeteoPlugin {

    public static final String PLUGIN_ID = "openmeteo";

    private static final Logger LOGGER
            = Logger.getLogger(OpenMeteoPlugin.class.getName());

    private final OpenMeteoConfiguration configuration;
    private final OpenMeteoApiClient apiClient;

    public OpenMeteoPlugin(final PluginDefinition definition) {
        this(OpenMeteoConfiguration.from(definition));
    }

    public OpenMeteoPlugin(
            final OpenMeteoConfiguration configuration) {
        this.configuration
                = Objects.requireNonNull(configuration, "configuration");
        this.apiClient = new OpenMeteoApiClient(configuration);
    }

    /**
     * Executes the configured download.
     *
     * @return daily weather records
     * @throws com.towermarsh.opendata.plugin.openmeteo.OpenMeteoException
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
     * @throws com.towermarsh.opendata.plugin.openmeteo.OpenMeteoException
     */
    public List<DailyWeatherRecord> execute(
            final LocalDate startDate,
            final LocalDate endDate)
            throws OpenMeteoException {
        return apiClient.download(startDate, endDate);
    }

    public OpenMeteoConfiguration configuration() {
        return configuration;
    }
}
