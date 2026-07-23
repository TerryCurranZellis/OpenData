# Current Code Inventory

Reviewed against the public `main` branch on 22 July 2026.

| Area | Current classes/contracts | Status |
|---|---|---|
| Bootstrap | `Main` | Implemented; pipeline hand-off pending |
| CLI | `CommandLineArguments`, `CommandLineArgumentsProcessor`, `CommandLineProcessingException` | Implemented |
| Configuration | `ApplicationConfig`, loaders, service, validators | Implemented |
| Download | `DataDownloader`, `HttpDataDownloader`, `DownloadResult` | Framework implemented |
| Parsing | `DataParser`, `CsvDataParser`, `JsonDataParser` | Framework implemented |
| Validation | `Validator`, `DataQualityValidator`, `ValidationResult` | Framework implemented |
| ETL | `ExtractService`, `TransformService`, `LoadService` | Stage contracts present |
| Persistence | `DatabaseConnectionManager`, `DatabaseRepository`, `SqlServerRepository` | SQL Server foundation present |
| Models | Dataset, source, file, and import result models | Present |
| Logging | `LoggingManager` and JUL usage | Present |
| Exceptions | Framework exception hierarchy | Present |
| Plugin registry | Static plugin-list output only | Not yet integrated |
| Ofgem execution | Identified as intended reference plugin | Not yet proven end-to-end from `Main` |
| Scheduling | External scheduling recommended | Internal scheduler deferred |

## Important consistency notes

- Maven compiler source and target are Java 17.
- Maven project version is `1.0.0`.
- `Main --version` currently prints `0.1.0-SNAPSHOT`; this should be centralised before release.
- The root README previously described Java 17 and version 0.1.0; Maven metadata and runtime version output are therefore not fully aligned.
- Architecture documents must avoid claiming that scheduling, complete plugin execution, or multi-database support are already production-ready.
