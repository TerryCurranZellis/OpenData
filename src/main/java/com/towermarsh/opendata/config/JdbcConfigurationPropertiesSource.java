/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads and stores configuration properties in SQL Server.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class JdbcConfigurationPropertiesSource
        implements ConfigurationPropertiesSource {

    private static final String SELECT_APPLICATION_SQL = """
            SELECT [property_key], [property_value]
              FROM [core].[application_property]
             ORDER BY [property_key]
            """;
    private static final String SELECT_PLUGIN_SQL = """
            SELECT [property_key], [property_value]
              FROM [core].[plugin_property]
             WHERE [plugin_id] = ?
             ORDER BY [property_key]
            """;
    private static final String UPSERT_APPLICATION_SQL = """
            MERGE [core].[application_property] AS target
            USING (SELECT ? AS [property_key], ? AS [property_value], ? AS [is_encrypted], ? AS [updated_at]) AS source
               ON target.[property_key] = source.[property_key]
            WHEN MATCHED THEN
                UPDATE SET [property_value] = source.[property_value],
                           [is_encrypted] = source.[is_encrypted],
                           [updated_at] = source.[updated_at]
            WHEN NOT MATCHED THEN
                INSERT ([property_key], [property_value], [is_encrypted], [updated_at])
                VALUES (source.[property_key], source.[property_value], source.[is_encrypted], source.[updated_at]);
            """;
    private static final String UPSERT_PLUGIN_SQL = """
            MERGE [core].[plugin_property] AS target
            USING (SELECT ? AS [plugin_id], ? AS [property_key], ? AS [property_value], ? AS [updated_at]) AS source
               ON target.[plugin_id] = source.[plugin_id]
              AND target.[property_key] = source.[property_key]
            WHEN MATCHED THEN
                UPDATE SET [property_value] = source.[property_value],
                           [updated_at] = source.[updated_at]
            WHEN NOT MATCHED THEN
                INSERT ([plugin_id], [property_key], [property_value], [updated_at])
                VALUES (source.[plugin_id], source.[property_key], source.[property_value], source.[updated_at]);
            """;
    private static final String DELETE_PLUGIN_SQL = """
            DELETE FROM [core].[plugin_property]
             WHERE [plugin_id] = ?
            """;

    private final DatabaseResourceManager database;

    /**
     * Creates the source.
     *
     * @param database database resource manager
     */
    public JdbcConfigurationPropertiesSource(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    @Override
    public Map<String, String> loadApplicationProperties() {
        return readApplicationProperties();
    }

    @Override
    public Map<String, String> loadPluginProperties(final String pluginId) {
        final var properties = readPluginProperties(pluginId);
        if (properties.isEmpty()) {
            throw new PluginDefinitionException("No database configuration exists for plugin: " + pluginId);
        }
        return properties;
    }

    /**
     * Upserts application properties.
     *
     * @param values property values
     * @param encryptedKeys keys whose values are encrypted
     */
    public void saveApplicationProperties(
            final Map<String, String> values,
            final List<String> encryptedKeys) {
        final var encryptedLookup = List.copyOf(encryptedKeys);
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(UPSERT_APPLICATION_SQL)) {
            final var updatedAt = LocalDateTime.now();
            for (var entry : values.entrySet()) {
                statement.setString(1, entry.getKey());
                statement.setString(2, entry.getValue());
                statement.setBoolean(3, encryptedLookup.contains(entry.getKey()));
                statement.setObject(4, updatedAt);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to store application properties.", exception);
        }
    }

    /**
     * Replaces all stored properties for one plugin.
     *
     * @param pluginId plugin identifier
     * @param values plugin properties
     */
    public void savePluginProperties(final String pluginId, final Map<String, String> values) {
        try (var connection = database.getConnection()) {
            try (var deleteStatement = connection.prepareStatement(DELETE_PLUGIN_SQL)) {
                deleteStatement.setString(1, pluginId);
                deleteStatement.executeUpdate();
            }
            try (var upsertStatement = connection.prepareStatement(UPSERT_PLUGIN_SQL)) {
                final var updatedAt = LocalDateTime.now();
                for (var entry : values.entrySet()) {
                    upsertStatement.setString(1, pluginId);
                    upsertStatement.setString(2, entry.getKey());
                    upsertStatement.setString(3, entry.getValue());
                    upsertStatement.setObject(4, updatedAt);
                    upsertStatement.addBatch();
                }
                upsertStatement.executeBatch();
            }
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to store properties for plugin: " + pluginId, exception);
        }
    }

    /**
     * Reads application properties.
     *
     * @return stored application properties
     */
    private Map<String, String> readApplicationProperties() {
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(SELECT_APPLICATION_SQL);
                var resultSet = statement.executeQuery()) {
            final Map<String, String> values = new LinkedHashMap<>();
            while (resultSet.next()) {
                values.put(resultSet.getString(1), resultSet.getString(2));
            }
            return values;
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to load application properties.", exception);
        }
    }

    /**
     * Reads plugin properties for one plugin.
     *
     * @param pluginId plugin identifier
     * @return stored plugin properties
     */
    private Map<String, String> readPluginProperties(final String pluginId) {
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(SELECT_PLUGIN_SQL)) {
            statement.setString(1, pluginId);
            try (var resultSet = statement.executeQuery()) {
                final Map<String, String> values = new LinkedHashMap<>();
                while (resultSet.next()) {
                    values.put(resultSet.getString(1), resultSet.getString(2));
                }
                return values;
            }
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to load plugin properties for: " + pluginId, exception);
        }
    }
}
