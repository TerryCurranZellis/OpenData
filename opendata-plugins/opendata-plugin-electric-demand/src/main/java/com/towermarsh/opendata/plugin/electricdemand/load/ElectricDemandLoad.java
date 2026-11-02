/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricdemand.load;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.electricdemand.transform.DemandRecord;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Load step: upserts demand records into {@code electricdemand.Demand} in one
 * transaction. A dry run reports the records as skipped and writes nothing.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class ElectricDemandLoad {

    private static final Logger LOG = Logger.getLogger(ElectricDemandLoad.class.getName());
    private static final String UPDATE = "UPDATE electricdemand.Demand SET NationalDemandMW=?,TransmissionSystemDemandMW=?,EnglandWalesDemandMW=?,EmbeddedWindMW=?,EmbeddedSolarMW=?,PumpingMW=? WHERE SettlementDate=? AND SettlementPeriod=?";
    private static final String INSERT = "INSERT INTO electricdemand.Demand(SettlementDate,SettlementPeriod,NationalDemandMW,TransmissionSystemDemandMW,EnglandWalesDemandMW,EmbeddedWindMW,EmbeddedSolarMW,PumpingMW) VALUES (?,?,?,?,?,?,?,?)";

    /**
     * Persists the records.
     *
     * @param records parsed records
     * @param context execution context
     * @return metrics
     * @throws SQLException when the database write fails
     */
    public PluginMetrics load(final List<DemandRecord> records, final PluginExecutionContext context)
            throws SQLException {
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(context, "context");
        if (context.dryRun()) {
            LOG.info(() -> "electric-demand load (dry run): records=" + records.size());
            return new PluginMetrics(records.size(), 0, 0, records.size());
        }
        var inserted = 0L;
        var updated = 0L;
        try (var connection = context.database().getConnection()) {
            final var original = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (var update = connection.prepareStatement(UPDATE);
                    var insert = connection.prepareStatement(INSERT)) {
                for (var record : records) {
                    bind(update, 1, record.values());
                    update.setDate(7, Date.valueOf(record.settlementDate()));
                    update.setInt(8, record.settlementPeriod());
                    if (update.executeUpdate() > 0) {
                        updated++;
                    } else {
                        insert.setDate(1, Date.valueOf(record.settlementDate()));
                        insert.setInt(2, record.settlementPeriod());
                        bind(insert, 3, record.values());
                        insert.executeUpdate();
                        inserted++;
                    }
                }
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(original);
            }
        }
        LOG.info("electric-demand load: read=%d, inserted=%d, updated=%d".formatted(records.size(), inserted, updated));
        return new PluginMetrics(records.size(), inserted, updated, 0);
    }

    private static void bind(final PreparedStatement statement, final int first, final List<Double> values)
            throws SQLException {
        for (var i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                statement.setNull(first + i, Types.DECIMAL);
            } else {
                statement.setDouble(first + i, values.get(i));
            }
        }
    }
}
