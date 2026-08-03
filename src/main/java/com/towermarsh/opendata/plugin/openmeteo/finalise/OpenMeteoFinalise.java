/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.openmeteo.finalise;

import com.towermarsh.opendata.plugin.PluginMetrics;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Performs Open-Meteo cleanup and final phase reporting. */
public final class OpenMeteoFinalise {
    private static final Logger LOGGER = Logger.getLogger(OpenMeteoFinalise.class.getName());
    public void complete(final PluginMetrics metrics) {
        LOGGER.log(Level.INFO, "OpenMeteo finalise complete; read={0}, inserted={1}, updated={2}, skipped={3}",
                new Object[]{metrics.read(), metrics.inserted(), metrics.updated(), metrics.skipped()});
    }
}
