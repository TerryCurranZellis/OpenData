/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.openmeteo;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoInitialise;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Open-Meteo plugin entry point; delegates flow control to Initialise. */
public final class OpenMeteoPlugin implements OpenDataPlugin {

    /**
     *
     */
    public static final String PLUGIN_ID = "openmeteo";
    private final OpenMeteoInitialise initialise;

    /**
     *
     * @param definition
     */
    public OpenMeteoPlugin(final PluginDefinition definition) { this(OpenMeteoConfiguration.from(definition)); }

    /**
     *
     * @param configuration
     */
    public OpenMeteoPlugin(final OpenMeteoConfiguration configuration) { this.initialise = new OpenMeteoInitialise(configuration); }

    /**
     *
     * @param context
     * @return
     * @throws Exception
     */
    @Override public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        return initialise.execute(Objects.requireNonNull(context, "context"));
    }

    /**
     *
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<DailyWeatherRecord> execute(final LocalDate start, final LocalDate end) throws Exception {
        return initialise.execute(start, end);
    }

    /**
     *
     * @return
     */
    public OpenMeteoConfiguration configuration() { return initialise.configuration(); }
}
