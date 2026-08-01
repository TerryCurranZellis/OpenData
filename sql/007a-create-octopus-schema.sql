/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
USE [OpenData];
GO
SET XACT_ABORT ON;
GO

IF SCHEMA_ID(N'octopus') IS NULL
BEGIN
    EXEC(N'CREATE SCHEMA [octopus] AUTHORIZATION [dbo];');
END;
GO

IF OBJECT_ID(N'[octopus].[electric_data]', N'U') IS NULL
BEGIN
    CREATE TABLE [octopus].[electric_data]
    (
        [bill_date] date NOT NULL,
        [bill_period_start] date NOT NULL,
        [bill_period_end] date NOT NULL,
        [tariff_name] nvarchar(200) NOT NULL,
        [tariff_period_start] date NOT NULL,
        [tariff_period_end] date NOT NULL,
        [mpan] varchar(13) NOT NULL,
        [meter_id] varchar(100) NOT NULL,
        [start_reading_date] date NOT NULL,
        [start_reading_value] decimal(18, 6) NOT NULL,
        [start_reading_type] nvarchar(100) NOT NULL,
        [end_reading_date] date NOT NULL,
        [end_reading_value] decimal(18, 6) NOT NULL,
        [end_reading_type] nvarchar(100) NOT NULL,
        [energy_used_kwh] decimal(18, 6) NOT NULL,
        [unit_rate_p_kwh] decimal(12, 6) NOT NULL,
        [standing_charge_rate_p_day] decimal(12, 6) NOT NULL,
        [standing_charge_total_gbp] decimal(19, 6) NOT NULL,
        [total_cost_gbp] decimal(19, 6) NOT NULL,
        [last_run_id] uniqueidentifier NOT NULL,
        [created_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_octopus_electric_data_created_at]
            DEFAULT SYSUTCDATETIME(),
        [updated_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_octopus_electric_data_updated_at]
            DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_octopus_electric_data]
            PRIMARY KEY CLUSTERED
            (
                [bill_date],
                [tariff_period_start],
                [tariff_period_end],
                [tariff_name],
                [mpan],
                [meter_id],
                [start_reading_date],
                [end_reading_date]
            ),
        CONSTRAINT [FK_octopus_electric_data_run]
            FOREIGN KEY ([last_run_id])
            REFERENCES [core].[PluginRun] ([RunId]),
        CONSTRAINT [CK_octopus_electric_data_bill_period]
            CHECK ([bill_period_end] >= [bill_period_start]),
        CONSTRAINT [CK_octopus_electric_data_tariff_period]
            CHECK ([tariff_period_end] >= [tariff_period_start]),
        CONSTRAINT [CK_octopus_electric_data_reading_period]
            CHECK ([end_reading_date] >= [start_reading_date]),
        CONSTRAINT [CK_octopus_electric_data_bill_date]
            CHECK ([bill_date] >= [bill_period_end]),
        CONSTRAINT [CK_octopus_electric_data_tariff_within_bill]
            CHECK ([tariff_period_start] >= [bill_period_start]
                AND [tariff_period_end] <= [bill_period_end]),
        CONSTRAINT [CK_octopus_electric_data_mpan]
            CHECK ([mpan] = ''
                OR (LEN([mpan]) = 13 AND [mpan] NOT LIKE '%[^0-9]%')),
        CONSTRAINT [CK_octopus_electric_data_readings_and_rates]
            CHECK ([start_reading_value] >= 0
               AND [end_reading_value] >= 0
               AND [unit_rate_p_kwh] >= 0
               AND [standing_charge_rate_p_day] >= 0)
    );

    CREATE INDEX [IX_octopus_electric_data_mpan_bill_date]
        ON [octopus].[electric_data] ([mpan], [bill_date] DESC)
        INCLUDE ([tariff_name], [total_cost_gbp], [energy_used_kwh], [last_run_id]);

    CREATE INDEX [IX_octopus_electric_data_last_run]
        ON [octopus].[electric_data] ([last_run_id], [bill_date] DESC);
END;
GO

IF OBJECT_ID(N'[octopus].[gas_data]', N'U') IS NULL
BEGIN
    CREATE TABLE [octopus].[gas_data]
    (
        [bill_date] date NOT NULL,
        [bill_period_start] date NOT NULL,
        [bill_period_end] date NOT NULL,
        [tariff_name] nvarchar(200) NOT NULL,
        [tariff_period_start] date NOT NULL,
        [tariff_period_end] date NOT NULL,
        [mprn] varchar(20) NOT NULL,
        [meter_id] varchar(100) NOT NULL,
        [start_reading_date] date NOT NULL,
        [start_reading_value] decimal(18, 6) NOT NULL,
        [start_reading_type] nvarchar(100) NOT NULL,
        [end_reading_date] date NOT NULL,
        [end_reading_value] decimal(18, 6) NOT NULL,
        [end_reading_type] nvarchar(100) NOT NULL,
        [consumption_m3] decimal(18, 6) NOT NULL,
        [energy_used_kwh] decimal(18, 6) NOT NULL,
        [unit_rate_p_kwh] decimal(12, 6) NOT NULL,
        [standing_charge_rate_p_day] decimal(12, 6) NOT NULL,
        [standing_charge_total_gbp] decimal(19, 6) NOT NULL,
        [total_cost_gbp] decimal(19, 6) NOT NULL,
        [last_run_id] uniqueidentifier NOT NULL,
        [created_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_octopus_gas_data_created_at]
            DEFAULT SYSUTCDATETIME(),
        [updated_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_octopus_gas_data_updated_at]
            DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_octopus_gas_data]
            PRIMARY KEY CLUSTERED
            (
                [bill_date],
                [tariff_period_start],
                [tariff_period_end],
                [tariff_name],
                [mprn],
                [meter_id],
                [start_reading_date],
                [end_reading_date]
            ),
        CONSTRAINT [FK_octopus_gas_data_run]
            FOREIGN KEY ([last_run_id])
            REFERENCES [core].[PluginRun] ([RunId]),
        CONSTRAINT [CK_octopus_gas_data_bill_period]
            CHECK ([bill_period_end] >= [bill_period_start]),
        CONSTRAINT [CK_octopus_gas_data_tariff_period]
            CHECK ([tariff_period_end] >= [tariff_period_start]),
        CONSTRAINT [CK_octopus_gas_data_reading_period]
            CHECK ([end_reading_date] >= [start_reading_date]),
        CONSTRAINT [CK_octopus_gas_data_bill_date]
            CHECK ([bill_date] >= [bill_period_end]),
        CONSTRAINT [CK_octopus_gas_data_tariff_within_bill]
            CHECK ([tariff_period_start] >= [bill_period_start]
                AND [tariff_period_end] <= [bill_period_end]),
        CONSTRAINT [CK_octopus_gas_data_mprn]
            CHECK ([mprn] = '' OR [mprn] NOT LIKE '%[^0-9]%'),
        CONSTRAINT [CK_octopus_gas_data_readings_and_rates]
            CHECK ([start_reading_value] >= 0
               AND [end_reading_value] >= 0
               AND [unit_rate_p_kwh] >= 0
               AND [standing_charge_rate_p_day] >= 0)
    );

    CREATE INDEX [IX_octopus_gas_data_mprn_bill_date]
        ON [octopus].[gas_data] ([mprn], [bill_date] DESC)
        INCLUDE ([tariff_name], [total_cost_gbp], [energy_used_kwh], [last_run_id]);

    CREATE INDEX [IX_octopus_gas_data_last_run]
        ON [octopus].[gas_data] ([last_run_id], [bill_date] DESC);
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM [core].[schema_version] WHERE [version] = '080'
)
BEGIN
    INSERT INTO [core].[schema_version] ([version], [description])
    VALUES ('080', N'Create Octopus electricity and gas billing tables');
END;
GO
