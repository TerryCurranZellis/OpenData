/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
USE [OpenData];
GO

IF OBJECT_ID(N'core.plugin_registry', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[plugin_registry]
    (
        [plugin_id] varchar(100) NOT NULL,
        [display_name] nvarchar(200) NOT NULL,
        [description] nvarchar(1000) NOT NULL
            CONSTRAINT [DF_plugin_registry_description] DEFAULT (N''),
        [implementation_class] varchar(500) NOT NULL,
        [is_enabled] bit NOT NULL
            CONSTRAINT [DF_plugin_registry_is_enabled] DEFAULT (1),
        [configuration_version] int NOT NULL
            CONSTRAINT [DF_plugin_registry_configuration_version] DEFAULT (1),
        [registered_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_plugin_registry_registered_at] DEFAULT SYSUTCDATETIME(),
        [updated_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_plugin_registry_updated_at] DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_plugin_registry]
            PRIMARY KEY CLUSTERED ([plugin_id]),
        CONSTRAINT [CK_plugin_registry_plugin_id]
            CHECK ([plugin_id] NOT LIKE '%[^a-z0-9-]%'),
        CONSTRAINT [CK_plugin_registry_configuration_version]
            CHECK ([configuration_version] >= 1)
    );

    CREATE INDEX [IX_plugin_registry_enabled]
        ON [core].[plugin_registry] ([is_enabled], [plugin_id]);
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM [core].[schema_version] WHERE [version] = '016'
)
BEGIN
    INSERT INTO [core].[schema_version] ([version], [description])
    VALUES ('016', N'Create persistent plugin registry and enabled status store');
END;
GO
