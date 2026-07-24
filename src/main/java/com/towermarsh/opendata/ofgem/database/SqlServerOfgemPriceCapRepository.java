/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ofgem.database;

import com.towermarsh.opendata.database.DatabaseConnectionManager;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapLevel;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapPeriod;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

/**
 * SQL Server persistence for the levelised Ofgem price-cap output.
 */
public final class SqlServerOfgemPriceCapRepository
        implements OfgemPriceCapRepository {

    private static final String FIND_PERIOD_SQL = """
            SELECT price_cap_period_id
            FROM ofgem.price_cap_period
            WHERE effective_from = ? AND effective_to = ?
            """;

    private static final String UPDATE_PERIOD_SQL = """
            UPDATE ofgem.price_cap_period
            SET period_name = ?,
                source_column_reference = ?,
                source_file_id = ?,
                is_current = ?,
                updated_at = SYSUTCDATETIME()
            WHERE price_cap_period_id = ?
            """;

    private static final String INSERT_PERIOD_SQL = """
            INSERT INTO ofgem.price_cap_period
                (period_name, effective_from, effective_to,
                 source_column_reference, source_file_id, is_current)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String CLEAR_CURRENT_SQL = """
            UPDATE ofgem.price_cap_period
            SET is_current = 0,
                updated_at = SYSUTCDATETIME()
            WHERE is_current = 1
            """;

    private static final String DELETE_LEVELS_SQL = """
            DELETE FROM ofgem.price_cap_level
            WHERE price_cap_period_id = ?
            """;

    private static final String INSERT_LEVEL_SQL = """
            INSERT INTO ofgem.price_cap_level
                (price_cap_period_id, region_code, payment_method_code,
                 tariff_type_code, consumption_basis_code, amount_gbp,
                 vat_included, source_sheet, source_cell, ingestion_run_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private final DatabaseConnectionManager connectionManager;

    public SqlServerOfgemPriceCapRepository(
            DatabaseConnectionManager connectionManager) {
        this.connectionManager = Objects.requireNonNull(
                connectionManager, "connectionManager");
    }

    @Override
    public long upsertPeriod(
            OfgemPriceCapPeriod period,
            long sourceFileId) throws SQLException {
        Objects.requireNonNull(period, "period");
        try (Connection connection = connectionManager.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                if (period.current()) {
                    try (PreparedStatement clearCurrent = connection.prepareStatement(
                            CLEAR_CURRENT_SQL)) {
                        clearCurrent.executeUpdate();
                    }
                }

                Long existingId = findPeriodId(connection, period);
                long periodId;
                if (existingId == null) {
                    periodId = insertPeriod(connection, period, sourceFileId);
                } else {
                    updatePeriod(connection, existingId, period, sourceFileId);
                    periodId = existingId;
                }
                connection.commit();
                return periodId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    @Override
    public int replaceLevels(
            long periodId,
            long ingestionRunId,
            List<OfgemPriceCapLevel> levels) throws SQLException {
        Objects.requireNonNull(levels, "levels");
        if (levels.isEmpty()) {
            throw new IllegalArgumentException("levels cannot be empty");
        }

        try (Connection connection = connectionManager.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement delete = connection.prepareStatement(
                        DELETE_LEVELS_SQL)) {
                    delete.setLong(1, periodId);
                    delete.executeUpdate();
                }

                int loaded = 0;
                try (PreparedStatement insert = connection.prepareStatement(
                        INSERT_LEVEL_SQL)) {
                    for (OfgemPriceCapLevel level : levels) {
                        insert.setLong(1, periodId);
                        insert.setString(2, level.regionCode());
                        insert.setString(3, level.paymentMethodCode());
                        insert.setString(4, level.tariffTypeCode());
                        insert.setString(5, level.consumptionBasisCode());
                        insert.setBigDecimal(6, level.amountGbp());
                        insert.setBoolean(7, level.vatIncluded());
                        insert.setString(8, level.sourceSheet());
                        insert.setString(9, level.sourceCell());
                        insert.setLong(10, ingestionRunId);
                        insert.addBatch();
                    }
                    int[] results = insert.executeBatch();
                    for (int result : results) {
                        if (result == Statement.EXECUTE_FAILED) {
                            throw new SQLException(
                                    "SQL Server reported a failed Ofgem batch row");
                        }
                        if (result == Statement.SUCCESS_NO_INFO) {
                            loaded++;
                        } else if (result > 0) {
                            loaded += result;
                        }
                    }
                }
                connection.commit();
                return loaded;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private static Long findPeriodId(
            Connection connection,
            OfgemPriceCapPeriod period) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_PERIOD_SQL)) {
            statement.setDate(1, Date.valueOf(period.effectiveFrom()));
            statement.setDate(2, Date.valueOf(period.effectiveTo()));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong(1) : null;
            }
        }
    }

    private static long insertPeriod(
            Connection connection,
            OfgemPriceCapPeriod period,
            long sourceFileId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                INSERT_PERIOD_SQL,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, period.periodName());
            statement.setDate(2, Date.valueOf(period.effectiveFrom()));
            statement.setDate(3, Date.valueOf(period.effectiveTo()));
            setNullableInteger(statement, 4, period.sourceColumnReference());
            statement.setLong(5, sourceFileId);
            statement.setBoolean(6, period.current());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No generated price-cap period key returned");
                }
                return keys.getLong(1);
            }
        }
    }

    private static void updatePeriod(
            Connection connection,
            long periodId,
            OfgemPriceCapPeriod period,
            long sourceFileId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                UPDATE_PERIOD_SQL)) {
            statement.setString(1, period.periodName());
            setNullableInteger(statement, 2, period.sourceColumnReference());
            statement.setLong(3, sourceFileId);
            statement.setBoolean(4, period.current());
            statement.setLong(5, periodId);
            statement.executeUpdate();
        }
    }

    private static void setNullableInteger(
            PreparedStatement statement,
            int index,
            Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }
}
