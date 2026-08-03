# 8. Octopus Energy Statements

**Document ID:** USER-008-OCTOPUS  
**Version:** 2.0  
**Status:** Runtime implemented with dry-run limitation  
**Baseline date:** 3 August 2026

---

The Octopus plugin imports PDF billing statements that the operator has already
obtained legitimately. It does not download bills from the Octopus website or
API.

## Required file name

Place each PDF in the configured input directory using:

```text
octopus-energy-statement-YYYY-MM-DD.pdf
```

Only regular files matching this pattern, case-insensitively, are considered.
The date in the file name is used as the statement date and files are processed
in date/name order.

## Required configuration

```properties
property.input.directory.value=C:\Attachments\octopus\incoming
property.working.directory.value=C:\Attachments\octopus\working
property.archive.directory.value=C:\Attachments\octopus\archive
```

All three properties must be present. `working.directory` is currently unused.
Do not leave `archive.directory` blank because a blank path resolves to the
process working directory.

## Controlled acceptance run

Octopus dry run is not usable in the current baseline. Use disposable copies of
statements, an isolated database and an explicit archive directory:

```text
opendata --plugin octopus --file C:\OpenData\octopus.properties
```

The plugin:

1. lists matching PDFs;
2. calculates each file's SHA-256 hash;
3. skips a file only when the same name and hash already have a completed ledger
   row in `octopus.statement_file`;
4. extracts PDF text and parses electricity and gas records;
5. writes the complete batch and completed-file ledger in one JDBC transaction;
   and
6. moves source PDFs to the archive directory after the database commit.

If archiving fails, the database commit remains successful and a warning is
logged. The source files may therefore remain in the input directory even though
`octopus.statement_file` marks them completed; the next run will skip them by
name and hash.

## Verification

Review the plugin summary and query:

```sql
SELECT TOP (20) * FROM core.PluginRun
WHERE PluginId = 'octopus'
ORDER BY StartedAt DESC;

SELECT TOP (20) * FROM octopus.statement_file
ORDER BY processed_at DESC;

SELECT TOP (20) * FROM octopus.electric_data
ORDER BY bill_date DESC;

SELECT TOP (20) * FROM octopus.gas_data
ORDER BY bill_date DESC;
```

Protect statement files, logs and backups as personal financial data.
