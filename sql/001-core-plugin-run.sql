/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
USE [OpenData];
GO
SET XACT_ABORT ON;
GO

IF SCHEMA_ID(N'core') IS NULL
BEGIN
    EXEC(N'CREATE SCHEMA [core] AUTHORIZATION [dbo];');
END;
GO

IF OBJECT_ID(N'[core].[PluginRun]', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[PluginRun]
    (
        [RunId] uniqueidentifier NOT NULL,
        [PluginId] nvarchar(100) NOT NULL,
        [Status] varchar(20) NOT NULL,
        [StartedAt] datetime2(3) NOT NULL,
        [CompletedAt] datetime2(3) NULL,
        [ThreadName] nvarchar(128) NOT NULL,
        [HostName] nvarchar(128) NOT NULL,
        [RowsRead] bigint NOT NULL CONSTRAINT [DF_PluginRun_RowsRead] DEFAULT (0),
        [RowsInserted] bigint NOT NULL CONSTRAINT [DF_PluginRun_RowsInserted] DEFAULT (0),
        [RowsUpdated] bigint NOT NULL CONSTRAINT [DF_PluginRun_RowsUpdated] DEFAULT (0),
        [RowsSkipped] bigint NOT NULL CONSTRAINT [DF_PluginRun_RowsSkipped] DEFAULT (0),
        [ErrorMessage] nvarchar(4000) NULL,
        [CreatedAt] datetime2(3) NOT NULL CONSTRAINT [DF_PluginRun_CreatedAt] DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT [PK_PluginRun] PRIMARY KEY CLUSTERED ([RunId]),
        CONSTRAINT [CK_PluginRun_Status] CHECK
            ([Status] IN ('RUNNING', 'SUCCESS', 'DRY_RUN', 'FAILED', 'CANCELLED')),
        CONSTRAINT [CK_PluginRun_Completed] CHECK
            (([Status] = 'RUNNING' AND [CompletedAt] IS NULL)
             OR ([Status] <> 'RUNNING' AND [CompletedAt] IS NOT NULL)),
        CONSTRAINT [CK_PluginRun_RowCounts] CHECK
            ([RowsRead] >= 0 AND [RowsInserted] >= 0 AND [RowsUpdated] >= 0 AND [RowsSkipped] >= 0)
    );

    CREATE INDEX [IX_PluginRun_PluginId_StartedAt]
        ON [core].[PluginRun] ([PluginId], [StartedAt] DESC)
        INCLUDE ([Status], [CompletedAt], [RowsRead], [RowsInserted], [RowsUpdated], [RowsSkipped]);
END;
GO
