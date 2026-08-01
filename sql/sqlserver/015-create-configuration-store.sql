/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
USE [OpenData];
GO

IF OBJECT_ID(N'core.application_property', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[application_property]
    (
        [property_key] varchar(200) NOT NULL,
        [property_value] nvarchar(max) NOT NULL,
        [is_encrypted] bit NOT NULL
            CONSTRAINT [DF_application_property_is_encrypted] DEFAULT (0),
        [updated_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_application_property_updated_at] DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_application_property]
            PRIMARY KEY CLUSTERED ([property_key])
    );
END;
GO

IF OBJECT_ID(N'core.plugin_property', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[plugin_property]
    (
        [plugin_id] varchar(100) NOT NULL,
        [property_key] varchar(200) NOT NULL,
        [property_value] nvarchar(max) NOT NULL,
        [updated_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_plugin_property_updated_at] DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_plugin_property]
            PRIMARY KEY CLUSTERED ([plugin_id], [property_key]),
        CONSTRAINT [CK_plugin_property_plugin_id]
            CHECK ([plugin_id] NOT LIKE '%[^a-z0-9-]%')
    );

    CREATE INDEX [IX_plugin_property_property_key]
        ON [core].[plugin_property] ([property_key]);
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM [core].[schema_version] WHERE [version] = '015'
)
BEGIN
    INSERT INTO [core].[schema_version] ([version], [description])
    VALUES ('015', N'Create application and plugin configuration property store');
END;
GO
