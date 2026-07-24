/*
    OpenData SQL Server bootstrap.

    Run with an administrator login using SQLCMD mode, for example:

      sqlcmd -S localhost -E -i 001-create-database-and-login.sql \
             -v OpenDataPassword="replace-with-a-strong-local-password"

    Do not commit the real password. SQLCMD performs textual substitution, so
    use a password without a single-quote character when running this script.
*/
:setvar OpenDataPassword "CHANGE_ME_BEFORE_RUNNING"

USE [master];
GO

IF DB_ID(N'OpenData') IS NULL
BEGIN
    PRINT N'Creating database [OpenData].';
    EXEC (N'CREATE DATABASE [OpenData]');
END;
GO

IF N'$(OpenDataPassword)' = N'CHANGE_ME_BEFORE_RUNNING'
BEGIN
    THROW 50001, 'Set the OpenDataPassword SQLCMD variable before running this script.', 1;
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.server_principals
    WHERE [name] = N'OpenData'
)
BEGIN
    PRINT N'Creating SQL login [OpenData].';
    CREATE LOGIN [OpenData]
        WITH PASSWORD = '$(OpenDataPassword)',
             DEFAULT_DATABASE = [OpenData],
             CHECK_POLICY = ON,
             CHECK_EXPIRATION = OFF;
END;
GO

USE [OpenData];
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.database_principals
    WHERE [name] = N'OpenData'
)
BEGIN
    CREATE USER [OpenData] FOR LOGIN [OpenData];
END;
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

IF NOT EXISTS (
    SELECT 1
    FROM sys.database_role_members AS drm
    INNER JOIN sys.database_principals AS role_principal
        ON role_principal.principal_id = drm.role_principal_id
    INNER JOIN sys.database_principals AS member_principal
        ON member_principal.principal_id = drm.member_principal_id
    WHERE role_principal.[name] = N'opendata_app'
      AND member_principal.[name] = N'OpenData'
)
BEGIN
    ALTER ROLE [opendata_app] ADD MEMBER [OpenData];
END;
GO
