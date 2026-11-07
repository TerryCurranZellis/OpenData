/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.load;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.common.database.jdbc.JdbcBatchExecutor;
import com.towermarsh.opendata.common.database.jdbc.JdbcTransactionTemplate;
import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import com.towermarsh.opendata.validation.SqlIdentifiers;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Transactional and idempotent SQL Server writer for Open-Meteo daily data.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
public class OpenMeteoRepository {

    private static final String STAGE_TABLE = "#OpenMeteoDaily";

    private final JdbcTransactionTemplate transactions;

    /**
     * Creates a repository backed by the supplied database resource manager.
     *
     * @param database database resource manager
     * @since 2.0.0
     */
    public OpenMeteoRepository(DatabaseResourceManager database) {
        transactions = new JdbcTransactionTemplate(
                Objects.requireNonNull(database, "database"));
    }

    /**
     * Saves transformed daily weather records transactionally.
     *
     * @param configuration typed Open-Meteo configuration
     * @param records transformed daily weather records
     * @param runId plugin run identifier
     * @return persistence result summary
     * @since 2.0.0
     */
    public OpenMeteoPersistenceResult save(
            OpenMeteoConfiguration configuration,
            List<DailyWeatherRecord> records,
            UUID runId) {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(runId, "runId");
        if (records.isEmpty()) {
            return new OpenMeteoPersistenceResult(0, 0, 0);
        }

        return transactions.execute(
                "Unable to persist Open-Meteo daily weather data.",
                connection -> save(connection, configuration, records, runId),
                OpenMeteoRepository::resetPooledSession);
    }

    /**
     * Save.
     *
     * @param connection input connection
     * @param configuration input configuration
     * @param records input records
     * @param runId requested run id
     * @return result of save
     * @throws SQLException if save fails
     */
    private static OpenMeteoPersistenceResult save(
            Connection connection,
            OpenMeteoConfiguration configuration,
            List<DailyWeatherRecord> records,
            UUID runId) throws SQLException {
        execute(connection, "DROP TABLE IF EXISTS " + STAGE_TABLE);
        execute(connection, "SET XACT_ABORT ON");
        acquireApplicationLock(connection, configuration);

        var locationId = upsertLocation(connection, configuration);
        createStageTable(connection);
        var staged = stage(
                connection,
                records,
                configuration.databaseBatchSize());
        if (staged != records.size()) {
            throw new SQLException(
                    "Open-Meteo staging affected " + staged
                            + " rows for " + records.size() + " records.");
        }

        var updated = updateExisting(
                connection,
                configuration,
                locationId,
                runId);
        var inserted = insertMissing(
                connection,
                configuration,
                locationId,
                runId);
        var skipped = records.size() - inserted - updated;

        return new OpenMeteoPersistenceResult(
                inserted,
                updated,
                Math.max(0, skipped));
    }

    /**
     * Acquire application lock.
     *
     * @param connection input connection
     * @param configuration input configuration
     * @throws SQLException if acquire application lock fails
     */
    private static void acquireApplicationLock(
            Connection connection,
            OpenMeteoConfiguration configuration) throws SQLException {
        var sql = """
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
            statement.setString(
                    1, "OpenData:openmeteo:" + configuration.locationKey());
            statement.setInt(
                    2, Math.toIntExact(configuration.databaseLockTimeout().toMillis()));
            try (var result = statement.executeQuery()) {
                if (!result.next() || result.getInt(1) < 0) {
                    throw new SQLException(
                            "Unable to acquire Open-Meteo transaction application lock.");
                }
            }
        }
    }

    /**
     * Upsert location.
     *
     * @param connection input connection
     * @param configuration input configuration
     * @return result of upsert location
     * @throws SQLException if upsert location fails
     */
    private static long upsertLocation(
            Connection connection,
            OpenMeteoConfiguration configuration) throws SQLException {
        var table = SqlIdentifiers.qualify(
                configuration.targetSchema(),
                configuration.locationTable());
        var update = (
                "UPDATE %s WITH (UPDLOCK, HOLDLOCK) "
                        + "SET [LocationName] = ?, [Latitude] = ?, [Longitude] = ?, "
                        + "[TimeZone] = ?, [UpdatedAt] = SYSUTCDATETIME() "
                        + "WHERE [LocationKey] = ?")
                .formatted(table);
        try (var statement = connection.prepareStatement(update)) {
            statement.setString(1, configuration.locationName());
            statement.setDouble(2, configuration.latitude());
            statement.setDouble(3, configuration.longitude());
            statement.setString(4, configuration.timezone().getId());
            statement.setString(5, configuration.locationKey());
            if (statement.executeUpdate() == 0) {
                insertLocation(connection, configuration, table);
            }
        }

        var select = "SELECT [LocationId] FROM %s WITH "
                + "(UPDLOCK, HOLDLOCK) WHERE [LocationKey] = ?";
        try (var statement = connection.prepareStatement(select.formatted(table))) {
            statement.setString(1, configuration.locationKey());
            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException(
                            "Open-Meteo location row was not found after upsert.");
                }
                return result.getLong(1);
            }
        }
    }

    /**
     * Insert location.
     *
     * @param connection input connection
     * @param configuration input configuration
     * @param table input table
     * @throws SQLException if insert location fails
     */
    private static void insertLocation(
            Connection connection,
            OpenMeteoConfiguration configuration,
            String table) throws SQLException {
        var insert = (
                "INSERT INTO %s "
                        + "([LocationKey], [LocationName], [Latitude], [Longitude], [TimeZone]) "
                        + "VALUES (?, ?, ?, ?, ?)")
                .formatted(table);
        try (var statement = connection.prepareStatement(insert)) {
            statement.setString(1, configuration.locationKey());
            statement.setString(2, configuration.locationName());
            statement.setDouble(3, configuration.latitude());
            statement.setDouble(4, configuration.longitude());
            statement.setString(5, configuration.timezone().getId());
            statement.executeUpdate();
        }
    }

    /**
     * Creates stage table.
     *
     * @param connection input connection
     * @throws SQLException if create stage table fails
     */
    private static void createStageTable(Connection connection) throws SQLException {
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

    /**
     * Stage.
     *
     * @param connection input connection
     * @param records input records
     * @param batchSize input batch size
     * @return result of stage
     * @throws SQLException if stage fails
     */
    private static int stage(
            Connection connection,
            List<DailyWeatherRecord> records,
            int batchSize) throws SQLException {
        var sql = """
                INSERT INTO #OpenMeteoDaily
                    ([ObservationDate], [MinimumTemperatureC], [MaximumTemperatureC],
                     [MeanTemperatureC], [Sunrise], [Sunset], [DaylightMinutes],
                     [WeatherCode], [WeatherDescription])
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return JdbcBatchExecutor.execute(
                connection,
                sql,
                records,
                batchSize,
                (statement, record) -> {
                    statement.setDate(1, Date.valueOf(record.observationDate()));
                    statement.setDouble(2, record.minimumTemperatureC());
                    statement.setDouble(3, record.maximumTemperatureC());
                    statement.setDouble(4, record.meanTemperatureC());
                    statement.setTime(5, Time.valueOf(record.sunrise()));
                    statement.setTime(6, Time.valueOf(record.sunset()));
                    statement.setLong(7, record.daylightMinutes());
                    statement.setInt(8, record.weatherCode());
                    statement.setString(9, record.weatherDescription());
                });
    }

    /**
     * Updates existing.
     *
     * @param connection input connection
     * @param configuration input configuration
     * @param locationId requested location id
     * @param runId requested run id
     * @return result of update existing
     * @throws SQLException if update existing fails
     */
    private static long updateExisting(
            Connection connection,
            OpenMeteoConfiguration configuration,
            long locationId,
            UUID runId) throws SQLException {
        var table = SqlIdentifiers.qualify(
                configuration.targetSchema(),
                configuration.dailyTable());
        var sql = (
                "UPDATE target WITH (UPDLOCK, HOLDLOCK) "
                        + "SET [MinimumTemperatureC] = source.[MinimumTemperatureC], "
                        + "[MaximumTemperatureC] = source.[MaximumTemperatureC], "
                        + "[MeanTemperatureC] = source.[MeanTemperatureC], "
                        + "[Sunrise] = source.[Sunrise], "
                        + "[Sunset] = source.[Sunset], "
                        + "[DaylightMinutes] = source.[DaylightMinutes], "
                        + "[WeatherCode] = source.[WeatherCode], "
                        + "[WeatherDescription] = source.[WeatherDescription], "
                        + "[LastRunId] = ?, "
                        + "[UpdatedAt] = SYSUTCDATETIME() "
                        + "FROM %s AS target "
                        + "JOIN #OpenMeteoDaily AS source "
                        + "ON source.[ObservationDate] = target.[ObservationDate] "
                        + "WHERE target.[LocationId] = ? "
                        + "AND (target.[MinimumTemperatureC] <> source.[MinimumTemperatureC] "
                        + "OR target.[MaximumTemperatureC] <> source.[MaximumTemperatureC] "
                        + "OR target.[MeanTemperatureC] <> source.[MeanTemperatureC] "
                        + "OR target.[Sunrise] <> source.[Sunrise] "
                        + "OR target.[Sunset] <> source.[Sunset] "
                        + "OR target.[DaylightMinutes] <> source.[DaylightMinutes] "
                        + "OR target.[WeatherCode] <> source.[WeatherCode] "
                        + "OR target.[WeatherDescription] <> source.[WeatherDescription])")
                .formatted(table);
        try (var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, runId);
            statement.setLong(2, locationId);
            return statement.executeUpdate();
        }
    }

    /**
     * Insert missing.
     *
     * @param connection input connection
     * @param configuration input configuration
     * @param locationId requested location id
     * @param runId requested run id
     * @return result of insert missing
     * @throws SQLException if insert missing fails
     */
    private static long insertMissing(
            Connection connection,
            OpenMeteoConfiguration configuration,
            long locationId,
            UUID runId) throws SQLException {
        var table = SqlIdentifiers.qualify(
                configuration.targetSchema(),
                configuration.dailyTable());
        var sql = (
                "INSERT INTO %s "
                        + "([LocationId], [ObservationDate], [MinimumTemperatureC], "
                        + "[MaximumTemperatureC], [MeanTemperatureC], [Sunrise], [Sunset], "
                        + "[DaylightMinutes], [WeatherCode], [WeatherDescription], [LastRunId]) "
                        + "SELECT ?, source.[ObservationDate], source.[MinimumTemperatureC], "
                        + "source.[MaximumTemperatureC], source.[MeanTemperatureC], "
                        + "source.[Sunrise], source.[Sunset], source.[DaylightMinutes], "
                        + "source.[WeatherCode], source.[WeatherDescription], ? "
                        + "FROM #OpenMeteoDaily AS source "
                        + "WHERE NOT EXISTS ("
                        + "SELECT 1 FROM %s AS target WITH (UPDLOCK, HOLDLOCK) "
                        + "WHERE target.[LocationId] = ? "
                        + "AND target.[ObservationDate] = source.[ObservationDate])")
                .formatted(table, table);
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, locationId);
            statement.setObject(2, runId);
            statement.setLong(3, locationId);
            return statement.executeUpdate();
        }
    }

    /**
     * Execute.
     *
     * @param connection input connection
     * @param sql input sql
     * @throws SQLException if execute fails
     */
    private static void execute(
            Connection connection,
            String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * Reset pooled session.
     *
     * @param connection input connection
     * @throws SQLException if reset pooled session fails
     */
    private static void resetPooledSession(Connection connection)
            throws SQLException {
        SQLException failure = null;
        try {
            execute(connection, "DROP TABLE IF EXISTS " + STAGE_TABLE);
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
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Combine.
     *
     * @param existing input existing
     * @param additional input additional
     * @return result of combine
     */
    private static SQLException combine(
            SQLException existing,
            SQLException additional) {
        if (existing == null) {
            return additional;
        }
        existing.addSuppressed(additional);
        return existing;
    }
}
