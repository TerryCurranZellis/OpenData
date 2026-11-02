/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
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

/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
GO

MERGE [core].[dataset] AS target
USING
(
    VALUES
    (
        'ofgem',
        'OFGEM_PRICE_CAP',
        N'Ofgem energy price cap',
        N'Office of Gas and Electricity Markets',
        N'Quarterly default tariff cap levels and supporting model outputs.',
        N'https://www.ofgem.gov.uk/energy-policy-and-regulation/policy-and-regulatory-programmes/energy-price-cap-default-tariff-policy/energy-price-cap-default-tariff-levels'
    )
) AS source
(
    [plugin_code], [dataset_code], [dataset_name], [publisher_name],
    [description], [source_page_url]
)
ON target.[dataset_code] = source.[dataset_code]
WHEN MATCHED THEN UPDATE SET
    [plugin_code] = source.[plugin_code],
    [dataset_name] = source.[dataset_name],
    [publisher_name] = source.[publisher_name],
    [description] = source.[description],
    [source_page_url] = source.[source_page_url],
    [is_active] = 1,
    [updated_at] = SYSUTCDATETIME()
WHEN NOT MATCHED THEN INSERT
(
    [plugin_code], [dataset_code], [dataset_name], [publisher_name],
    [description], [source_page_url], [is_active]
)
VALUES
(
    source.[plugin_code], source.[dataset_code], source.[dataset_name],
    source.[publisher_name], source.[description], source.[source_page_url], 1
);
GO

MERGE [ofgem].[charge_restriction_region] AS target
USING
(
    VALUES
        ('NORTH_WEST', N'North West', 10, 0),
        ('NORTHERN', N'Northern', 20, 0),
        ('YORKSHIRE', N'Yorkshire', 30, 0),
        ('NORTHERN_SCOTLAND', N'Northern Scotland', 40, 0),
        ('SOUTHERN', N'Southern', 50, 0),
        ('SOUTHERN_SCOTLAND', N'Southern Scotland', 60, 0),
        ('N_WALES_AND_MERSEY', N'N Wales and Mersey', 70, 0),
        ('LONDON', N'London', 80, 0),
        ('SOUTH_EAST', N'South East', 90, 0),
        ('EASTERN', N'Eastern', 100, 0),
        ('EAST_MIDLANDS', N'East Midlands', 110, 0),
        ('MIDLANDS', N'Midlands', 120, 0),
        ('SOUTHERN_WESTERN', N'Southern Western', 130, 0),
        ('SOUTH_WALES', N'South Wales', 140, 0),
        ('GB_AVERAGE', N'GB average', 900, 1)
) AS source ([region_code], [region_name], [display_order], [is_gb_average])
ON target.[region_code] = source.[region_code]
WHEN MATCHED THEN UPDATE SET
    [region_name] = source.[region_name],
    [display_order] = source.[display_order],
    [is_gb_average] = source.[is_gb_average]
WHEN NOT MATCHED THEN INSERT
    ([region_code], [region_name], [display_order], [is_gb_average])
VALUES
    (source.[region_code], source.[region_name], source.[display_order],
     source.[is_gb_average]);
GO

MERGE [ofgem].[payment_method] AS target
USING
(
    VALUES
        ('OTHER', N'Other Payment Method', 10),
        ('STANDARD_CREDIT', N'Standard Credit', 20),
        ('PPM', N'Prepayment Meter', 30)
) AS source ([payment_method_code], [payment_method_name], [display_order])
ON target.[payment_method_code] = source.[payment_method_code]
WHEN MATCHED THEN UPDATE SET
    [payment_method_name] = source.[payment_method_name],
    [display_order] = source.[display_order]
WHEN NOT MATCHED THEN INSERT
    ([payment_method_code], [payment_method_name], [display_order])
VALUES
    (source.[payment_method_code], source.[payment_method_name],
     source.[display_order]);
GO

MERGE [ofgem].[tariff_type] AS target
USING
(
    VALUES
        ('ELECTRICITY_SINGLE_RATE',
         N'Electricity: Single-Rate Metering Arrangement',
         'ELECTRICITY', 'SINGLE_RATE', 0, 10),
        ('ELECTRICITY_MULTI_REGISTER',
         N'Electricity: Multi-Register Metering Arrangement',
         'ELECTRICITY', 'MULTI_REGISTER', 0, 20),
        ('GAS', N'Gas', 'GAS', NULL, 0, 30),
        ('DUAL_FUEL', N'Dual fuel (implied)', 'DUAL_FUEL', NULL, 1, 40)
) AS source
(
    [tariff_type_code], [tariff_type_name], [fuel_code],
    [metering_arrangement], [is_derived], [display_order]
)
ON target.[tariff_type_code] = source.[tariff_type_code]
WHEN MATCHED THEN UPDATE SET
    [tariff_type_name] = source.[tariff_type_name],
    [fuel_code] = source.[fuel_code],
    [metering_arrangement] = source.[metering_arrangement],
    [is_derived] = source.[is_derived],
    [display_order] = source.[display_order]
WHEN NOT MATCHED THEN INSERT
(
    [tariff_type_code], [tariff_type_name], [fuel_code],
    [metering_arrangement], [is_derived], [display_order]
)
VALUES
(
    source.[tariff_type_code], source.[tariff_type_name], source.[fuel_code],
    source.[metering_arrangement], source.[is_derived], source.[display_order]
);
GO

MERGE [ofgem].[consumption_basis] AS target
USING
(
    VALUES
        ('NIL', N'Nil consumption', 10),
        ('BENCHMARK', N'Benchmark annual consumption', 20)
) AS source
    ([consumption_basis_code], [consumption_basis_name], [display_order])
ON target.[consumption_basis_code] = source.[consumption_basis_code]
WHEN MATCHED THEN UPDATE SET
    [consumption_basis_name] = source.[consumption_basis_name],
    [display_order] = source.[display_order]
WHEN NOT MATCHED THEN INSERT
    ([consumption_basis_code], [consumption_basis_name], [display_order])
VALUES
    (source.[consumption_basis_code], source.[consumption_basis_name],
     source.[display_order]);
GO

MERGE [ofgem].[price_cap_component] AS target
USING
(
    VALUES
        ('DF', N'DF', 10),
        ('CM', N'CM', 20),
        ('AA', N'AA', 30),
        ('PC', N'PC', 40),
        ('NC', N'NC', 50),
        ('OC', N'OC', 60),
        ('SMNCC', N'SMNCC', 70),
        ('IC', N'IC', 80),
        ('PAAC', N'PAAC', 90),
        ('PAP', N'PAP', 100),
        ('CO', N'CO', 110),
        ('DRC', N'DRC', 120),
        ('EBIT', N'EBIT', 130),
        ('HAP', N'HAP', 140),
        ('LEVELISATION', N'Levelisation', 150),
        ('TOTAL_GB_AVERAGE', N'Total GB average', 900)
) AS source ([component_code], [component_name], [display_order])
ON target.[component_code] = source.[component_code]
WHEN MATCHED THEN UPDATE SET
    [component_name] = source.[component_name],
    [display_order] = source.[display_order]
WHEN NOT MATCHED THEN INSERT
    ([component_code], [component_name], [display_order])
VALUES
    (source.[component_code], source.[component_name], source.[display_order]);
GO

IF DATABASE_PRINCIPAL_ID(N'opendata_app') IS NOT NULL
    GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::[ofgem] TO [opendata_app];
GO
