/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricsupply;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.electricsupply.initialise.ElectricSupplyInitialise;
import com.towermarsh.opendata.plugin.neso.NesoSettings;
import java.util.Objects;

/**
 * Neso historic generation mix ingestion plugin.
 *
 * <p>Follows the standard five-step pipeline: {@link ElectricSupplyInitialise initialise}
 * (configuration and orchestration), extract (download CSV resources),
 * transform (parse rows), load (upsert into {@code electricsupply.GenerationMix}) and finalise
 * (clean up and report). Database objects are created by {@link RegisterPlugin}.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class ElectricSupplyPlugin implements OpenDataPlugin {

    /** Stable plugin identifier. */
    public static final String PLUGIN_ID = "electric-supply";

    private final ElectricSupplyInitialise initialise;

    /**
     * Creates the plugin from its resolved definition.
     *
     * @param definition resolved plugin definition
     */
    public ElectricSupplyPlugin(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (!PLUGIN_ID.equals(definition.id())) {
            throw new IllegalArgumentException("Wrong plugin id: " + definition.id());
        }
        this.initialise = new ElectricSupplyInitialise(NesoSettings.from(definition, "historic-generation-mix"));
    }

    @Override
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        return initialise.execute(Objects.requireNonNull(context, "context"));
    }
}
