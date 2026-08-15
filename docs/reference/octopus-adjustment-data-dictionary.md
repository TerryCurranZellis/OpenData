# Octopus Adjustment Data Dictionary

**Document ID:** REF-OCTOPUS-ADJUSTMENT-DATA-001  
**Version:** 3.1.0  
**Baseline date:** 15 August 2026  

---

The adjustment electricity and gas tables deliberately mirror the ordinary
Octopus business data structures.

## Electricity

Table:

```text
octopus.adjustment_electric_data
```

| Column | SQL type | Meaning |
|---|---|---|
| `bill_date` | `date` | Bill/document date parsed from PDF |
| `bill_period_start` | `date` | First date covered by the bill |
| `bill_period_end` | `date` | Last date covered by the bill |
| `tariff_name` | `nvarchar(200)` | Octopus tariff description |
| `tariff_period_start` | `date` | Start of tariff segment |
| `tariff_period_end` | `date` | End of tariff segment |
| `mpan` | `varchar(13)` | Electricity supply MPAN |
| `meter_id` | `varchar(100)` | Meter identifier |
| `start_reading_date` | `date` | Opening reading date |
| `start_reading_value` | `decimal(18,6)` | Opening meter reading |
| `start_reading_type` | `nvarchar(100)` | Opening reading classification |
| `end_reading_date` | `date` | Closing reading date |
| `end_reading_value` | `decimal(18,6)` | Closing meter reading |
| `end_reading_type` | `nvarchar(100)` | Closing reading classification |
| `energy_used_kwh` | `decimal(18,6)` | Electricity consumption |
| `unit_rate_p_kwh` | `decimal(12,6)` | Unit rate in pence/kWh |
| `standing_charge_rate_p_day` | `decimal(12,6)` | Daily standing-charge rate |
| `standing_charge_total_gbp` | `decimal(19,6)` | Standing charge total in GBP |
| `total_cost_gbp` | `decimal(19,6)` | Adjustment segment total in GBP |
| `last_run_id` | `uniqueidentifier` | OpenData plugin run |
| `created_at` | `datetime2(3)` | Row creation timestamp |
| `updated_at` | `datetime2(3)` | Last update timestamp |

## Gas

Table:

```text
octopus.adjustment_gas_data
```

| Column | SQL type | Meaning |
|---|---|---|
| `bill_date` | `date` | Bill/document date parsed from PDF |
| `bill_period_start` | `date` | First date covered by the bill |
| `bill_period_end` | `date` | Last date covered by the bill |
| `tariff_name` | `nvarchar(200)` | Octopus tariff description |
| `tariff_period_start` | `date` | Start of tariff segment |
| `tariff_period_end` | `date` | End of tariff segment |
| `mprn` | `varchar(20)` | Gas supply MPRN |
| `meter_id` | `varchar(100)` | Meter identifier |
| `start_reading_date` | `date` | Opening reading date |
| `start_reading_value` | `decimal(18,6)` | Opening meter reading |
| `start_reading_type` | `nvarchar(100)` | Opening reading classification |
| `end_reading_date` | `date` | Closing reading date |
| `end_reading_value` | `decimal(18,6)` | Closing meter reading |
| `end_reading_type` | `nvarchar(100)` | Closing reading classification |
| `consumption_m3` | `decimal(18,6)` | Gas volume consumed |
| `energy_used_kwh` | `decimal(18,6)` | Converted energy consumption |
| `unit_rate_p_kwh` | `decimal(12,6)` | Unit rate in pence/kWh |
| `standing_charge_rate_p_day` | `decimal(12,6)` | Daily standing-charge rate |
| `standing_charge_total_gbp` | `decimal(19,6)` | Standing charge total in GBP |
| `total_cost_gbp` | `decimal(19,6)` | Adjustment segment total in GBP |
| `last_run_id` | `uniqueidentifier` | OpenData plugin run |
| `created_at` | `datetime2(3)` | Row creation timestamp |
| `updated_at` | `datetime2(3)` | Last update timestamp |

## Interpretation

These are recalculated billing facts, not replacement rows in the ordinary
Octopus tables.

A consumer wishing to compare original and adjusted billing should query both
datasets explicitly and preserve their provenance.
