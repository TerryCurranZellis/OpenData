USE [OpenData];
GO

IF SCHEMA_ID(N'core') IS NULL
BEGIN
    EXEC (N'CREATE SCHEMA [core] AUTHORIZATION [dbo]');
END;
GO

IF OBJECT_ID(N'core.schema_version', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[schema_version]
    (
        [version] varchar(20) NOT NULL,
        [description] nvarchar(200) NOT NULL,
        [applied_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_schema_version_applied_at]
            DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_schema_version]
            PRIMARY KEY CLUSTERED ([version])
    );
END;
GO

IF OBJECT_ID(N'core.dataset', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[dataset]
    (
        [dataset_id] int IDENTITY(1, 1) NOT NULL,
        [plugin_code] varchar(50) NOT NULL,
        [dataset_code] varchar(100) NOT NULL,
        [dataset_name] nvarchar(200) NOT NULL,
        [publisher_name] nvarchar(200) NOT NULL,
        [description] nvarchar(1000) NULL,
        [source_page_url] nvarchar(2048) NULL,
        [is_active] bit NOT NULL
            CONSTRAINT [DF_dataset_is_active] DEFAULT (1),
        [created_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_dataset_created_at] DEFAULT SYSUTCDATETIME(),
        [updated_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_dataset_updated_at] DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_dataset]
            PRIMARY KEY CLUSTERED ([dataset_id]),
        CONSTRAINT [UQ_dataset_code]
            UNIQUE ([dataset_code])
    );
END;
GO

IF OBJECT_ID(N'core.ingestion_run', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[ingestion_run]
    (
        [ingestion_run_id] bigint IDENTITY(1, 1) NOT NULL,
        [dataset_id] int NOT NULL,
        [status] varchar(32) NOT NULL,
        [started_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_ingestion_run_started_at]
            DEFAULT SYSUTCDATETIME(),
        [finished_at] datetime2(3) NULL,
        [source_page_url] nvarchar(2048) NULL,
        [host_name] nvarchar(255) NULL,
        [application_version] varchar(50) NULL,
        [rows_extracted] bigint NOT NULL
            CONSTRAINT [DF_ingestion_run_rows_extracted] DEFAULT (0),
        [rows_loaded] bigint NOT NULL
            CONSTRAINT [DF_ingestion_run_rows_loaded] DEFAULT (0),
        [rows_rejected] bigint NOT NULL
            CONSTRAINT [DF_ingestion_run_rows_rejected] DEFAULT (0),
        [status_message] nvarchar(2000) NULL,
        [duration_ms] AS
            (CASE WHEN [finished_at] IS NULL THEN NULL
                  ELSE DATEDIFF_BIG(MILLISECOND, [started_at], [finished_at])
             END),
        CONSTRAINT [PK_ingestion_run]
            PRIMARY KEY CLUSTERED ([ingestion_run_id]),
        CONSTRAINT [FK_ingestion_run_dataset]
            FOREIGN KEY ([dataset_id]) REFERENCES [core].[dataset] ([dataset_id]),
        CONSTRAINT [CK_ingestion_run_status]
            CHECK ([status] IN
                ('STARTED', 'SUCCEEDED', 'SUCCEEDED_WITH_REJECTIONS',
                 'FAILED', 'CANCELLED')),
        CONSTRAINT [CK_ingestion_run_row_counts]
            CHECK ([rows_extracted] >= 0
               AND [rows_loaded] >= 0
               AND [rows_rejected] >= 0),
        CONSTRAINT [CK_ingestion_run_finished]
            CHECK (([status] = 'STARTED' AND [finished_at] IS NULL)
                OR ([status] <> 'STARTED' AND [finished_at] IS NOT NULL))
    );

    CREATE INDEX [IX_ingestion_run_dataset_started]
        ON [core].[ingestion_run] ([dataset_id], [started_at] DESC);

    CREATE INDEX [IX_ingestion_run_status]
        ON [core].[ingestion_run] ([status], [started_at] DESC);
END;
GO

IF OBJECT_ID(N'core.source_file', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[source_file]
    (
        [source_file_id] bigint IDENTITY(1, 1) NOT NULL,
        [ingestion_run_id] bigint NOT NULL,
        [source_uri] nvarchar(2048) NOT NULL,
        [original_file_name] nvarchar(512) NOT NULL,
        [content_type] nvarchar(255) NULL,
        [size_bytes] bigint NOT NULL,
        [sha256] char(64) NOT NULL,
        [downloaded_at] datetime2(3) NOT NULL,
        [created_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_source_file_created_at] DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_source_file]
            PRIMARY KEY CLUSTERED ([source_file_id]),
        CONSTRAINT [FK_source_file_ingestion_run]
            FOREIGN KEY ([ingestion_run_id])
            REFERENCES [core].[ingestion_run] ([ingestion_run_id]),
        CONSTRAINT [UQ_source_file_run_hash]
            UNIQUE ([ingestion_run_id], [sha256]),
        CONSTRAINT [CK_source_file_size]
            CHECK ([size_bytes] >= 0),
        CONSTRAINT [CK_source_file_sha256]
            CHECK ([sha256] NOT LIKE '%[^0-9a-fA-F]%')
    );

    CREATE INDEX [IX_source_file_sha256]
        ON [core].[source_file] ([sha256]);
END;
GO

IF OBJECT_ID(N'core.ingestion_error', N'U') IS NULL
BEGIN
    CREATE TABLE [core].[ingestion_error]
    (
        [ingestion_error_id] bigint IDENTITY(1, 1) NOT NULL,
        [ingestion_run_id] bigint NOT NULL,
        [source_file_id] bigint NULL,
        [source_row_number] bigint NULL,
        [processing_stage] varchar(50) NOT NULL,
        [error_code] varchar(100) NULL,
        [error_message] nvarchar(4000) NOT NULL,
        [raw_payload] nvarchar(max) NULL,
        [created_at] datetime2(3) NOT NULL
            CONSTRAINT [DF_ingestion_error_created_at]
            DEFAULT SYSUTCDATETIME(),
        CONSTRAINT [PK_ingestion_error]
            PRIMARY KEY CLUSTERED ([ingestion_error_id]),
        CONSTRAINT [FK_ingestion_error_run]
            FOREIGN KEY ([ingestion_run_id])
            REFERENCES [core].[ingestion_run] ([ingestion_run_id]),
        CONSTRAINT [FK_ingestion_error_file]
            FOREIGN KEY ([source_file_id])
            REFERENCES [core].[source_file] ([source_file_id]),
        CONSTRAINT [CK_ingestion_error_row]
            CHECK ([source_row_number] IS NULL OR [source_row_number] > 0)
    );

    CREATE INDEX [IX_ingestion_error_run]
        ON [core].[ingestion_error]
            ([ingestion_run_id], [created_at]);
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM [core].[schema_version] WHERE [version] = '010'
)
BEGIN
    INSERT INTO [core].[schema_version] ([version], [description])
    VALUES ('010', N'Create shared dataset and ingestion audit schema');
END;
GO
