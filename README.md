# Octopus local statement extraction

Apply the files over the project root, then run `sql/007a-create-octopus-schema.sql` before executing the plugin.

The pipeline scans `C:\Attachments\octopus` for files named exactly:

`octopus-energy-statement-YYYY-MM-DD.pdf`

Completed files are identified by filename plus SHA-256 in `octopus.statement_file`. All new files are extracted and transformed as one batch. Electricity rows, gas rows, and file-ledger rows are committed in one transaction. A successful non-dry run moves processed PDFs to the configured archive directory.
