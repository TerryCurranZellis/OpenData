USE [OpenData];
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

IF NOT EXISTS (
    SELECT 1 FROM [core].[schema_version] WHERE [version] = '030'
)
BEGIN
    INSERT INTO [core].[schema_version] ([version], [description])
    VALUES ('030', N'Seed Ofgem dataset dimensions');
END;
GO
