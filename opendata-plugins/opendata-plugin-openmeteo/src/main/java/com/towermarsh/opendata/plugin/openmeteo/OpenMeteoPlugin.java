/* 
 * Copyright © 2026 Terry Curran; 
 * SPDX-License-Identifier: Apache-2.0 
 */
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

/**
 * Open-Meteo plugin entry point delegates flow control to Initialise.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public class OpenMeteoPlugin implements OpenDataPlugin {

    public static final String PLUGIN_ID = "openmeteo";
    private final OpenMeteoInitialise initialise;

    /**
     * Open the plugin
     *
     * @param definition
     */
    public OpenMeteoPlugin(PluginDefinition definition) {
        this(OpenMeteoConfiguration.from(definition));
    }

    /**
     *
     * @param configuration
     */
    public OpenMeteoPlugin(OpenMeteoConfiguration configuration) {
        this.initialise = new OpenMeteoInitialise(configuration);
    }

    /**
     * execute the plugin
     * @param context
     * @return
     * @throws Exception
     */
    @Override
    public PluginMetrics execute(PluginExecutionContext context) throws Exception {
        return initialise.execute(Objects.requireNonNull(context, "context"));
    }

    /**
     * Execute the plugin
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<DailyWeatherRecord> execute(LocalDate start, LocalDate end) throws Exception {
        return initialise.execute(start, end);
    }

    /**
     *
     * @return
     */
    public OpenMeteoConfiguration configuration() {
        return initialise.configuration();
    }
}
