/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.load;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Tests Open-Meteo integration with the shared JDBC transaction and batch helpers.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
class OpenMeteoRepositoryTest {

    @Test
    void emptyInputAvoidsDatabaseConnection() {
        final DatabaseResourceManager database = mock(DatabaseResourceManager.class);
        final OpenMeteoRepository repository = new OpenMeteoRepository(database);

        final OpenMeteoPersistenceResult result = repository.save(
                configuration(2),
                List.of(),
                UUID.randomUUID());

        assertEquals(new OpenMeteoPersistenceResult(0, 0, 0), result);
        verifyNoInteractions(database);
    }

    @Test
    void persistsUsingSharedTransactionBatchAndQualifiedIdentifiers()
            throws Exception {
        final Fixture fixture = new Fixture(2);
        when(fixture.stageStatement.executeBatch()).thenReturn(new int[]{1});
        when(fixture.updateDailyStatement.executeUpdate()).thenReturn(0);
        when(fixture.insertDailyStatement.executeUpdate()).thenReturn(1);

        final OpenMeteoPersistenceResult result = fixture.repository.save(
                fixture.configuration,
                List.of(record(1)),
                UUID.randomUUID());

        assertEquals(1L, result.inserted());
        assertEquals(0L, result.updated());
        assertEquals(0L, result.skipped());
        verify(fixture.connection).setAutoCommit(false);
        verify(fixture.connection, times(2)).commit();
        verify(fixture.connection).setAutoCommit(true);
        verify(fixture.connection, never()).rollback();
        verify(fixture.stageStatement).addBatch();
        verify(fixture.stageStatement).executeBatch();
        verify(fixture.sqlStatement).execute("SET XACT_ABORT OFF");
    }

    @Test
    void honoursConfiguredStageBatchSize() throws Exception {
        final Fixture fixture = new Fixture(2);
        when(fixture.stageStatement.executeBatch()).thenReturn(
                new int[]{1, 1},
                new int[]{1});
        when(fixture.updateDailyStatement.executeUpdate()).thenReturn(0);
        when(fixture.insertDailyStatement.executeUpdate()).thenReturn(3);

        final OpenMeteoPersistenceResult result = fixture.repository.save(
                fixture.configuration,
                List.of(record(1), record(2), record(3)),
                UUID.randomUUID());

        assertEquals(3L, result.inserted());
        verify(fixture.stageStatement, times(2)).executeBatch();
    }

    @Test
    void databaseFailureRollsBackAndResetsPooledSession() throws Exception {
        final Fixture fixture = new Fixture(2);
        when(fixture.updateLocationStatement.executeUpdate())
                .thenThrow(new SQLException("broken location update"));

        assertThrows(
                DatabaseAccessException.class,
                () -> fixture.repository.save(
                        fixture.configuration,
                        List.of(record(1)),
                        UUID.randomUUID()));

        verify(fixture.connection).rollback();
        verify(fixture.sqlStatement).execute("SET XACT_ABORT OFF");
        verify(fixture.connection).setAutoCommit(true);
    }

    private static OpenMeteoConfiguration configuration(final int batchSize) {
        return new OpenMeteoConfiguration(
                URI.create("https://archive-api.open-meteo.com/v1/archive"),
                "home",
                "Home",
                51.674304,
                -0.785602,
                ZoneId.of("Europe/London"),
                Duration.ofSeconds(30),
                Duration.ofSeconds(60),
                Optional.empty(),
                Optional.empty(),
                365,
                false,
                "openmeteo",
                "Location",
                "DailyWeather",
                batchSize,
                Duration.ofSeconds(30));
    }

    private static DailyWeatherRecord record(final int day) {
        return new DailyWeatherRecord(
                LocalDate.of(2026, 7, day),
                "Home",
                51.674304,
                -0.785602,
                10.0,
                20.0,
                15.0,
                LocalTime.of(5, 0),
                LocalTime.of(21, 0),
                960,
                1,
                "Clear");
    }

    private static final class Fixture {

        private final DatabaseResourceManager database
                = mock(DatabaseResourceManager.class);
        private final Connection connection = mock(Connection.class);
        private final Statement sqlStatement = mock(Statement.class);
        private final PreparedStatement lockStatement
                = mock(PreparedStatement.class);
        private final PreparedStatement updateLocationStatement
                = mock(PreparedStatement.class);
        private final PreparedStatement selectLocationStatement
                = mock(PreparedStatement.class);
        private final PreparedStatement stageStatement
                = mock(PreparedStatement.class);
        private final PreparedStatement updateDailyStatement
                = mock(PreparedStatement.class);
        private final PreparedStatement insertDailyStatement
                = mock(PreparedStatement.class);
        private final ResultSet lockResult = mock(ResultSet.class);
        private final ResultSet locationResult = mock(ResultSet.class);
        private final OpenMeteoConfiguration configuration;
        private final OpenMeteoRepository repository;

        private Fixture(final int batchSize) throws Exception {
            configuration = configuration(batchSize);
            repository = new OpenMeteoRepository(database);

            when(database.getConnection()).thenReturn(connection);
            when(connection.getAutoCommit()).thenReturn(true, false);
            when(connection.createStatement()).thenReturn(sqlStatement);
            when(sqlStatement.execute(anyString())).thenReturn(true);
            when(connection.prepareStatement(anyString())).thenAnswer(invocation -> {
                final String sql = invocation.getArgument(0, String.class);
                final String trimmed = sql.trim();
                if (trimmed.contains("sp_getapplock")) {
                    return lockStatement;
                }
                if (trimmed.startsWith("UPDATE [openmeteo].[Location]")) {
                    return updateLocationStatement;
                }
                if (trimmed.startsWith("SELECT [LocationId]")) {
                    return selectLocationStatement;
                }
                if (trimmed.startsWith("INSERT INTO #OpenMeteoDaily")) {
                    return stageStatement;
                }
                if (trimmed.startsWith("UPDATE target")) {
                    return updateDailyStatement;
                }
                if (trimmed.startsWith("INSERT INTO [openmeteo].[DailyWeather]")) {
                    return insertDailyStatement;
                }
                throw new AssertionError("Unexpected prepared SQL: " + sql);
            });

            when(lockStatement.executeQuery()).thenReturn(lockResult);
            when(lockResult.next()).thenReturn(true);
            when(lockResult.getInt(1)).thenReturn(0);
            when(updateLocationStatement.executeUpdate()).thenReturn(1);
            when(selectLocationStatement.executeQuery()).thenReturn(locationResult);
            when(locationResult.next()).thenReturn(true);
            when(locationResult.getLong(1)).thenReturn(10L);
        }
    }
}
