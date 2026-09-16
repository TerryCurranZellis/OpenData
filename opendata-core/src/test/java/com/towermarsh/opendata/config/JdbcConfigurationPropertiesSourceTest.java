/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests database-backed configuration property loading.
 *
 * @author Terry Curran
 * @version 2.0.0
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

    @Test
    void loadApplicationPropertiesRestoresEncryptedPrefixFromDatabaseFlag() throws Exception {
        when(database.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("database.password", "database.user");
        when(resultSet.getString(2)).thenReturn("ciphertext", "OpenData");
        when(resultSet.getBoolean(3)).thenReturn(true, false);

        final var source = new JdbcConfigurationPropertiesSource(database);
        final Map<String, String> properties = source.loadApplicationProperties();

        assertEquals("{enc}ciphertext", properties.get("database.password"));
        assertEquals("OpenData", properties.get("database.user"));
    }
}
