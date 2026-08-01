/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
USE [OpenData];
GO

IF SCHEMA_ID(N'ofgem') IS NULL
BEGIN
    EXEC (N'CREATE SCHEMA [ofgem] AUTHORIZATION [dbo]');
END;
GO

IF OBJECT_ID(N'ofgem.charge_restriction_region', N'U') IS NULL
BEGIN
    CREATE TABLE [ofgem].[charge_restriction_region]
    (
        [region_code] varchar(40) NOT NULL,
        [region_name] nvarchar(100) NOT NULL,
        [display_order] smallint NOT NULL,
        [is_gb_average] bit NOT NULL
            CONSTRAINT [DF_region_is_gb_average] DEFAULT (0),
        CONSTRAINT [PK_charge_restriction_region]
            PRIMARY KEY CLUSTERED ([region_code]),
        CONSTRAINT [UQ_charge_restriction_region_name]
            UNIQUE ([region_name])
    );
END;
GO

IF OBJECT_ID(N'ofgem.payment_method', N'U') IS NULL
BEGIN
    CREATE TABLE [ofgem].[payment_method]
    (
        [payment_method_code] varchar(30) NOT NULL,
        [payment_method_name] nvarchar(100) NOT NULL,
        [display_order] smallint NOT NULL,
        CONSTRAINT [PK_payment_method]
            PRIMARY KEY CLUSTERED ([payment_method_code]),
        CONSTRAINT [UQ_payment_method_name]
            UNIQUE ([payment_method_name])
    );
END;
GO

IF OBJECT_ID(N'ofgem.tariff_type', N'U') IS NULL
BEGIN
    CREATE TABLE [ofgem].[tariff_type]
    (
        [tariff_type_code] varchar(40) NOT NULL,
        [tariff_type_name] nvarchar(150) NOT NULL,
        [fuel_code] varchar(20) NOT NULL,
        [metering_arrangement] varchar(30) NULL,
        [is_derived] bit NOT NULL
            CONSTRAINT [DF_tariff_type_is_derived] DEFAULT (0),
        [display_order] smallint NOT NULL,
        CONSTRAINT [PK_tariff_type]
            PRIMARY KEY CLUSTERED ([tariff_type_code]),
        CONSTRAINT [CK_tariff_type_fuel]
            CHECK ([fuel_code] IN ('ELECTRICITY', 'GAS', 'DUAL_FUEL'))
    );
END;
GO

IF OBJECT_ID(N'ofgem.consumption_basis', N'U') IS NULL
BEGIN
    CREATE TABLE [ofgem].[consumption_basis]
    (
        [consumption_basis_code] varchar(20) NOT NULL,
        [consumption_basis_name] nvarchar(100) NOT NULL,
        [display_order] smallint NOT NULL,
        CONSTRAINT [PK_consumption_basis]
            PRIMARY KEY CLUSTERED ([consumption_basis_code])
    );
END;
GO

IF OBJECT_ID(N'ofgem.price_cap_period', N'U') IS NULL
BEGIN
    CREATE TABLE [ofgem].[price_cap_period]
    (
        [price_cap_period_id] bigint IDENTITY(1, 1) NOT NULL,
        [period_name] nvarchar(100) NOT NULL,
        [effective_from] date NOT NULL,
        [effective_to] date NOT NULL,
        [source_column_reference] int NULL,
        [source_file_id] bigint NOT NULL,
        [is_current] bit NOT NULL
            CONSTRAINT [DF_price_cap_period_is_current] DEFAULT (0),
        [created_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_price_cap_period_created_at]
            DEFAULT SYSUTCDATETIME(),
        [updated_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_price_cap_period_updated_at]
            DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_price_cap_period]
            PRIMARY KEY CLUSTERED ([price_cap_period_id]),
        CONSTRAINT [UQ_price_cap_period_dates]
            UNIQUE ([effective_from], [effective_to]),
        CONSTRAINT [FK_price_cap_period_source_file]
            FOREIGN KEY ([source_file_id])
            REFERENCES [core].[source_file] ([source_file_id]),
        CONSTRAINT [CK_price_cap_period_dates]
            CHECK ([effective_to] >= [effective_from]),
        CONSTRAINT [CK_price_cap_period_column]
            CHECK ([source_column_reference] IS NULL
                OR [source_column_reference] > 0)
    );

    CREATE UNIQUE INDEX [UX_price_cap_period_current]
        ON [ofgem].[price_cap_period] ([is_current])
        WHERE [is_current] = 1;
END;
GO

IF OBJECT_ID(N'ofgem.price_cap_level', N'U') IS NULL
BEGIN
    CREATE TABLE [ofgem].[price_cap_level]
    (
        [price_cap_period_id] bigint NOT NULL,
        [region_code] varchar(40) NOT NULL,
        [payment_method_code] varchar(30) NOT NULL,
        [tariff_type_code] varchar(40) NOT NULL,
        [consumption_basis_code] varchar(20) NOT NULL,
        [amount_gbp] decimal(19, 6) NOT NULL,
        [vat_included] bit NOT NULL,
        [source_sheet] nvarchar(128) NOT NULL,
        [source_cell] varchar(20) NOT NULL,
        [ingestion_run_id] bigint NOT NULL,
        [loaded_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_price_cap_level_loaded_at]
            DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_price_cap_level]
            PRIMARY KEY CLUSTERED
            (
                [price_cap_period_id],
                [region_code],
                [payment_method_code],
                [tariff_type_code],
                [consumption_basis_code],
                [vat_included]
            ),
        CONSTRAINT [FK_price_cap_level_period]
            FOREIGN KEY ([price_cap_period_id])
            REFERENCES [ofgem].[price_cap_period] ([price_cap_period_id]),
        CONSTRAINT [FK_price_cap_level_region]
            FOREIGN KEY ([region_code])
            REFERENCES [ofgem].[charge_restriction_region] ([region_code]),
        CONSTRAINT [FK_price_cap_level_payment]
            FOREIGN KEY ([payment_method_code])
            REFERENCES [ofgem].[payment_method] ([payment_method_code]),
        CONSTRAINT [FK_price_cap_level_tariff]
            FOREIGN KEY ([tariff_type_code])
            REFERENCES [ofgem].[tariff_type] ([tariff_type_code]),
        CONSTRAINT [FK_price_cap_level_consumption]
            FOREIGN KEY ([consumption_basis_code])
            REFERENCES [ofgem].[consumption_basis]
                ([consumption_basis_code]),
        CONSTRAINT [FK_price_cap_level_run]
            FOREIGN KEY ([ingestion_run_id])
            REFERENCES [core].[ingestion_run] ([ingestion_run_id]),
        CONSTRAINT [CK_price_cap_level_amount]
            CHECK ([amount_gbp] >= 0)
    );

    CREATE INDEX [IX_price_cap_level_reporting]
        ON [ofgem].[price_cap_level]
            ([price_cap_period_id], [payment_method_code], [region_code])
        INCLUDE ([tariff_type_code], [consumption_basis_code],
                 [amount_gbp], [vat_included]);
END;
GO

IF OBJECT_ID(N'ofgem.price_cap_component', N'U') IS NULL
BEGIN
    CREATE TABLE [ofgem].[price_cap_component]
    (
        [component_code] varchar(20) NOT NULL,
        [component_name] nvarchar(150) NOT NULL,
        [description] nvarchar(1000) NULL,
        [display_order] smallint NOT NULL,
        CONSTRAINT [PK_price_cap_component]
            PRIMARY KEY CLUSTERED ([component_code])
    );
END;
GO

IF OBJECT_ID(N'ofgem.price_cap_component_value', N'U') IS NULL
BEGIN
    CREATE TABLE [ofgem].[price_cap_component_value]
    (
        [price_cap_period_id] bigint NOT NULL,
        [region_code] varchar(40) NOT NULL,
        [payment_method_code] varchar(30) NOT NULL,
        [tariff_type_code] varchar(40) NOT NULL,
        [consumption_basis_code] varchar(20) NOT NULL,
        [component_code] varchar(20) NOT NULL,
        [amount_gbp] decimal(19, 6) NULL,
        [is_levelised] bit NOT NULL
            CONSTRAINT [DF_component_value_is_levelised] DEFAULT (1),
        [source_sheet] nvarchar(128) NOT NULL,
        [source_cell] varchar(20) NOT NULL,
        [ingestion_run_id] bigint NOT NULL,
        [loaded_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_component_value_loaded_at]
            DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_price_cap_component_value]
            PRIMARY KEY CLUSTERED
            (
                [price_cap_period_id],
                [region_code],
                [payment_method_code],
                [tariff_type_code],
                [consumption_basis_code],
                [component_code]
            ),
        CONSTRAINT [FK_component_value_period]
            FOREIGN KEY ([price_cap_period_id])
            REFERENCES [ofgem].[price_cap_period] ([price_cap_period_id]),
        CONSTRAINT [FK_component_value_region]
            FOREIGN KEY ([region_code])
            REFERENCES [ofgem].[charge_restriction_region] ([region_code]),
        CONSTRAINT [FK_component_value_payment]
            FOREIGN KEY ([payment_method_code])
            REFERENCES [ofgem].[payment_method] ([payment_method_code]),
        CONSTRAINT [FK_component_value_tariff]
            FOREIGN KEY ([tariff_type_code])
            REFERENCES [ofgem].[tariff_type] ([tariff_type_code]),
        CONSTRAINT [FK_component_value_consumption]
            FOREIGN KEY ([consumption_basis_code])
            REFERENCES [ofgem].[consumption_basis]
                ([consumption_basis_code]),
        CONSTRAINT [FK_component_value_component]
            FOREIGN KEY ([component_code])
            REFERENCES [ofgem].[price_cap_component] ([component_code]),
        CONSTRAINT [FK_component_value_run]
            FOREIGN KEY ([ingestion_run_id])
            REFERENCES [core].[ingestion_run] ([ingestion_run_id])
    );
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM [core].[schema_version] WHERE [version] = '020'
)
BEGIN
    INSERT INTO [core].[schema_version] ([version], [description])
    VALUES ('020', N'Create Ofgem price-cap schema');
END;
GO
