/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
USE [OpenData];
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.database_principals
    WHERE [name] = N'opendata_app' AND [type] = 'R'
)
BEGIN
    CREATE ROLE [opendata_app] AUTHORIZATION [dbo];
END;
GO

GRANT SELECT ON SCHEMA::[core] TO [opendata_app];
GRANT SELECT ON SCHEMA::[ofgem] TO [opendata_app];

GRANT INSERT, UPDATE ON OBJECT::[core].[ingestion_run] TO [opendata_app];
GRANT INSERT ON OBJECT::[core].[source_file] TO [opendata_app];
GRANT INSERT ON OBJECT::[core].[ingestion_error] TO [opendata_app];
GRANT INSERT, UPDATE ON OBJECT::[core].[application_property] TO [opendata_app];
GRANT SELECT, INSERT, UPDATE, DELETE ON OBJECT::[core].[plugin_property] TO [opendata_app];

GRANT INSERT, UPDATE ON OBJECT::[ofgem].[price_cap_period] TO [opendata_app];
GRANT INSERT, DELETE ON OBJECT::[ofgem].[price_cap_level] TO [opendata_app];
GRANT INSERT, DELETE ON OBJECT::[ofgem].[price_cap_component_value]
    TO [opendata_app];
GO

IF NOT EXISTS (
    SELECT 1 FROM [core].[schema_version] WHERE [version] = '090'
)
BEGIN
    INSERT INTO [core].[schema_version] ([version], [description])
    VALUES ('090', N'Grant least-privilege application permissions');
END;
GO
