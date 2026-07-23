# Documentation Audit

## Summary

The earlier documentation contained a mixture of implemented architecture and target-state design. This update makes the implementation boundary explicit.

## Corrected statements

| Earlier statement | Current wording |
|---|---|
| Production ready | Active development; foundations implemented |
| Full plugin architecture | Plugin design accepted; registry/execution integration pending |
| Scheduling support | Internal scheduler deferred; use external scheduling |
| Database independent | Repository abstraction exists; SQL Server is the only current implementation |
| Complete ETL pipeline | Stage contracts exist; executable orchestration is not yet wired |
| Current version 0.1.0 | Maven version is 1.0.0; runtime banner still says 0.1.0-SNAPSHOT |
| Only original package list | Added current `cli` package and current configuration service design |

## Documents that should be treated as target-state material

Any document describing the following without an implementation qualifier should be revised or marked future-state:

- dynamic plugin loading;
- completed plugin manifest registry;
- internal scheduler;
- database-backed plugin settings;
- automatic run metadata persistence;
- multiple database engines;
- end-to-end Ofgem processing from `Main`.

## Recommended repository cleanup

1. Replace the root README with the supplied `PROJECT-README.md`.
2. Keep one canonical architecture overview.
3. Add links from older numbered architecture chapters to the canonical overview.
4. Centralise the application version so Maven metadata and `--version` agree.
5. Remove wording that calls the application production-ready until an end-to-end plugin run is tested.
