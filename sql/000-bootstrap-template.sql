/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
/*
    Optional SQLCMD-mode bootstrap for a new installation.
    The password is deliberately not included in this package.

    Example:
      sqlcmd -S localhost -E -v OpenDataPassword="a-strong-password" -i 000-bootstrap-template.sql
*/
:setvar OpenDataPassword "CHANGE_ME"

USE [master];
GO

IF DB_ID(N'OpenData') IS NULL
BEGIN
    CREATE DATABASE [OpenData];
END;
GO

IF SUSER_ID(N'OpenData') IS NULL
BEGIN
    DECLARE @sql nvarchar(max) =
        N'CREATE LOGIN [OpenData] WITH PASSWORD = N''' +
        REPLACE(N'$(OpenDataPassword)', N'''', N'''''') +
        N''', CHECK_POLICY = ON, CHECK_EXPIRATION = OFF;';
    EXEC sys.sp_executesql @sql;
END;
GO

USE [OpenData];
GO

IF USER_ID(N'OpenData') IS NULL
BEGIN
    CREATE USER [OpenData] FOR LOGIN [OpenData];
END;
GO

IF DATABASE_PRINCIPAL_ID(N'opendata_app') IS NULL
BEGIN
    CREATE ROLE [opendata_app] AUTHORIZATION [dbo];
END;
GO

ALTER ROLE [opendata_app] ADD MEMBER [OpenData];
GO
