/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests persistent plugin registry reads and status changes. */
@ExtendWith(MockitoExtension.class)
class JdbcPluginRegistryTest {

    @Mock
    private DatabaseResourceManager database;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement statement;
    @Mock
    private PreparedStatement propertyStatement;
    @Mock
    private PreparedStatement registryStatement;
    @Mock
    private PreparedStatement enabledQueryStatement;
    @Mock
    private PreparedStatement deleteStatement;
    @Mock
    private PreparedStatement insertStatement;
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void prepareConnection() {
        when(database.getConnection()).thenReturn(connection);
    }

    @Test
    void listsRegisteredPluginsWithStatus() throws Exception {
        when(connection.prepareStatement(contains("FROM [core].[plugin_registry]")))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("plugin_id")).thenReturn("ofgem");
        when(resultSet.getString("display_name")).thenReturn("Ofgem");
        when(resultSet.getString("description")).thenReturn("Price cap");
        when(resultSet.getString("implementation_class")).thenReturn("example.OfgemPlugin");
        when(resultSet.getBoolean("is_enabled")).thenReturn(false);
        when(resultSet.getInt("configuration_version")).thenReturn(2);

        final var plugins = new JdbcPluginRegistry(database).list();

        assertEquals(1, plugins.size());
        assertEquals("ofgem", plugins.get(0).id());
        assertFalse(plugins.get(0).enabled());
        assertEquals(2, plugins.get(0).configurationVersion());
    }

    @Test
    void registrationPreservesExistingDisabledStatusInStoredProperties() throws Exception {
        when(connection.prepareStatement(contains("MERGE [core].[plugin_registry]")))
                .thenReturn(registryStatement);
        when(connection.prepareStatement(contains("SELECT [is_enabled]")))
                .thenReturn(enabledQueryStatement);
        when(enabledQueryStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBoolean(1)).thenReturn(false);
        when(connection.prepareStatement(contains("DELETE FROM [core].[plugin_property]")))
                .thenReturn(deleteStatement);
        when(connection.prepareStatement(contains("INSERT INTO [core].[plugin_property]")))
                .thenReturn(insertStatement);

        new JdbcPluginRegistry(database).register(
                new PluginDescriptor(
                        "example",
                        "Example",
                        "Example plugin",
                        "example.ExamplePlugin",
                        true,
                        1),
                Map.of(
                        "plugin.id", "example",
                        "plugin.enabled", "true",
                        "dataset.id", "example-data"));

        verify(insertStatement, atLeastOnce()).setString(3, "false");
        verify(connection).commit();
    }

    @Test
    void unregisterRemovesPropertiesAndRegistryRow() throws Exception {
        when(connection.prepareStatement(contains("DELETE FROM [core].[plugin_property]")))
                .thenReturn(deleteStatement);
        when(connection.prepareStatement(contains("DELETE FROM [core].[plugin_registry]")))
                .thenReturn(registryStatement);
        when(registryStatement.executeUpdate()).thenReturn(1);

        new JdbcPluginRegistry(database).unregister("Example");

        verify(deleteStatement).setString(1, "example");
        verify(registryStatement).setString(1, "example");
        verify(connection).commit();
    }

    @Test
    void changesRegisteredPluginStatus() throws Exception {
        when(connection.prepareStatement(contains("UPDATE [core].[plugin_registry]")))
                .thenReturn(statement);
        when(connection.prepareStatement(contains("MERGE [core].[plugin_property]")))
                .thenReturn(propertyStatement);
        when(statement.executeUpdate()).thenReturn(1);

        new JdbcPluginRegistry(database).setEnabled("OfGem", true);

        verify(statement).setBoolean(1, true);
        verify(statement).setString(3, "ofgem");
        verify(propertyStatement).setString(1, "ofgem");
        verify(propertyStatement).setString(2, "true");
        verify(connection).commit();
    }

    @Test
    void rejectsStatusChangeForUnknownPlugin() throws Exception {
        when(connection.prepareStatement(contains("UPDATE [core].[plugin_registry]")))
                .thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(0);

        assertThrows(PluginRegistryException.class,
                () -> new JdbcPluginRegistry(database).setEnabled("missing", false));
    }
}
