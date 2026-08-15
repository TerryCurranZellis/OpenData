/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.RsaConfigurationPasswordCipher;
import com.towermarsh.opendata.database.SQLServerResource;
import com.towermarsh.opendata.plugin.JdbcPluginRegistry;
import java.util.List;
import java.util.Map;

/**
 * Opens the bootstrap database resources needed for one GUI plugin-table load.
 *
 * <p>The connection pool is deliberately scoped to the read operation in Batch
 * 3. Later administration/execution batches may introduce a longer-lived GUI
 * application service if several operations benefit from sharing one session.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class PluginTableDataLoader {

    /**
     * Loads the current plugin-table data from the configured SQL Server
     * database.
     *
     * @return current registered plugin rows
     */
    public List<PluginTableEntry> load() {
        final var passwordCipher = new RsaConfigurationPasswordCipher();
        final var bootstrap = new ApplicationBootstrapPropertiesLoader(passwordCipher)
                .load(Map.of());

        try (var database = SQLServerResource.initialise(
                bootstrap.toDatabasePoolConfiguration())) {
            final var registry = new JdbcPluginRegistry(database);
            return new PluginTableDataService(database, registry).load();
        }
    }
}
