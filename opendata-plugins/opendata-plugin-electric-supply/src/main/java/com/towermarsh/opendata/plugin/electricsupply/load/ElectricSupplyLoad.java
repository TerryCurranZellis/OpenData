/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricsupply.load;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.electricsupply.transform.GenerationMixRecord;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Load step: upserts generation mix records into {@code electricsupply.GenerationMix}
 * in one transaction. A dry run reports the records as skipped and writes nothing.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public class ElectricSupplyLoad {

    private static final Logger LOG = Logger.getLogger(ElectricSupplyLoad.class.getName());
    private static final String UPDATE = "UPDATE electricsupply.GenerationMix SET "
            + "GasMW=?,CoalMW=?,NuclearMW=?,WindMW=?,EmbeddedWindMW=?,HydroMW=?,"
            + "ImportsMW=?,BiomassMW=?,OtherMW=?,SolarMW=?,StorageMW=?,"
            + "GenerationMW=?,CarbonIntensity=? WHERE DateTimeUtc=?";
    private static final String INSERT = "INSERT INTO electricsupply.GenerationMix("
            + "DateTimeUtc,GasMW,CoalMW,NuclearMW,WindMW,EmbeddedWindMW,"
            + "HydroMW,ImportsMW,BiomassMW,OtherMW,SolarMW,StorageMW,"
            + "GenerationMW,CarbonIntensity) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    /**
     * Persists the records.
     *
     * @param records parsed records
     * @param context execution context
     * @return metrics
     * @throws SQLException when the database write fails
     */
    public PluginMetrics load(List<GenerationMixRecord> records, final PluginExecutionContext context)
            throws SQLException {
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(context, "context");
        if (context.dryRun()) {
            LOG.info(() -> "electric-supply load (dry run): records=" + records.size());
            return new PluginMetrics(records.size(), 0, 0, records.size());
        }
        var inserted = 0L;
        var updated = 0L;
        try (var connection = context.database().getConnection()) {
            var original = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (var update = connection.prepareStatement(UPDATE);
                    var insert = connection.prepareStatement(INSERT)) {
                for (var record : records) {
                    final var timestamp = Timestamp.from(record.dateTimeUtc());
                    bind(update, 1, record.values());
                    update.setTimestamp(14, timestamp);
                    if (update.executeUpdate() > 0) {
                        updated++;
                    } else {
                        insert.setTimestamp(1, timestamp);
                        bind(insert, 2, record.values());
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
        LOG.info("electric-supply load: read=%d, inserted=%d, updated=%d".formatted(records.size(), inserted, updated));
        return new PluginMetrics(records.size(), inserted, updated, 0);
    }

    /**
     * Bind.
     *
     * @param statement input statement
     * @param first input first
     * @param values value of values
     * @throws SQLException if bind fails
     */
    private static void bind(PreparedStatement statement, int first, List<Double> values)
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
