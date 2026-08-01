/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
USE [OpenData];
GO
SET XACT_ABORT ON;
GO

IF SCHEMA_ID(N'openmeteo') IS NULL
BEGIN
    EXEC(N'CREATE SCHEMA [openmeteo] AUTHORIZATION [dbo];');
END;
GO

IF OBJECT_ID(N'[openmeteo].[Location]', N'U') IS NULL
BEGIN
    CREATE TABLE [openmeteo].[Location]
    (
        [LocationId] bigint IDENTITY(1,1) NOT NULL,
        [LocationKey] nvarchar(100) NOT NULL,
        [LocationName] nvarchar(200) NOT NULL,
        [Latitude] decimal(9,6) NOT NULL,
        [Longitude] decimal(9,6) NOT NULL,
        [TimeZone] nvarchar(100) NOT NULL,
        [CreatedAt] datetime2(3) NOT NULL CONSTRAINT [DF_OpenMeteoLocation_CreatedAt] DEFAULT (SYSUTCDATETIME()),
        [UpdatedAt] datetime2(3) NOT NULL CONSTRAINT [DF_OpenMeteoLocation_UpdatedAt] DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT [PK_OpenMeteoLocation] PRIMARY KEY CLUSTERED ([LocationId]),
        CONSTRAINT [UQ_OpenMeteoLocation_LocationKey] UNIQUE ([LocationKey]),
        CONSTRAINT [CK_OpenMeteoLocation_Latitude] CHECK ([Latitude] BETWEEN -90.0 AND 90.0),
        CONSTRAINT [CK_OpenMeteoLocation_Longitude] CHECK ([Longitude] BETWEEN -180.0 AND 180.0)
    );
END;
GO

IF OBJECT_ID(N'[openmeteo].[DailyWeather]', N'U') IS NULL
BEGIN
    CREATE TABLE [openmeteo].[DailyWeather]
    (
        [LocationId] bigint NOT NULL,
        [ObservationDate] date NOT NULL,
        [MinimumTemperatureC] decimal(6,2) NOT NULL,
        [MaximumTemperatureC] decimal(6,2) NOT NULL,
        [MeanTemperatureC] decimal(6,2) NOT NULL,
        [Sunrise] time(0) NOT NULL,
        [Sunset] time(0) NOT NULL,
        [DaylightMinutes] smallint NOT NULL,
        [WeatherCode] smallint NOT NULL,
        [WeatherDescription] nvarchar(200) NOT NULL,
        [LastRunId] uniqueidentifier NOT NULL,
        [CreatedAt] datetime2(3) NOT NULL CONSTRAINT [DF_OpenMeteoDaily_CreatedAt] DEFAULT (SYSUTCDATETIME()),
        [UpdatedAt] datetime2(3) NOT NULL CONSTRAINT [DF_OpenMeteoDaily_UpdatedAt] DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT [PK_OpenMeteoDailyWeather]
            PRIMARY KEY CLUSTERED ([LocationId], [ObservationDate]),
        CONSTRAINT [FK_OpenMeteoDailyWeather_Location]
            FOREIGN KEY ([LocationId]) REFERENCES [openmeteo].[Location] ([LocationId]),
        CONSTRAINT [FK_OpenMeteoDailyWeather_PluginRun]
            FOREIGN KEY ([LastRunId]) REFERENCES [core].[PluginRun] ([RunId]),
        CONSTRAINT [CK_OpenMeteoDailyWeather_TemperatureOrder]
            CHECK ([MinimumTemperatureC] <= [MaximumTemperatureC]),
        CONSTRAINT [CK_OpenMeteoDailyWeather_Daylight]
            CHECK ([DaylightMinutes] BETWEEN 0 AND 1440),
        CONSTRAINT [CK_OpenMeteoDailyWeather_WeatherCode]
            CHECK ([WeatherCode] BETWEEN 0 AND 999)
    );

    CREATE INDEX [IX_OpenMeteoDailyWeather_ObservationDate]
        ON [openmeteo].[DailyWeather] ([ObservationDate], [LocationId])
        INCLUDE ([MinimumTemperatureC], [MaximumTemperatureC], [MeanTemperatureC], [WeatherCode]);

    CREATE INDEX [IX_OpenMeteoDailyWeather_LastRunId]
        ON [openmeteo].[DailyWeather] ([LastRunId]);
END;
GO
