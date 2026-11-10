/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests database-backed configuration property loading.
 *
 * @author Terry Curran
 * @since 2.0.0
 * @version 4.1.0
 */
@ExtendWith(MockitoExtension.class)
class JdbcConfigurationPropertiesSourceTest {

    @Mock
    private DatabaseResourceManager database;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement statement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private PreparedStatement deleteStatement;

    @Mock
    private PreparedStatement upsertStatement;

    @Test
    void loadApplicationPropertiesPreservesAesPrefixFromDatabase() throws Exception {
        when(database.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("database.password", "database.user");
        when(resultSet.getString(2)).thenReturn("{aes}ciphertext", "OpenData");
        when(resultSet.getBoolean(3)).thenReturn(true, false);

        final var source = new JdbcConfigurationPropertiesSource(database);
        final Map<String, String> properties = source.loadApplicationProperties();

        assertEquals("{aes}ciphertext", properties.get("database.password"));
        assertEquals("OpenData", properties.get("database.user"));
    }

    /** All application-property updates are part of one committed transaction. */
    @Test
    void saveApplicationPropertiesCommitsAndRestoresAutoCommit() throws Exception {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(contains("MERGE [core].[application_property]")))
                .thenReturn(statement);

        new JdbcConfigurationPropertiesSource(database)
                .saveApplicationProperties(Map.of("database.password", "{aes}secret"),
                        List.of("database.password"));

        verify(statement).setBoolean(3, true);
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    /** A plugin-property replacement is atomic across DELETE and MERGE. */
    @Test
    void savePluginPropertiesCommitsAndRestoresAutoCommit() throws Exception {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(contains("DELETE FROM [core].[plugin_property]")))
                .thenReturn(deleteStatement);
        when(connection.prepareStatement(contains("MERGE [core].[plugin_property]")))
                .thenReturn(upsertStatement);

        new JdbcConfigurationPropertiesSource(database)
                .savePluginProperties("ofgem", Map.of("plugin.enabled", "false"));

        verify(deleteStatement).executeUpdate();
        verify(upsertStatement).executeBatch();
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    /** A failed replacement must roll back the preceding DELETE statement. */
    @Test
    void failedPluginPropertyWriteRollsBackDeletion() throws Exception {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(contains("DELETE FROM [core].[plugin_property]")))
                .thenReturn(deleteStatement);
        when(connection.prepareStatement(contains("MERGE [core].[plugin_property]")))
                .thenReturn(upsertStatement);
        when(upsertStatement.executeBatch()).thenThrow(new SQLException("simulated failure"));

        assertThrows(DatabaseAccessException.class, () ->
                new JdbcConfigurationPropertiesSource(database)
                        .savePluginProperties("ofgem", Map.of("dataset.id", "test")));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).setAutoCommit(true);
    }

    /** A failed application-property batch also rolls back its transaction. */
    @Test
    void failedApplicationPropertyWriteRollsBack() throws Exception {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(contains("MERGE [core].[application_property]")))
                .thenReturn(statement);
        when(statement.executeBatch()).thenThrow(new SQLException("simulated failure"));

        assertThrows(DatabaseAccessException.class, () ->
                new JdbcConfigurationPropertiesSource(database)
                        .saveApplicationProperties(Map.of("init", "false"), List.of()));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).setAutoCommit(true);
    }

}
