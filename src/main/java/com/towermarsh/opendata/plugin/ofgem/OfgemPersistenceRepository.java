/*
 * Filename: OfgemPersistenceRepository.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapLevel;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapPeriod;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapWorkbookData;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

/** Transactional SQL Server persistence for one Ofgem workbook. */
final class OfgemPersistenceRepository {
    private final DatabaseResourceManager database;

    OfgemPersistenceRepository(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    OfgemPersistenceResult persist(
            final PluginDefinition definition,
            final ResolvedDownload download,
            final OfgemPriceCapWorkbookData data) {
        try (Connection connection = database.getConnection()) {
            final boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                final long datasetId = requireDatasetId(connection, definition.datasetId());
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
                final int inserted = insertLevels(connection, periodId, ingestionRunId, data.levels());
                completeRun(connection, ingestionRunId, data.levels().size(), inserted);
                connection.commit();
                return new OfgemPersistenceResult(
                        existingPeriodId == null ? inserted : 0,
                        updated,
                        0);
            } catch (SQLException | IOException | NoSuchAlgorithmException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException | IOException | NoSuchAlgorithmException exception) {
            throw new DatabaseAccessException("Unable to persist Ofgem price-cap data", exception);
        }
    }

    private static long requireDatasetId(final Connection c, final String datasetCode) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(
                "SELECT dataset_id FROM core.dataset WHERE dataset_code = ?")) {
            s.setString(1, datasetCode);
            try (ResultSet r = s.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Dataset is not seeded in core.dataset: " + datasetCode);
                }
                return r.getLong(1);
            }
        }
    }

    private static long insertRun(
            final Connection c,
            final long datasetId,
            final ResolvedDownload download) throws SQLException {
        final String sql = """
                INSERT INTO core.ingestion_run
                    (dataset_id, status, source_page_url)
                VALUES (?, 'STARTED', ?)
                """;
        try (PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setLong(1, datasetId);
            s.setString(2, download.requestedUri().toString());
            s.executeUpdate();
            return generatedKey(s, "ingestion run");
        }
    }

    private static long insertSourceFile(
            final Connection c,
            final long ingestionRunId,
            final ResolvedDownload download)
            throws SQLException, IOException, NoSuchAlgorithmException {
        final String sql = """
                INSERT INTO core.source_file
                    (ingestion_run_id, source_uri, original_file_name,
                     content_type, size_bytes, sha256, downloaded_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setLong(1, ingestionRunId);
            s.setString(2, download.resolvedUri().toString());
            s.setString(3, download.localFile().getFileName().toString());
            s.setString(4, download.contentType().orElse(null));
            s.setLong(5, download.byteCount());
            s.setString(6, sha256(download.localFile()));
            s.setObject(7, LocalDateTime.ofInstant(download.completedAtUtc(), ZoneOffset.UTC));
            s.executeUpdate();
            return generatedKey(s, "source file");
        }
    }

    private static Long findPeriodId(final Connection c, final OfgemPriceCapPeriod p)
            throws SQLException {
        try (PreparedStatement s = c.prepareStatement(
                "SELECT price_cap_period_id FROM ofgem.price_cap_period "
                        + "WHERE effective_from = ? AND effective_to = ?")) {
            s.setDate(1, Date.valueOf(p.effectiveFrom()));
            s.setDate(2, Date.valueOf(p.effectiveTo()));
            try (ResultSet r = s.executeQuery()) {
                return r.next() ? r.getLong(1) : null;
            }
        }
    }

    private static long insertPeriod(
            final Connection c,
            final OfgemPriceCapPeriod p,
            final long sourceFileId) throws SQLException {
        final String sql = """
                INSERT INTO ofgem.price_cap_period
                    (period_name, effective_from, effective_to,
                     source_column_reference, source_file_id, is_current)
                VALUES (?, ?, ?, ?, ?, 1)
                """;
        try (PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPeriod(s, p, sourceFileId);
            s.executeUpdate();
            return generatedKey(s, "price-cap period");
        }
    }

    private static void updatePeriod(
            final Connection c,
            final long periodId,
            final OfgemPriceCapPeriod p,
            final long sourceFileId) throws SQLException {
        final String sql = """
                UPDATE ofgem.price_cap_period
                   SET period_name = ?, effective_from = ?, effective_to = ?,
                       source_column_reference = ?, source_file_id = ?,
                       is_current = 1, updated_at = SYSUTCDATETIME()
                 WHERE price_cap_period_id = ?
                """;
        try (PreparedStatement s = c.prepareStatement(sql)) {
            setPeriod(s, p, sourceFileId);
            s.setLong(6, periodId);
            s.executeUpdate();
        }
    }

    private static void setPeriod(
            final PreparedStatement s,
            final OfgemPriceCapPeriod p,
            final long sourceFileId) throws SQLException {
        s.setString(1, p.periodName());
        s.setDate(2, Date.valueOf(p.effectiveFrom()));
        s.setDate(3, Date.valueOf(p.effectiveTo()));
        if (p.sourceColumnReference() == null) {
            s.setNull(4, Types.INTEGER);
        } else {
            s.setInt(4, p.sourceColumnReference());
        }
        s.setLong(5, sourceFileId);
    }

    private static void clearCurrentFlag(final Connection c) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(
                "UPDATE ofgem.price_cap_period SET is_current = 0, "
                        + "updated_at = SYSUTCDATETIME() WHERE is_current = 1")) {
            s.executeUpdate();
        }
    }

    private static void deleteLevels(final Connection c, final long periodId) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(
                "DELETE FROM ofgem.price_cap_level WHERE price_cap_period_id = ?")) {
            s.setLong(1, periodId);
            s.executeUpdate();
        }
    }

    private static int insertLevels(
            final Connection c,
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
        try (PreparedStatement s = c.prepareStatement(sql)) {
            for (OfgemPriceCapLevel level : levels) {
                s.setLong(1, periodId);
                s.setString(2, level.regionCode());
                s.setString(3, level.paymentMethodCode());
                s.setString(4, level.tariffTypeCode());
                s.setString(5, level.consumptionBasisCode());
                s.setBigDecimal(6, level.amountGbp());
                s.setBoolean(7, level.vatIncluded());
                s.setString(8, level.sourceSheet());
                s.setString(9, level.sourceCell());
                s.setLong(10, ingestionRunId);
                s.addBatch();
            }
            int count = 0;
            for (int result : s.executeBatch()) {
                count += result == Statement.SUCCESS_NO_INFO ? 1 : Math.max(result, 0);
            }
            return count;
        }
    }

    private static void completeRun(
            final Connection c,
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
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, extracted);
            s.setInt(2, loaded);
            s.setString(3, "Ofgem price-cap workbook imported successfully");
            s.setLong(4, ingestionRunId);
            s.executeUpdate();
        }
    }

    private static long generatedKey(final PreparedStatement s, final String description)
            throws SQLException {
        try (ResultSet keys = s.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("No generated key returned for " + description);
            }
            return keys.getLong(1);
        }
    }

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
