# Octopus Plugin Reference

**Document ID:** REF-PLUGIN-OCTOPUS-001
**Version:** 2.0
**Status:** Write-mode implementation reference
**Baseline date:** 3 August 2026

---

| Item | Value |
|---|---|
| Plugin id | `octopus` |
| Implementation class | `com.towermarsh.opendata.plugin.octopus.OctopusPlugin` |
| Dataset id | `octopus-energy-billing` |
| Source | Local PDF directory |
| Filename pattern | `octopus-energy-statement-YYYY-MM-DD.pdf` |
| Duplicate check | lower-cased filename plus SHA-256 against completed ledger rows |
| Persistence | One batch transaction for facts and file-ledger completion |
| Archive | Move source PDFs after successful commit |

## Active class flow

```text
OctopusPlugin
 -> initialise.OctopusInitialise
 -> extract.OctopusExtract / PdfTextExtractor
 -> transform.OctopusTransform / OctopusStatementParser
 -> load.OctopusLoad / OctopusPersistenceRepository
 -> finalise.OctopusFinalise
```

## Metrics

`read` is the total number of electricity and gas records parsed. Write metrics
count inserted or updated business rows. The processed-file ledger is updated in
the same transaction but is not counted as a business record.

## Failure conditions

- missing/non-directory input path;
- unsupported filename or invalid date in a candidate name;
- PDF text extraction failure;
- changed statement layout or missing required financial fields;
- absent Octopus schema or database permission failure;
- business-row or ledger transaction failure;
- post-commit archive move failure.

## Known dry-run defect

The extract stage always reads the completed-file ledger. In framework dry-run
mode that call receives an unavailable database resource and fails. The load and
archive stages are dry-run aware, but an end-to-end Octopus dry run is not
currently supported. `--plugin all --dry-run` is therefore also unsuitable.

See [plugin documentation](../plugins/octopus/README.md) and
[schema reference](octopus-schema.md).
