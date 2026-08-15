/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.ApplicationRuntimeConfiguration;
import com.towermarsh.opendata.config.ClasspathConfigurationPropertiesSource;
import com.towermarsh.opendata.config.JdbcConfigurationPropertiesSource;
import com.towermarsh.opendata.config.RsaConfigurationPasswordCipher;
import com.towermarsh.opendata.database.SQLServerResource;
import com.towermarsh.opendata.logging.LoggingManager;
import com.towermarsh.opendata.app.ApplicationInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resolves the effective non-sensitive application settings shown by the
 * JavaFX Settings/Preferences dialog.
 *
 * <p>Batch 5 deliberately presents settings read-only. No decrypted password
 * or encrypted password text crosses this GUI boundary.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class ApplicationSettingsGateway {

    /**
     * Loads effective application settings.
     *
     * @return immutable display rows
     */
    public List<ConfigurationDisplayEntry> load() {
        final var passwordCipher = new RsaConfigurationPasswordCipher();
        final var bootstrap = new ApplicationBootstrapPropertiesLoader(passwordCipher)
                .load(Map.of());

        if (bootstrap.useDatabaseProperties()) {
            try (var database = SQLServerResource.initialise(
                    bootstrap.toDatabasePoolConfiguration())) {
                return entries(
                        bootstrap.useDatabaseProperties(),
                        ApplicationRuntimeConfiguration.load(
                                new JdbcConfigurationPropertiesSource(database), Map.of()));
            }
        }
        return entries(
                false,
                ApplicationRuntimeConfiguration.load(
                        new ClasspathConfigurationPropertiesSource(), Map.of()));
    }

    private static List<ConfigurationDisplayEntry> entries(
            final boolean databaseBacked,
            final ApplicationRuntimeConfiguration runtime) {
        final List<ConfigurationDisplayEntry> result = new ArrayList<>();
        result.add(new ConfigurationDisplayEntry(
                "Application version", ApplicationInfo.current().version()));
        result.add(new ConfigurationDisplayEntry(
                "Runtime configuration source",
                databaseBacked ? "SQL Server application properties" : "Classpath properties"));
        result.add(new ConfigurationDisplayEntry(
                "Database driver", runtime.database().driverClass()));
        result.add(new ConfigurationDisplayEntry(
                "Database URL", runtime.database().jdbcUrl()));
        result.add(new ConfigurationDisplayEntry(
                "Database user", runtime.database().user()));
        result.add(new ConfigurationDisplayEntry("Database password", "********"));
        result.add(new ConfigurationDisplayEntry(
                "Database pool name", runtime.database().poolName()));
        result.add(new ConfigurationDisplayEntry(
                "Database pool max total", Integer.toString(runtime.database().maxTotal())));
        result.add(new ConfigurationDisplayEntry(
                "Database pool max idle", Integer.toString(runtime.database().maxIdle())));
        result.add(new ConfigurationDisplayEntry(
                "Database pool min idle", Integer.toString(runtime.database().minIdle())));
        result.add(new ConfigurationDisplayEntry(
                "Database pool max wait",
                runtime.database().maxWait().toSeconds() + " seconds"));
        result.add(new ConfigurationDisplayEntry(
                "Database validation query", runtime.database().validationQuery()));
        result.add(new ConfigurationDisplayEntry(
                "Maximum parallel plugins",
                Integer.toString(runtime.execution().maxParallelPlugins())));
        result.add(new ConfigurationDisplayEntry(
                "Execution shutdown timeout",
                runtime.execution().shutdownTimeout().toSeconds() + " seconds"));
        result.add(new ConfigurationDisplayEntry(
                "Configured log directory", runtime.logging().directory().toString()));
        result.add(new ConfigurationDisplayEntry(
                "Active GUI log directory",
                LoggingManager.activeLogDirectory()
                        .map(path -> path.toString())
                        .orElse("Not initialised")));
        result.add(new ConfigurationDisplayEntry(
                "Log file size limit",
                Integer.toString(runtime.logging().fileLimitBytes()) + " bytes"));
        result.add(new ConfigurationDisplayEntry(
                "Rotating log file count", Integer.toString(runtime.logging().fileCount())));
        result.add(new ConfigurationDisplayEntry(
                "Append to existing log", runtime.logging().append() ? "Yes" : "No"));
        return List.copyOf(result);
    }
}
