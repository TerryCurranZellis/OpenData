/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.load;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapLevel;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapPeriod;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapWorkbookData;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Ofgem integration with the shared JDBC transaction and batch helpers.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
class OfgemPersistenceRepositoryTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void persistsNewPeriodUsingSharedTransactionAndBatchExecution() throws Exception {
        final DatabaseResourceManager database = mock(DatabaseResourceManager.class);
        final Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);

        final PreparedStatement datasetStatement = mock(PreparedStatement.class);
        final PreparedStatement clearCurrentStatement = mock(PreparedStatement.class);
        final PreparedStatement findPeriodStatement = mock(PreparedStatement.class);
        final PreparedStatement deleteLevelsStatement = mock(PreparedStatement.class);
        final PreparedStatement insertLevelsStatement = mock(PreparedStatement.class);
        final PreparedStatement completeRunStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(
                datasetStatement,
                clearCurrentStatement,
                findPeriodStatement,
                deleteLevelsStatement,
                insertLevelsStatement,
                completeRunStatement);

        final PreparedStatement insertRunStatement = mock(PreparedStatement.class);
        final PreparedStatement insertSourceFileStatement = mock(PreparedStatement.class);
        final PreparedStatement insertPeriodStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(
                anyString(),
                eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(
                        insertRunStatement,
                        insertSourceFileStatement,
                        insertPeriodStatement);

        final ResultSet datasetResults = mock(ResultSet.class);
        when(datasetStatement.executeQuery()).thenReturn(datasetResults);
        when(datasetResults.next()).thenReturn(true, false);
        when(datasetResults.getLong(1)).thenReturn(11L);

        generatedKey(insertRunStatement, 21L);
        generatedKey(insertSourceFileStatement, 31L);
        generatedKey(insertPeriodStatement, 41L);

        final ResultSet periodResults = mock(ResultSet.class);
        when(findPeriodStatement.executeQuery()).thenReturn(periodResults);
        when(periodResults.next()).thenReturn(false);
        when(insertLevelsStatement.executeBatch()).thenReturn(new int[]{1});

        final Path workbook = temporaryDirectory.resolve("price-cap.xlsx");
        Files.writeString(workbook, "Ofgem test workbook");

        final PluginDefinition definition = mock(PluginDefinition.class);
        when(definition.id()).thenReturn("ofgem");
        when(definition.datasetId()).thenReturn("ofgem-price-cap");

        final ResolvedDownload download = mock(ResolvedDownload.class);
        when(download.requestedUri()).thenReturn(URI.create("https://example.test/ofgem"));
        when(download.resolvedUri()).thenReturn(URI.create("https://example.test/ofgem.xlsx"));
        when(download.localFile()).thenReturn(workbook);
        when(download.contentType()).thenReturn(Optional.of(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        when(download.byteCount()).thenReturn(Files.size(workbook));
        when(download.completedAtUtc()).thenReturn(Instant.parse("2026-08-04T15:00:00Z"));

        final OfgemPriceCapPeriod period = mock(OfgemPriceCapPeriod.class);
        when(period.periodName()).thenReturn("October to December 2026");
        when(period.effectiveFrom()).thenReturn(LocalDate.of(2026, 10, 1));
        when(period.effectiveTo()).thenReturn(LocalDate.of(2026, 12, 31));
        when(period.sourceColumnReference()).thenReturn(4);

        final OfgemPriceCapLevel level = mock(OfgemPriceCapLevel.class);
        when(level.regionCode()).thenReturn("GB");
        when(level.paymentMethodCode()).thenReturn("DD");
        when(level.tariffTypeCode()).thenReturn("STANDARD");
        when(level.consumptionBasisCode()).thenReturn("TYPICAL");
        when(level.sourceSheet()).thenReturn("Levelised cap rates");
        when(level.sourceCell()).thenReturn("D12");

        final OfgemPriceCapWorkbookData data = mock(OfgemPriceCapWorkbookData.class);
        when(data.period()).thenReturn(period);
        when(data.levels()).thenReturn(List.of(level));

        final OfgemPersistenceResult result = new OfgemPersistenceRepository(database)
                .persist(definition, download, data);

        assertEquals(1L, result.inserted());
        assertEquals(0L, result.updated());
        assertEquals(0L, result.skipped());
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
        verify(connection, never()).rollback();
        verify(insertLevelsStatement).addBatch();
        verify(insertLevelsStatement).executeBatch();
    }

    private static void generatedKey(
            final PreparedStatement statement,
            final long value) throws Exception {
        final ResultSet keys = mock(ResultSet.class);
        when(statement.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(true);
        when(keys.getLong(1)).thenReturn(value);
    }
}
