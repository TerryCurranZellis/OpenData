/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.load;

import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

/**
 * SQL Server bindings for Octopus gas billing records.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
final class GasRecordUpsertAdapter extends AbstractOctopusUpsertAdapter<GasRecord> {

    private static final String EXISTS_SQL = """
            SELECT 1
              FROM octopus.gas_data
             WHERE bill_date = ?
               AND tariff_period_start = ?
               AND tariff_period_end = ?
               AND tariff_name = ?
               AND mprn = ?
               AND meter_id = ?
               AND start_reading_date = ?
               AND end_reading_date = ?
            """;

    private static final String INSERT_SQL = """
            INSERT INTO octopus.gas_data
                (bill_date, bill_period_start, bill_period_end, tariff_name,
                 tariff_period_start, tariff_period_end, mprn, meter_id,
                 start_reading_date, start_reading_value, start_reading_type,
                 end_reading_date, end_reading_value, end_reading_type,
                 consumption_m3, energy_used_kwh, unit_rate_p_kwh,
                 standing_charge_rate_p_day, standing_charge_total_gbp,
                 total_cost_gbp, last_run_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE octopus.gas_data
               SET bill_period_start = ?,
                   bill_period_end = ?,
                   start_reading_value = ?,
                   start_reading_type = ?,
                   end_reading_value = ?,
                   end_reading_type = ?,
                   consumption_m3 = ?,
                   energy_used_kwh = ?,
                   unit_rate_p_kwh = ?,
                   standing_charge_rate_p_day = ?,
                   standing_charge_total_gbp = ?,
                   total_cost_gbp = ?,
                   last_run_id = ?,
                   updated_at = SYSUTCDATETIME()
             WHERE bill_date = ?
               AND tariff_period_start = ?
               AND tariff_period_end = ?
               AND tariff_name = ?
               AND mprn = ?
               AND meter_id = ?
               AND start_reading_date = ?
               AND end_reading_date = ?
            """;

    /** Creates the gas adapter. */
    GasRecordUpsertAdapter() {
        super(EXISTS_SQL, INSERT_SQL, UPDATE_SQL);
    }

    /** {@inheritDoc} */
    @Override
    protected int bindKey(
            final PreparedStatement statement,
            final GasRecord record,
            final int index) throws SQLException {
        int parameter = index;
        statement.setDate(parameter++, Date.valueOf(record.billDate()));
        statement.setDate(parameter++, Date.valueOf(record.tariffPeriodStart()));
        statement.setDate(parameter++, Date.valueOf(record.tariffPeriodEnd()));
        statement.setString(parameter++, record.tariffName());
        statement.setString(parameter++, record.mprn());
        statement.setString(parameter++, record.meterId());
        statement.setDate(parameter++, Date.valueOf(record.startReadingDate()));
        statement.setDate(parameter++, Date.valueOf(record.endReadingDate()));
        return parameter;
    }

    /** {@inheritDoc} */
    @Override
    protected void bindInsert(
            final PreparedStatement statement,
            final GasRecord record,
            final UUID runId) throws SQLException {
        int parameter = 1;
        statement.setDate(parameter++, Date.valueOf(record.billDate()));
        statement.setDate(parameter++, Date.valueOf(record.billPeriodStart()));
        statement.setDate(parameter++, Date.valueOf(record.billPeriodEnd()));
        statement.setString(parameter++, record.tariffName());
        statement.setDate(parameter++, Date.valueOf(record.tariffPeriodStart()));
        statement.setDate(parameter++, Date.valueOf(record.tariffPeriodEnd()));
        statement.setString(parameter++, record.mprn());
        statement.setString(parameter++, record.meterId());
        statement.setDate(parameter++, Date.valueOf(record.startReadingDate()));
        statement.setBigDecimal(parameter++, record.startReadingValue());
        statement.setString(parameter++, record.startReadingType());
        statement.setDate(parameter++, Date.valueOf(record.endReadingDate()));
        statement.setBigDecimal(parameter++, record.endReadingValue());
        statement.setString(parameter++, record.endReadingType());
        statement.setBigDecimal(parameter++, record.consumptionM3());
        statement.setBigDecimal(parameter++, record.energyUsedKwh());
        statement.setBigDecimal(parameter++, record.unitRatePKwh());
        statement.setBigDecimal(parameter++, record.standingChargeRatePDay());
        statement.setBigDecimal(parameter++, record.standingChargeTotalGbp());
        statement.setBigDecimal(parameter++, record.totalCostGbp());
        statement.setObject(parameter, runId, Types.VARCHAR);
    }

    /** {@inheritDoc} */
    @Override
    protected void bindUpdate(
            final PreparedStatement statement,
            final GasRecord record,
            final UUID runId) throws SQLException {
        int parameter = 1;
        statement.setDate(parameter++, Date.valueOf(record.billPeriodStart()));
        statement.setDate(parameter++, Date.valueOf(record.billPeriodEnd()));
        statement.setBigDecimal(parameter++, record.startReadingValue());
        statement.setString(parameter++, record.startReadingType());
        statement.setBigDecimal(parameter++, record.endReadingValue());
        statement.setString(parameter++, record.endReadingType());
        statement.setBigDecimal(parameter++, record.consumptionM3());
        statement.setBigDecimal(parameter++, record.energyUsedKwh());
        statement.setBigDecimal(parameter++, record.unitRatePKwh());
        statement.setBigDecimal(parameter++, record.standingChargeRatePDay());
        statement.setBigDecimal(parameter++, record.standingChargeTotalGbp());
        statement.setBigDecimal(parameter++, record.totalCostGbp());
        statement.setObject(parameter++, runId, Types.VARCHAR);
        bindKey(statement, record, parameter);
    }
}
