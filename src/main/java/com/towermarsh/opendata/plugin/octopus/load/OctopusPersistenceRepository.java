/* 
 *
 * Copyright © 2026 Terry Curran
 * 
 * SPDX-License-Identifier: Apache-2.0 
 *
 */
package com.towermarsh.opendata.plugin.octopus.load;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.octopus.extract.ExtractedOctopusStatement;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;
import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.UUID;

/**
 * Transactionally persists an entire Octopus statement batch and its file
 * ledger.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class OctopusPersistenceRepository {

    private final DatabaseResourceManager database;

    public OctopusPersistenceRepository(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    /**
     * Save the octopus data
     *
     * @param batch batch of octopus records to save
     * @param runId run id for thread process
     * @return update statistics
     */
    public OctopusPersistenceResult save(final OctopusParseResult batch, final UUID runId) {
        Objects.requireNonNull(batch, "batch");
        Objects.requireNonNull(runId, "runId");
        try (var connection = database.getConnection()) {
            final var oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                var inserted = 0;
                var updated = 0;
                for (var record : batch.electricityRecords()) {
                    if (electricityExists(connection, record)) {
                        updateElectricity(connection, record, runId);
                        updated++;
                    } else {
                        insertElectricity(connection, record, runId);
                        inserted++;
                    }
                }
                for (var record : batch.gasRecords()) {
                    if (gasExists(connection, record)) {
                        updateGas(connection, record, runId);
                        updated++;
                    } else {
                        insertGas(connection, record, runId);
                        inserted++;
                    }
                }
                for (var statement : batch.statements()) {
                    markCompleted(connection, statement, runId);
                }
                connection.commit();
                return new OctopusPersistenceResult(inserted, updated, 0);
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to persist Octopus statement batch", exception);
        }
    }

    /**
     * check if the electricity table exists
     *
     * @param c connection
     * @param r record to connect with
     * @return true if the table exists
     * @throws SQLException
     */
    private static boolean electricityExists(Connection c, ElectricityRecord r) throws SQLException {
        var sql = """
                     SELECT 1 FROM octopus.electric_data WHERE bill_date=? AND tariff_period_start=? AND tariff_period_end=? AND tariff_name=? AND mpan=? AND meter_id=? AND start_reading_date=? AND end_reading_date=?
                     """;
        try (var s = c.prepareStatement(sql)) {
            bindElectricityKey(s, r);
            try (var x = s.executeQuery()) {
                return x.next();
            }
        }
    }

    /**
     * bind columns to keys
     *
     * @param s sql statement
     * @param r record
     * @throws SQLException
     */
    private static void bindElectricityKey(PreparedStatement s, ElectricityRecord r) throws SQLException {
        s.setDate(1, Date.valueOf(r.billDate()));
        s.setDate(2, Date.valueOf(r.tariffPeriodStart()));
        s.setDate(3, Date.valueOf(r.tariffPeriodEnd()));
        s.setString(4, r.tariffName());
        s.setString(5, r.mpan());
        s.setString(6, r.meterId());
        s.setDate(7, Date.valueOf(r.startReadingDate()));
        s.setDate(8, Date.valueOf(r.endReadingDate()));
    }

    /**
     * insert electricity records
     *
     * @param c
     * @param r
     * @param runId
     * @throws SQLException
     */
    private static void insertElectricity(Connection c, ElectricityRecord r, UUID runId) throws SQLException {
        var sql = """
                INSERT INTO octopus.electric_data (bill_date,bill_period_start,bill_period_end,tariff_name,tariff_period_start,tariff_period_end,mpan,meter_id,start_reading_date,start_reading_value,start_reading_type,end_reading_date,end_reading_value,end_reading_type,energy_used_kwh,unit_rate_p_kwh,standing_charge_rate_p_day,standing_charge_total_gbp,total_cost_gbp,last_run_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;
        try (var s = c.prepareStatement(sql)) {
            bindElectricity(s, r, runId);
            s.executeUpdate();
        }
    }

    /**
     * update electricity records
     *
     * @param c
     * @param r
     * @param runId
     * @throws SQLException
     */
    private static void updateElectricity(Connection c, ElectricityRecord r, UUID runId) throws SQLException {
        var sql = """
                UPDATE octopus.electric_data SET bill_period_start=?,bill_period_end=?,start_reading_value=?,start_reading_type=?,end_reading_value=?,end_reading_type=?,energy_used_kwh=?,unit_rate_p_kwh=?,standing_charge_rate_p_day=?,standing_charge_total_gbp=?,total_cost_gbp=?,last_run_id=?,updated_at=SYSUTCDATETIME() WHERE bill_date=? AND tariff_period_start=? AND tariff_period_end=? AND tariff_name=? AND mpan=? AND meter_id=? AND start_reading_date=? AND end_reading_date=?
                """;
        try (var s = c.prepareStatement(sql)) {
            var i = 1;
            s.setDate(i++, Date.valueOf(r.billPeriodStart()));
            s.setDate(i++, Date.valueOf(r.billPeriodEnd()));
            s.setBigDecimal(i++, r.startReadingValue());
            s.setString(i++, r.startReadingType());
            s.setBigDecimal(i++, r.endReadingValue());
            s.setString(i++, r.endReadingType());
            s.setBigDecimal(i++, r.energyUsedKwh());
            s.setBigDecimal(i++, r.unitRatePKwh());
            s.setBigDecimal(i++, r.standingChargeRatePDay());
            s.setBigDecimal(i++, r.standingChargeTotalGbp());
            s.setBigDecimal(i++, r.totalCostGbp());
            s.setObject(i++, runId, Types.OTHER);
            s.setDate(i++, Date.valueOf(r.billDate()));
            s.setDate(i++, Date.valueOf(r.tariffPeriodStart()));
            s.setDate(i++, Date.valueOf(r.tariffPeriodEnd()));
            s.setString(i++, r.tariffName());
            s.setString(i++, r.mpan());
            s.setString(i++, r.meterId());
            s.setDate(i++, Date.valueOf(r.startReadingDate()));
            s.setDate(i, Date.valueOf(r.endReadingDate()));
            s.executeUpdate();
        }
    }

    /**
     * bind electricity records
     *
     * @param s
     * @param r
     * @param runId
     * @throws SQLException
     */
    private static void bindElectricity(PreparedStatement s, ElectricityRecord r, UUID runId) throws SQLException {
        var i = 1;
        s.setDate(i++, Date.valueOf(r.billDate()));
        s.setDate(i++, Date.valueOf(r.billPeriodStart()));
        s.setDate(i++, Date.valueOf(r.billPeriodEnd()));
        s.setString(i++, r.tariffName());
        s.setDate(i++, Date.valueOf(r.tariffPeriodStart()));
        s.setDate(i++, Date.valueOf(r.tariffPeriodEnd()));
        s.setString(i++, r.mpan());
        s.setString(i++, r.meterId());
        s.setDate(i++, Date.valueOf(r.startReadingDate()));
        s.setBigDecimal(i++, r.startReadingValue());
        s.setString(i++, r.startReadingType());
        s.setDate(i++, Date.valueOf(r.endReadingDate()));
        s.setBigDecimal(i++, r.endReadingValue());
        s.setString(i++, r.endReadingType());
        s.setBigDecimal(i++, r.energyUsedKwh());
        s.setBigDecimal(i++, r.unitRatePKwh());
        s.setBigDecimal(i++, r.standingChargeRatePDay());
        s.setBigDecimal(i++, r.standingChargeTotalGbp());
        s.setBigDecimal(i++, r.totalCostGbp());
        s.setObject(i, runId, Types.OTHER);
    }

    /**
     *
     * @param c
     * @param r
     * @return
     * @throws SQLException
     */
    private static boolean gasExists(Connection c, GasRecord r) throws SQLException {
        var sql = """
                     SELECT 1 FROM octopus.gas_data WHERE bill_date=? AND tariff_period_start=? AND tariff_period_end=? AND tariff_name=? AND mprn=? AND meter_id=? AND start_reading_date=? AND end_reading_date=?
                     """;
        try (var s = c.prepareStatement(sql)) {
            bindGasKey(s, r);
            try (var x = s.executeQuery()) {
                return x.next();
            }
        }
    }

    /**
     *
     * @param s
     * @param r
     * @throws SQLException
     */
    private static void bindGasKey(PreparedStatement s, GasRecord r) throws SQLException {
        s.setDate(1, Date.valueOf(r.billDate()));
        s.setDate(2, Date.valueOf(r.tariffPeriodStart()));
        s.setDate(3, Date.valueOf(r.tariffPeriodEnd()));
        s.setString(4, r.tariffName());
        s.setString(5, r.mprn());
        s.setString(6, r.meterId());
        s.setDate(7, Date.valueOf(r.startReadingDate()));
        s.setDate(8, Date.valueOf(r.endReadingDate()));
    }

    /**
     *
     * @param c
     * @param r
     * @param runId
     * @throws SQLException
     */
    private static void insertGas(Connection c, GasRecord r, UUID runId) throws SQLException {
        var sql = """
                   INSERT INTO octopus.gas_data (bill_date,bill_period_start,bill_period_end,tariff_name,tariff_period_start,tariff_period_end,mprn,meter_id,start_reading_date,start_reading_value,start_reading_type,end_reading_date,end_reading_value,end_reading_type,consumption_m3,energy_used_kwh,unit_rate_p_kwh,standing_charge_rate_p_day,standing_charge_total_gbp,total_cost_gbp,last_run_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                   """;
        try (var s = c.prepareStatement(sql)) {
            var i = 1;
            s.setDate(i++, Date.valueOf(r.billDate()));
            s.setDate(i++, Date.valueOf(r.billPeriodStart()));
            s.setDate(i++, Date.valueOf(r.billPeriodEnd()));
            s.setString(i++, r.tariffName());
            s.setDate(i++, Date.valueOf(r.tariffPeriodStart()));
            s.setDate(i++, Date.valueOf(r.tariffPeriodEnd()));
            s.setString(i++, r.mprn());
            s.setString(i++, r.meterId());
            s.setDate(i++, Date.valueOf(r.startReadingDate()));
            s.setBigDecimal(i++, r.startReadingValue());
            s.setString(i++, r.startReadingType());
            s.setDate(i++, Date.valueOf(r.endReadingDate()));
            s.setBigDecimal(i++, r.endReadingValue());
            s.setString(i++, r.endReadingType());
            s.setBigDecimal(i++, r.consumptionM3());
            s.setBigDecimal(i++, r.energyUsedKwh());
            s.setBigDecimal(i++, r.unitRatePKwh());
            s.setBigDecimal(i++, r.standingChargeRatePDay());
            s.setBigDecimal(i++, r.standingChargeTotalGbp());
            s.setBigDecimal(i++, r.totalCostGbp());
            s.setObject(i, runId, Types.OTHER);
            s.executeUpdate();
        }
    }

    /**
     *
     * @param c
     * @param r
     * @param runId
     * @throws SQLException
     */
    private static void updateGas(Connection c, GasRecord r, UUID runId) throws SQLException {
        var sql = """
                   UPDATE octopus.gas_data SET bill_period_start=?,bill_period_end=?,start_reading_value=?,start_reading_type=?,end_reading_value=?,end_reading_type=?,consumption_m3=?,energy_used_kwh=?,unit_rate_p_kwh=?,standing_charge_rate_p_day=?,standing_charge_total_gbp=?,total_cost_gbp=?,last_run_id=?,updated_at=SYSUTCDATETIME() WHERE bill_date=? AND tariff_period_start=? AND tariff_period_end=? AND tariff_name=? AND mprn=? AND meter_id=? AND start_reading_date=? AND end_reading_date=?
                   """;
        try (var s = c.prepareStatement(sql)) {
            var i = 1;
            s.setDate(i++, Date.valueOf(r.billPeriodStart()));
            s.setDate(i++, Date.valueOf(r.billPeriodEnd()));
            s.setBigDecimal(i++, r.startReadingValue());
            s.setString(i++, r.startReadingType());
            s.setBigDecimal(i++, r.endReadingValue());
            s.setString(i++, r.endReadingType());
            s.setBigDecimal(i++, r.consumptionM3());
            s.setBigDecimal(i++, r.energyUsedKwh());
            s.setBigDecimal(i++, r.unitRatePKwh());
            s.setBigDecimal(i++, r.standingChargeRatePDay());
            s.setBigDecimal(i++, r.standingChargeTotalGbp());
            s.setBigDecimal(i++, r.totalCostGbp());
            s.setObject(i++, runId, Types.OTHER);
            s.setDate(i++, Date.valueOf(r.billDate()));
            s.setDate(i++, Date.valueOf(r.tariffPeriodStart()));
            s.setDate(i++, Date.valueOf(r.tariffPeriodEnd()));
            s.setString(i++, r.tariffName());
            s.setString(i++, r.mprn());
            s.setString(i++, r.meterId());
            s.setDate(i++, Date.valueOf(r.startReadingDate()));
            s.setDate(i, Date.valueOf(r.endReadingDate()));
            s.executeUpdate();
        }
    }

    /**
     *
     * @param c
     * @param f
     * @param runId
     * @throws SQLException
     */
    private static void markCompleted(Connection c, ExtractedOctopusStatement f, UUID runId) throws SQLException {
        var sql = """
                  MERGE octopus.statement_file WITH (HOLDLOCK) AS target USING (SELECT ? AS file_name, ? AS sha256) AS source ON target.file_name=source.file_name AND target.sha256=source.sha256 WHEN MATCHED THEN UPDATE SET statement_date=?,size_bytes=?,status='COMPLETED',last_run_id=?,processed_at=SYSUTCDATETIME(),failure_message=NULL WHEN NOT MATCHED THEN INSERT(file_name,statement_date,sha256,size_bytes,status,last_run_id,processed_at) VALUES(?,?,?,?,'COMPLETED',?,SYSUTCDATETIME());
                  """;
        try (var s = c.prepareStatement(sql)) {
            var i = 1;
            s.setString(i++, f.fileName());
            s.setString(i++, f.sha256());
            s.setDate(i++, Date.valueOf(f.statementDate()));
            s.setLong(i++, f.sizeBytes());
            s.setObject(i++, runId, Types.OTHER);
            s.setString(i++, f.fileName());
            s.setDate(i++, Date.valueOf(f.statementDate()));
            s.setString(i++, f.sha256());
            s.setLong(i++, f.sizeBytes());
            s.setObject(i, runId, Types.OTHER);
            s.executeUpdate();
        }
    }
}
