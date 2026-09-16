/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Persistent SQL Server plugin registry and administration service.
 *
 * <p>Plugin metadata is stored in {@code core.plugin_registry}; complete
 * definition properties are stored in {@code core.plugin_property}.</p>
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class JdbcPluginRegistry implements PluginRegistry {

    private static final String SELECT_ALL_SQL = """
            SELECT [plugin_id], [display_name], [description],
                   [implementation_class], [is_enabled], [configuration_version]
              FROM [core].[plugin_registry]
             ORDER BY [plugin_id]
            """;
    private static final String SELECT_ONE_SQL = """
            SELECT [plugin_id], [display_name], [description],
                   [implementation_class], [is_enabled], [configuration_version]
              FROM [core].[plugin_registry]
             WHERE [plugin_id] = ?
            """;
    private static final String SELECT_ENABLED_SQL = """
            SELECT [is_enabled]
              FROM [core].[plugin_registry]
             WHERE [plugin_id] = ?
            """;
    private static final String UPSERT_ENABLED_PROPERTY_SQL = """
            MERGE [core].[plugin_property] AS target
            USING (SELECT ? AS [plugin_id], 'plugin.enabled' AS [property_key],
                          ? AS [property_value], ? AS [updated_at]) AS source
               ON target.[plugin_id] = source.[plugin_id]
              AND target.[property_key] = source.[property_key]
            WHEN MATCHED THEN
                UPDATE SET [property_value] = source.[property_value],
                           [updated_at] = source.[updated_at]
            WHEN NOT MATCHED THEN
                INSERT ([plugin_id], [property_key], [property_value], [updated_at])
                VALUES (source.[plugin_id], source.[property_key],
                        source.[property_value], source.[updated_at]);
            """;
    private static final String UPSERT_REGISTRY_SQL = """
            MERGE [core].[plugin_registry] AS target
            USING (SELECT ? AS [plugin_id], ? AS [display_name], ? AS [description],
                          ? AS [implementation_class], ? AS [is_enabled],
                          ? AS [configuration_version], ? AS [updated_at]) AS source
               ON target.[plugin_id] = source.[plugin_id]
            WHEN MATCHED THEN
                UPDATE SET [display_name] = source.[display_name],
                           [description] = source.[description],
                           [implementation_class] = source.[implementation_class],
                           [configuration_version] = source.[configuration_version],
                           [updated_at] = source.[updated_at]
            WHEN NOT MATCHED THEN
                INSERT ([plugin_id], [display_name], [description],
                        [implementation_class], [is_enabled],
                        [configuration_version], [registered_at], [updated_at])
                VALUES (source.[plugin_id], source.[display_name], source.[description],
                        source.[implementation_class], source.[is_enabled],
                        source.[configuration_version], source.[updated_at], source.[updated_at]);
            """;
    private static final String DELETE_PROPERTIES_SQL = """
            DELETE FROM [core].[plugin_property] WHERE [plugin_id] = ?
            """;
    private static final String INSERT_PROPERTY_SQL = """
            INSERT INTO [core].[plugin_property]
                   ([plugin_id], [property_key], [property_value], [updated_at])
            VALUES (?, ?, ?, ?)
            """;
    private static final String DELETE_REGISTRY_SQL = """
            DELETE FROM [core].[plugin_registry] WHERE [plugin_id] = ?
            """;
    private static final String UPDATE_ENABLED_SQL = """
            UPDATE [core].[plugin_registry]
               SET [is_enabled] = ?, [updated_at] = ?
             WHERE [plugin_id] = ?
            """;

    private final DatabaseResourceManager database;

    /**
     * Creates a database-backed registry.
     *
     * @param database database resource manager
     */
    public JdbcPluginRegistry(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    @Override
    public List<PluginDescriptor> list() {
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet = statement.executeQuery()) {
            final List<PluginDescriptor> plugins = new ArrayList<>();
            while (resultSet.next()) {
                plugins.add(readDescriptor(resultSet));
            }
            return List.copyOf(plugins);
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to list registered plugins.", exception);
        }
    }

    @Override
    public Optional<PluginDescriptor> find(final String pluginId) {
        final var id = normaliseId(pluginId);
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(SELECT_ONE_SQL)) {
            statement.setString(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(readDescriptor(resultSet))
                        : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to find registered plugin: " + id, exception);
        }
    }

    @Override
    public PluginDescriptor requireEnabled(final String pluginId) {
        final var descriptor = find(pluginId)
                .orElseThrow(() -> new PluginRegistryException(
                        "Plugin is not registered: " + normaliseId(pluginId)));
        if (!descriptor.enabled()) {
            throw new PluginRegistryException(
                    "Plugin is registered but disabled: " + descriptor.id());
        }
        return descriptor;
    }

    /**
     * Registers or replaces one plugin and its complete property set.
     *
     * @param descriptor plugin metadata
     * @param properties complete plugin definition properties
     */
    public void register(
            final PluginDescriptor descriptor,
            final Map<String, String> properties) {
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(properties, "properties");
        try (var connection = database.getConnection()) {
            inTransaction(connection, () -> {
                upsertDescriptor(connection, descriptor);
                final var persistedEnabled = readEnabled(connection, descriptor.id());
                final Map<String, String> persistedProperties = new LinkedHashMap<>(properties);
                persistedProperties.put("plugin.enabled", Boolean.toString(persistedEnabled));
                replaceProperties(connection, descriptor.id(), persistedProperties);
            });
        } catch (SQLException exception) {
            throw new DatabaseAccessException(
                    "Unable to register plugin: " + descriptor.id(), exception);
        }
    }

    /**
     * Removes one plugin and its stored configuration.
     *
     * @param pluginId registered plugin id
     */
    public void unregister(final String pluginId) {
        final var id = normaliseId(pluginId);
        try (var connection = database.getConnection()) {
            inTransaction(connection, () -> {
                try (var properties = connection.prepareStatement(DELETE_PROPERTIES_SQL)) {
                    properties.setString(1, id);
                    properties.executeUpdate();
                }
                try (var registry = connection.prepareStatement(DELETE_REGISTRY_SQL)) {
                    registry.setString(1, id);
                    if (registry.executeUpdate() == 0) {
                        throw new PluginRegistryException("Plugin is not registered: " + id);
                    }
                }
            });
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to unregister plugin: " + id, exception);
        }
    }

    /**
     * Changes one registered plugin's enabled status.
     *
     * @param pluginId registered plugin id
     * @param enabled new enabled status
     */
    public void setEnabled(final String pluginId, final boolean enabled) {
        final var id = normaliseId(pluginId);
        try (var connection = database.getConnection()) {
            inTransaction(connection, () -> {
                final var updatedAt = LocalDateTime.now();
                try (var statement = connection.prepareStatement(UPDATE_ENABLED_SQL)) {
                    statement.setBoolean(1, enabled);
                    statement.setObject(2, updatedAt);
                    statement.setString(3, id);
                    if (statement.executeUpdate() == 0) {
                        throw new PluginRegistryException("Plugin is not registered: " + id);
                    }
                }
                upsertEnabledProperty(connection, id, enabled, updatedAt);
            });
        } catch (SQLException exception) {
            throw new DatabaseAccessException(
                    "Unable to %s plugin: %s".formatted(enabled ? "enable" : "disable", id),
                    exception);
        }
    }

    private static PluginDescriptor readDescriptor(final java.sql.ResultSet resultSet)
            throws SQLException {
        return new PluginDescriptor(
                resultSet.getString("plugin_id"),
                resultSet.getString("display_name"),
                resultSet.getString("description"),
                resultSet.getString("implementation_class"),
                resultSet.getBoolean("is_enabled"),
                resultSet.getInt("configuration_version"));
    }

    private static boolean readEnabled(
            final Connection connection,
            final String pluginId) throws SQLException {
        try (var statement = connection.prepareStatement(SELECT_ENABLED_SQL)) {
            statement.setString(1, pluginId);
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new PluginRegistryException(
                            "Registered plugin could not be read back: " + pluginId);
                }
                return resultSet.getBoolean(1);
            }
        }
    }

    private static void upsertEnabledProperty(
            final Connection connection,
            final String pluginId,
            final boolean enabled,
            final LocalDateTime updatedAt) throws SQLException {
        try (var statement = connection.prepareStatement(UPSERT_ENABLED_PROPERTY_SQL)) {
            statement.setString(1, pluginId);
            statement.setString(2, Boolean.toString(enabled));
            statement.setObject(3, updatedAt);
            statement.executeUpdate();
        }
    }

    private static void upsertDescriptor(
            final Connection connection,
            final PluginDescriptor descriptor) throws SQLException {
        try (var statement = connection.prepareStatement(UPSERT_REGISTRY_SQL)) {
            final var updatedAt = LocalDateTime.now();
            statement.setString(1, descriptor.id());
            statement.setString(2, descriptor.displayName());
            statement.setString(3, descriptor.description());
            statement.setString(4, descriptor.implementationClass());
            statement.setBoolean(5, descriptor.enabled());
            statement.setInt(6, descriptor.configurationVersion());
            statement.setObject(7, updatedAt);
            statement.executeUpdate();
        }
    }

    private static void replaceProperties(
            final Connection connection,
            final String pluginId,
            final Map<String, String> properties) throws SQLException {
        try (var delete = connection.prepareStatement(DELETE_PROPERTIES_SQL)) {
            delete.setString(1, pluginId);
            delete.executeUpdate();
        }
        try (var insert = connection.prepareStatement(INSERT_PROPERTY_SQL)) {
            final var updatedAt = LocalDateTime.now();
            for (var entry : properties.entrySet()) {
                insert.setString(1, pluginId);
                insert.setString(2, entry.getKey());
                insert.setString(3, entry.getValue());
                insert.setObject(4, updatedAt);
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private static void inTransaction(
            final Connection connection,
            final SqlWork work) throws SQLException {
        final var originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            work.run();
            connection.commit();
        } catch (SQLException | RuntimeException exception) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            throw exception;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private static String normaliseId(final String pluginId) {
        final var result = Objects.requireNonNull(pluginId, "pluginId")
                .trim().toLowerCase(Locale.ROOT);
        if (result.isBlank()) {
            throw new IllegalArgumentException("pluginId must not be blank.");
        }
        return result;
    }

    @FunctionalInterface
    private interface SqlWork {
        void run() throws SQLException;
    }
}
