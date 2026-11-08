/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.load;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.common.database.jdbc.JdbcBatchExecutor;
import com.towermarsh.opendata.common.database.jdbc.JdbcTransactionTemplate;
import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapLevel;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapPeriod;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapWorkbookData;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/**
 * Transactional SQL Server persistence for one Ofgem workbook.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
public final class OfgemPersistenceRepository {

    private static final int LEVEL_BATCH_SIZE = 500;

    private final JdbcTransactionTemplate transactions;

    /**
     * Creates an Ofgem persistence repository.
     *
     * @param database database resource manager
     * @since 2.0.0
     */
    public OfgemPersistenceRepository(final DatabaseResourceManager database) {
        transactions = new JdbcTransactionTemplate(
                Objects.requireNonNull(database, "database"));
    }

    /**
     * Persists one transformed Ofgem price-cap workbook transactionally.
     *
     * @param definition resolved Ofgem plugin definition
     * @param download downloaded workbook metadata
     * @param data transformed workbook data
     * @return persistence statistics
     * @since 2.0.0
     */
    public OfgemPersistenceResult persist(
            final PluginDefinition definition,
            final ResolvedDownload download,
            final OfgemPriceCapWorkbookData data) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(download, "download");
        Objects.requireNonNull(data, "data");

        return transactions.execute(
                "Unable to persist Ofgem price-cap data",
                connection -> persist(connection, definition, download, data));
    }

    /**
     * Persist.
     *
     * @param connection input connection
     * @param definition input definition
     * @param download input download
     * @param data input data
     * @return result of persist
     * @throws SQLException if persist fails
     * @throws IOException if persist fails
     * @throws NoSuchAlgorithmException if persist fails
     */
    private static OfgemPersistenceResult persist(
            final Connection connection,
            final PluginDefinition definition,
            final ResolvedDownload download,
            final OfgemPriceCapWorkbookData data)
            throws SQLException, IOException, NoSuchAlgorithmException {
        final long datasetId = requireDatasetId(
                connection,
                definition.datasetId(),
                definition.id());
        final long ingestionRunId = insertRun(connection, datasetId, download);
        final long sourceFileId = insertSourceFile(connection, ingestionRunId, download);
        clearCurrentFlag(connection);

        final Long existingPeriodId = findPeriodId(connection, data.period());
        final long periodId;
        final long updated;
        if (existingPeriodId == null) {
            periodId = insertPeriod(connection, data.period(), sourceFileId);
            updated = 0;
        } else {
            periodId = existingPeriodId;
            updatePeriod(connection, periodId, data.period(), sourceFileId);
            updated = data.levels().size();
        }

        deleteLevels(connection, periodId);
        final int inserted = insertLevels(
                connection,
                periodId,
                ingestionRunId,
                data.levels());
        completeRun(connection, ingestionRunId, data.levels().size(), inserted);

        return new OfgemPersistenceResult(
                existingPeriodId == null ? inserted : 0,
                updated,
                0);
    }

    /**
     * Validates dataset id.
     *
     * @param connection input connection
     * @param datasetCode input dataset code
     * @param pluginCode input plugin code
     * @return result of require dataset id
     * @throws SQLException if require dataset id fails
     */
    private static long requireDatasetId(
            final Connection connection,
            final String datasetCode,
            final String pluginCode) throws SQLException {
        final String sql = """
                SELECT TOP (2) dataset_id
                FROM core.dataset
                WHERE dataset_code = ?
                   OR (plugin_code = ? AND is_active = 1)
                ORDER BY CASE WHEN dataset_code = ? THEN 0 ELSE 1 END,
                         dataset_id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, datasetCode);
            statement.setString(2, pluginCode);
            statement.setString(3, datasetCode);

            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    throw new SQLException(
                            "Dataset is not seeded in core.dataset: " + datasetCode
                                    + " (plugin " + pluginCode + ")");
                }

                final long datasetId = results.getLong(1);
                if (results.next()) {
                    throw new SQLException(
                            "More than one active dataset is configured for plugin: "
                                    + pluginCode);
                }
                return datasetId;
            }
        }
    }

    /**
     * Insert run.
     *
     * @param connection input connection
     * @param datasetId requested dataset id
     * @param download input download
     * @return result of insert run
     * @throws SQLException if insert run fails
     */
    private static long insertRun(
            final Connection connection,
            final long datasetId,
            final ResolvedDownload download) throws SQLException {
        final String sql = """
                INSERT INTO core.ingestion_run
                    (dataset_id, status, source_page_url)
                VALUES (?, 'STARTED', ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, datasetId);
            statement.setString(2, download.requestedUri().toString());
            statement.executeUpdate();
            return generatedKey(statement, "ingestion run");
        }
    }

    /**
     * Insert source file.
     *
     * @param connection input connection
     * @param ingestionRunId requested ingestion run id
     * @param download input download
     * @return result of insert source file
     * @throws SQLException if insert source file fails
     * @throws IOException if insert source file fails
     * @throws NoSuchAlgorithmException if insert source file fails
     */
    private static long insertSourceFile(
            final Connection connection,
            final long ingestionRunId,
            final ResolvedDownload download)
            throws SQLException, IOException, NoSuchAlgorithmException {
        final String sql = """
                INSERT INTO core.source_file
                    (ingestion_run_id, source_uri, original_file_name,
                     content_type, size_bytes, sha256, downloaded_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, ingestionRunId);
            statement.setString(2, download.resolvedUri().toString());
            statement.setString(3, localFileName(download.localFile()));
            statement.setString(4, download.contentType().orElse(null));
            statement.setLong(5, download.byteCount());
            statement.setString(6, sha256(download.localFile()));
            statement.setObject(
                    7,
                    LocalDateTime.ofInstant(download.completedAtUtc(), ZoneOffset.UTC));
            statement.executeUpdate();
            return generatedKey(statement, "source file");
        }
    }

    /**
     * Local file name.
     *
     * @param localFile target local file
     * @return result of local file name
     */
    private static String localFileName(final Path localFile) {
        final var fileName = Objects.requireNonNull(localFile, "localFile").getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("Path must include a file name: " + localFile);
        }
        return fileName.toString();
    }

    /**
     * Returns find period id.
     *
     * @param connection input connection
     * @param period input period
     * @return result of find period id
     * @throws SQLException if find period id fails
     */
    private static Long findPeriodId(
            final Connection connection,
            final OfgemPriceCapPeriod period) throws SQLException {
        final String sql = """
                SELECT price_cap_period_id
                FROM ofgem.price_cap_period
                WHERE effective_from = ?
                  AND effective_to = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(period.effectiveFrom()));
            statement.setDate(2, Date.valueOf(period.effectiveTo()));
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? results.getLong(1) : null;
            }
        }
    }

    /**
     * Insert period.
     *
     * @param connection input connection
     * @param period input period
     * @param sourceFileId target source file id
     * @return result of insert period
     * @throws SQLException if insert period fails
     */
    private static long insertPeriod(
            final Connection connection,
            final OfgemPriceCapPeriod period,
            final long sourceFileId) throws SQLException {
        final String sql = """
                INSERT INTO ofgem.price_cap_period
                    (period_name, effective_from, effective_to,
                     source_column_reference, source_file_id, is_current)
                VALUES (?, ?, ?, ?, ?, 1)
                """;
        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS)) {
            setPeriod(statement, period, sourceFileId);
            statement.executeUpdate();
            return generatedKey(statement, "price-cap period");
        }
    }

    /**
     * Updates period.
     *
     * @param connection input connection
     * @param periodId requested period id
     * @param period input period
     * @param sourceFileId target source file id
     * @throws SQLException if update period fails
     */
    private static void updatePeriod(
            final Connection connection,
            final long periodId,
            final OfgemPriceCapPeriod period,
            final long sourceFileId) throws SQLException {
        final String sql = """
                UPDATE ofgem.price_cap_period
                   SET period_name = ?, effective_from = ?, effective_to = ?,
                       source_column_reference = ?, source_file_id = ?,
                       is_current = 1, updated_at = SYSUTCDATETIME()
                 WHERE price_cap_period_id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setPeriod(statement, period, sourceFileId);
            statement.setLong(6, periodId);
            statement.executeUpdate();
        }
    }

    /**
     * Updates period.
     *
     * @param statement input statement
     * @param period input period
     * @param sourceFileId target source file id
     * @throws SQLException if set period fails
     */
    private static void setPeriod(
            final PreparedStatement statement,
            final OfgemPriceCapPeriod period,
            final long sourceFileId) throws SQLException {
        statement.setString(1, period.periodName());
        statement.setDate(2, Date.valueOf(period.effectiveFrom()));
        statement.setDate(3, Date.valueOf(period.effectiveTo()));
        if (period.sourceColumnReference() == null) {
            statement.setNull(4, Types.INTEGER);
        } else {
            statement.setInt(4, period.sourceColumnReference());
        }
        statement.setLong(5, sourceFileId);
    }

    /**
     * Clear current flag.
     *
     * @param connection input connection
     * @throws SQLException if clear current flag fails
     */
    private static void clearCurrentFlag(final Connection connection) throws SQLException {
        final String sql = """
                UPDATE ofgem.price_cap_period
                   SET is_current = 0,
                       updated_at = SYSUTCDATETIME()
                 WHERE is_current = 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    /**
     * Delete levels.
     *
     * @param connection input connection
     * @param periodId requested period id
     * @throws SQLException if delete levels fails
     */
    private static void deleteLevels(
            final Connection connection,
            final long periodId) throws SQLException {
        final String sql = """
                DELETE FROM ofgem.price_cap_level
                WHERE price_cap_period_id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, periodId);
            statement.executeUpdate();
        }
    }

    /**
     * Insert levels.
     *
     * @param connection input connection
     * @param periodId requested period id
     * @param ingestionRunId requested ingestion run id
     * @param levels input levels
     * @return result of insert levels
     * @throws SQLException if insert levels fails
     */
    private static int insertLevels(
            final Connection connection,
            final long periodId,
            final long ingestionRunId,
            final List<OfgemPriceCapLevel> levels) throws SQLException {
        final String sql = """
                INSERT INTO ofgem.price_cap_level
                    (price_cap_period_id, region_code, payment_method_code,
                     tariff_type_code, consumption_basis_code, amount_gbp,
                     vat_included, source_sheet, source_cell, ingestion_run_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return JdbcBatchExecutor.execute(
                connection,
                sql,
                levels,
                LEVEL_BATCH_SIZE,
                (statement, level) -> {
                    statement.setLong(1, periodId);
                    statement.setString(2, level.regionCode());
                    statement.setString(3, level.paymentMethodCode());
                    statement.setString(4, level.tariffTypeCode());
                    statement.setString(5, level.consumptionBasisCode());
                    statement.setBigDecimal(6, level.amountGbp());
                    statement.setBoolean(7, level.vatIncluded());
                    statement.setString(8, level.sourceSheet());
                    statement.setString(9, level.sourceCell());
                    statement.setLong(10, ingestionRunId);
                });
    }

    /**
     * Complete run.
     *
     * @param connection input connection
     * @param ingestionRunId requested ingestion run id
     * @param extracted input extracted
     * @param loaded input loaded
     * @throws SQLException if complete run fails
     */
    private static void completeRun(
            final Connection connection,
            final long ingestionRunId,
            final int extracted,
            final int loaded) throws SQLException {
        final String sql = """
                UPDATE core.ingestion_run
                   SET status = 'SUCCEEDED', finished_at = SYSUTCDATETIME(),
                       rows_extracted = ?, rows_loaded = ?, rows_rejected = 0,
                       status_message = ?
                 WHERE ingestion_run_id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, extracted);
            statement.setInt(2, loaded);
            statement.setString(3, "Ofgem price-cap workbook imported successfully");
            statement.setLong(4, ingestionRunId);
            statement.executeUpdate();
        }
    }

    /**
     * Generated key.
     *
     * @param statement input statement
     * @param description input description
     * @return result of generated key
     * @throws SQLException if generated key fails
     */
    private static long generatedKey(
            final PreparedStatement statement,
            final String description) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("No generated key returned for " + description);
            }
            return keys.getLong(1);
        }
    }

    /**
     * Sha256.
     *
     * @param file target file
     * @return result of sha256
     * @throws IOException if sha256 fails
     * @throws NoSuchAlgorithmException if sha256 fails
     */
    private static String sha256(final java.nio.file.Path file)
            throws IOException, NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream input = Files.newInputStream(file)) {
            final byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) >= 0) {
                if (read > 0) {
                    digest.update(buffer, 0, read);
                }
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
