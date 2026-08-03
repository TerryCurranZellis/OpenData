/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
USE [OpenData];
GO

IF DATABASE_PRINCIPAL_ID(N'opendata_app') IS NULL
BEGIN
    CREATE ROLE [opendata_app] AUTHORIZATION [dbo];
END;
GO

GRANT SELECT, INSERT, UPDATE ON SCHEMA::[core] TO [opendata_app];
GRANT SELECT, INSERT, UPDATE ON SCHEMA::[openmeteo] TO [opendata_app];
GRANT SELECT, INSERT, UPDATE ON SCHEMA::[octopus] TO [opendata_app];
GRANT DELETE ON OBJECT::[core].[plugin_property] TO [opendata_app];
GRANT DELETE ON OBJECT::[core].[plugin_registry] TO [opendata_app];
-- sp_getapplock uses @DbPrincipal='public'; no application-schema EXECUTE grant is required.
GO
