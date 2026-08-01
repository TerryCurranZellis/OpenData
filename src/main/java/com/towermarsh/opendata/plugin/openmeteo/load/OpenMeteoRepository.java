/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.load;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.openmeteo.config.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Transactional and idempotent SQL Server writer for Open-Meteo daily data.  *
* @author Terry Curran
* @version 1.0.0
*/
public final class OpenMeteoRepository {
    private final DatabaseResourceManager database;

    /**
     * Creates a repository backed by the supplied database resource manager.
     *
     * @param database database resource manager
     */
    public OpenMeteoRepository(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    /**
     * Saves transformed daily weather records transactionally.
     *
     * @param configuration typed Open-Meteo configuration
     * @param records transformed daily weather records
     * @param runId plugin run identifier
     * @return persistence result summary
     */
    @SuppressWarnings("ThrowFromFinallyBlock")
    public OpenMeteoPersistenceResult save(
            final OpenMeteoConfiguration configuration,
            final List<DailyWeatherRecord> records,
            final UUID runId) {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(runId, "runId");
        if (records.isEmpty()) {
            return new OpenMeteoPersistenceResult(0, 0, 0);
        }

        try (Connection connection = database.getConnection()) {
            final boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            Exception primaryFailure = null;
            try {
                execute(connection, "DROP TABLE IF EXISTS #OpenMeteoDaily");
                execute(connection, "SET XACT_ABORT ON");
                acquireApplicationLock(connection, configuration);
                final long locationId = upsertLocation(connection, configuration);
                createStageTable(connection);
                stage(connection, records, configuration.databaseBatchSize());
                final long updated = updateExisting(connection, configuration, locationId, runId);
                final long inserted = insertMissing(connection, configuration, locationId, runId);
                final long skipped = records.size() - inserted - updated;
                connection.commit();
                return new OpenMeteoPersistenceResult(inserted, updated, Math.max(0, skipped));
            } catch (SQLException | RuntimeException exception) {
                primaryFailure = exception;
                rollback(connection, exception);
                throw exception;
            } finally {
                final SQLException cleanupFailure = resetPooledSession(connection, originalAutoCommit);
                if (cleanupFailure != null) {
                    if (primaryFailure != null) {
                        primaryFailure.addSuppressed(cleanupFailure);
                    } else {
                        throw cleanupFailure;
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to persist Open-Meteo daily weather data.", exception);
        }
    }

    private static void acquireApplicationLock(
            final Connection connection,
            final OpenMeteoConfiguration configuration) throws SQLException {
        final String sql = """
                DECLARE @result int;
                EXEC @result = sys.sp_getapplock
                    @Resource = ?,
                    @LockMode = 'Exclusive',
                    @LockOwner = 'Transaction',
                    @LockTimeout = ?,
                    @DbPrincipal = 'public';
                SELECT @result;
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, "OpenData:openmeteo:" + configuration.locationKey());
            statement.setInt(2, Math.toIntExact(configuration.databaseLockTimeout().toMillis()));
            try (var result = statement.executeQuery()) {
                if (!result.next() || result.getInt(1) < 0) {
                    throw new SQLException("Unable to acquire Open-Meteo transaction application lock.");
                }
            }
        }
    }

    private static long upsertLocation(
            final Connection connection,
            final OpenMeteoConfiguration configuration) throws SQLException {
        final String table = qualified(configuration.targetSchema(), configuration.locationTable());
        final String update = """
                UPDATE %s WITH (UPDLOCK, HOLDLOCK)
                   SET [LocationName] = ?, [Latitude] = ?, [Longitude] = ?,
                       [TimeZone] = ?, [UpdatedAt] = SYSUTCDATETIME()
                 WHERE [LocationKey] = ?
                """.formatted(table);
        try (var statement = connection.prepareStatement(update)) {
            statement.setString(1, configuration.locationName());
            statement.setDouble(2, configuration.latitude());
            statement.setDouble(3, configuration.longitude());
            statement.setString(4, configuration.timezone().getId());
            statement.setString(5, configuration.locationKey());
            if (statement.executeUpdate() == 0) {
                final String insert = """
                        INSERT INTO %s
                            ([LocationKey], [LocationName], [Latitude], [Longitude], [TimeZone])
                        VALUES (?, ?, ?, ?, ?)
                        """.formatted(table);
                try (var insertStatement = connection.prepareStatement(insert)) {
                    insertStatement.setString(1, configuration.locationKey());
                    insertStatement.setString(2, configuration.locationName());
                    insertStatement.setDouble(3, configuration.latitude());
                    insertStatement.setDouble(4, configuration.longitude());
                    insertStatement.setString(5, configuration.timezone().getId());
                    insertStatement.executeUpdate();
                }
            }
        }
        final String select = "SELECT [LocationId] FROM %s WITH (UPDLOCK, HOLDLOCK) WHERE [LocationKey] = ?"
                .formatted(table);
        try (var statement = connection.prepareStatement(select)) {
            statement.setString(1, configuration.locationKey());
            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("Open-Meteo location row was not found after upsert.");
                }
                return result.getLong(1);
            }
        }
    }

    /**
     * Creates the temporary staging table used for one load operation.
     *
     * @param connection open database connection
     * @throws SQLException if the staging table cannot be created
     */
    private static void createStageTable(final Connection connection) throws SQLException {
        execute(connection, """
                CREATE TABLE #OpenMeteoDaily (
                    [ObservationDate] date NOT NULL PRIMARY KEY,
                    [MinimumTemperatureC] decimal(6,2) NOT NULL,
                    [MaximumTemperatureC] decimal(6,2) NOT NULL,
                    [MeanTemperatureC] decimal(6,2) NOT NULL,
                    [Sunrise] time(0) NOT NULL,
                    [Sunset] time(0) NOT NULL,
                    [DaylightMinutes] smallint NOT NULL,
                    [WeatherCode] smallint NOT NULL,
                    [WeatherDescription] nvarchar(200) NOT NULL
                )
                """);
    }

    private static void stage(
            final Connection connection,
            final List<DailyWeatherRecord> records,
            final int batchSize) throws SQLException {
        final String sql = """
                INSERT INTO #OpenMeteoDaily
                    ([ObservationDate], [MinimumTemperatureC], [MaximumTemperatureC],
                     [MeanTemperatureC], [Sunrise], [Sunset], [DaylightMinutes],
                     [WeatherCode], [WeatherDescription])
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (var statement = connection.prepareStatement(sql)) {
            int pending = 0;
            for (DailyWeatherRecord record : records) {
                statement.setDate(1, Date.valueOf(record.observationDate()));
                statement.setDouble(2, record.minimumTemperatureC());
                statement.setDouble(3, record.maximumTemperatureC());
                statement.setDouble(4, record.meanTemperatureC());
                statement.setTime(5, Time.valueOf(record.sunrise()));
                statement.setTime(6, Time.valueOf(record.sunset()));
                statement.setLong(7, record.daylightMinutes());
                statement.setInt(8, record.weatherCode());
                statement.setString(9, record.weatherDescription());
                statement.addBatch();
                if (++pending == batchSize) {
                    statement.executeBatch();
                    pending = 0;
                }
            }
            if (pending > 0) {
                statement.executeBatch();
            }
        }
    }

    private static long updateExisting(
            final Connection connection,
            final OpenMeteoConfiguration configuration,
            final long locationId,
            final UUID runId) throws SQLException {
        final String table = qualified(configuration.targetSchema(), configuration.dailyTable());
        final String sql = """
                UPDATE target WITH (UPDLOCK, HOLDLOCK)
                   SET [MinimumTemperatureC] = source.[MinimumTemperatureC],
                       [MaximumTemperatureC] = source.[MaximumTemperatureC],
                       [MeanTemperatureC] = source.[MeanTemperatureC],
                       [Sunrise] = source.[Sunrise],
                       [Sunset] = source.[Sunset],
                       [DaylightMinutes] = source.[DaylightMinutes],
                       [WeatherCode] = source.[WeatherCode],
                       [WeatherDescription] = source.[WeatherDescription],
                       [LastRunId] = ?,
                       [UpdatedAt] = SYSUTCDATETIME()
                  FROM %s AS target
                  JOIN #OpenMeteoDaily AS source
                    ON source.[ObservationDate] = target.[ObservationDate]
                 WHERE target.[LocationId] = ?
                   AND (target.[MinimumTemperatureC] <> source.[MinimumTemperatureC]
                     OR target.[MaximumTemperatureC] <> source.[MaximumTemperatureC]
                     OR target.[MeanTemperatureC] <> source.[MeanTemperatureC]
                     OR target.[Sunrise] <> source.[Sunrise]
                     OR target.[Sunset] <> source.[Sunset]
                     OR target.[DaylightMinutes] <> source.[DaylightMinutes]
                     OR target.[WeatherCode] <> source.[WeatherCode]
                     OR target.[WeatherDescription] <> source.[WeatherDescription])
                """.formatted(table);
        try (var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, runId);
            statement.setLong(2, locationId);
            return statement.executeUpdate();
        }
    }

    private static long insertMissing(
            final Connection connection,
            final OpenMeteoConfiguration configuration,
            final long locationId,
            final UUID runId) throws SQLException {
        final String table = qualified(configuration.targetSchema(), configuration.dailyTable());
        final String sql = """
                INSERT INTO %s
                    ([LocationId], [ObservationDate], [MinimumTemperatureC],
                     [MaximumTemperatureC], [MeanTemperatureC], [Sunrise], [Sunset],
                     [DaylightMinutes], [WeatherCode], [WeatherDescription], [LastRunId])
                SELECT ?, source.[ObservationDate], source.[MinimumTemperatureC],
                       source.[MaximumTemperatureC], source.[MeanTemperatureC],
                       source.[Sunrise], source.[Sunset], source.[DaylightMinutes],
                       source.[WeatherCode], source.[WeatherDescription], ?
                  FROM #OpenMeteoDaily AS source
                 WHERE NOT EXISTS (
                       SELECT 1
                         FROM %s AS target WITH (UPDLOCK, HOLDLOCK)
                        WHERE target.[LocationId] = ?
                          AND target.[ObservationDate] = source.[ObservationDate])
                """.formatted(table, table);
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, locationId);
            statement.setObject(2, runId);
            statement.setLong(3, locationId);
            return statement.executeUpdate();
        }
    }

    /**
     * Builds a qualified SQL Server table name from validated identifiers.
     *
     * @param schema schema name
     * @param table table name
     * @return qualified table name
     */
    private static String qualified(final String schema, final String table) {
        return '[' + OpenMeteoConfiguration.sqlIdentifier(schema, "schema") + "].["
                + OpenMeteoConfiguration.sqlIdentifier(table, "table") + ']';
    }

    /**
     * Executes a SQL statement without parameters.
     *
     * @param connection open database connection
     * @param sql SQL statement to execute
     * @throws SQLException if execution fails
     */
    private static void execute(final Connection connection, final String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }


    /**
     * Removes connection-scoped state before a physical SQL Server session is
     * returned to the pool. Local temporary tables and SET options otherwise
     * survive a logical pooled-connection close.
     */
    private static SQLException resetPooledSession(
            final Connection connection,
            final boolean originalAutoCommit) {
        SQLException failure = null;
        try {
            execute(connection, "DROP TABLE IF EXISTS #OpenMeteoDaily");
        } catch (SQLException exception) {
            failure = exception;
        }
        try {
            execute(connection, "SET XACT_ABORT OFF");
        } catch (SQLException exception) {
            failure = combine(failure, exception);
        }
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException exception) {
            failure = combine(failure, exception);
        }
        try {
            connection.setAutoCommit(originalAutoCommit);
        } catch (SQLException exception) {
            failure = combine(failure, exception);
        }
        return failure;
    }

    /**
     * Combines cleanup SQL exceptions into a single throwable chain.
     *
     * @param existing existing accumulated exception
     * @param additional additional cleanup failure
     * @return combined exception reference
     */
    private static SQLException combine(
            final SQLException existing,
            final SQLException additional) {
        if (existing == null) {
            return additional;
        }
        existing.addSuppressed(additional);
        return existing;
    }

    /**
     * Attempts to roll back the active database transaction.
     *
     * @param connection open database connection
     * @param original original failure that triggered the rollback
     */
    private static void rollback(final Connection connection, final Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }
}
