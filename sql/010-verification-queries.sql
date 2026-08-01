/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
/*
    Optional read-only checks after deployment and the first OpenMeteo run.
*/
USE [OpenData];
GO

SELECT
    [SchemaName] = schema_name(t.schema_id),
    [TableName] = t.name,
    [RowCount] = SUM(p.rows)
FROM sys.tables AS t
JOIN sys.partitions AS p
  ON p.object_id = t.object_id
 AND p.index_id IN (0, 1)
WHERE t.object_id IN
      (OBJECT_ID(N'[core].[PluginRun]'),
       OBJECT_ID(N'[core].[application_property]'),
       OBJECT_ID(N'[core].[plugin_property]'),
       OBJECT_ID(N'[octopus].[electric_data]'),
       OBJECT_ID(N'[octopus].[gas_data]'),
       OBJECT_ID(N'[openmeteo].[Location]'),
       OBJECT_ID(N'[openmeteo].[DailyWeather]'))
GROUP BY t.schema_id, t.name
ORDER BY [SchemaName], [TableName];
GO

SELECT TOP (20)
    [RunId], [PluginId], [Status], [StartedAt], [CompletedAt],
    [ThreadName], [RowsRead], [RowsInserted], [RowsUpdated],
    [RowsSkipped], [ErrorMessage]
FROM [core].[PluginRun]
ORDER BY [StartedAt] DESC;
GO

SELECT
    [property_key],
    [is_encrypted],
    [updated_at]
FROM [core].[application_property]
ORDER BY [property_key];
GO

SELECT
    [plugin_id],
    [property_key],
    [updated_at]
FROM [core].[plugin_property]
ORDER BY [plugin_id], [property_key];
GO

SELECT
    l.[LocationKey],
    l.[LocationName],
    [FirstObservationDate] = MIN(w.[ObservationDate]),
    [LastObservationDate] = MAX(w.[ObservationDate]),
    [ObservationCount] = COUNT_BIG(*)
FROM [openmeteo].[Location] AS l
LEFT JOIN [openmeteo].[DailyWeather] AS w
  ON w.[LocationId] = l.[LocationId]
GROUP BY l.[LocationKey], l.[LocationName]
ORDER BY l.[LocationKey];
GO

SELECT TOP (20)
    e.[mpan],
    e.[bill_date],
    e.[tariff_name],
    e.[energy_used_kwh],
    e.[total_cost_gbp],
    e.[last_run_id]
FROM [octopus].[electric_data] AS e
ORDER BY e.[bill_date] DESC, e.[mpan];
GO

SELECT TOP (20)
    g.[mprn],
    g.[bill_date],
    g.[tariff_name],
    g.[energy_used_kwh],
    g.[total_cost_gbp],
    g.[last_run_id]
FROM [octopus].[gas_data] AS g
ORDER BY g.[bill_date] DESC, g.[mprn];
GO

SELECT TOP (20)
    l.[LocationKey],
    w.[ObservationDate],
    w.[MinimumTemperatureC],
    w.[MaximumTemperatureC],
    w.[MeanTemperatureC],
    w.[Sunrise],
    w.[Sunset],
    w.[DaylightMinutes],
    w.[WeatherCode],
    w.[WeatherDescription],
    w.[LastRunId]
FROM [openmeteo].[DailyWeather] AS w
JOIN [openmeteo].[Location] AS l
  ON l.[LocationId] = w.[LocationId]
ORDER BY w.[ObservationDate] DESC, l.[LocationKey];
GO
