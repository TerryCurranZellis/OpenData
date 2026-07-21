# OpenData Components

## 1. Application bootstrap

### Package

```text
com.towermarsh.opendata.app
```

### Responsibilities

- Define the composition root.
- Initialise JUL logging.
- Construct shared infrastructure.
- Parse the process result into an operating-system exit code.
- Close resources in a defined order.

### Principal classes

| Class | Responsibility |
|---|---|
| `OpenDataApplication` | `main` entry point and top-level exception boundary |
| `ApplicationBootstrap` | Creates and connects application services |
| `ApplicationRunner` | Runs one parsed command request |
| `ExitCode` | Stable process exit-code enumeration |

The `main` method should remain very small.

## 2. Command-line adapter

### Package

```text
com.towermarsh.opendata.cli
```

### Responsibilities

- Define Apache Commons CLI options.
- Parse arguments.
- Validate global option combinations.
- Display help and version information.
- Convert input into a framework `CommandRequest`.

### Suggested classes

| Class | Responsibility |
|---|---|
| `CliOptionsFactory` | Creates the Commons CLI `Options` model |
| `CliParser` | Parses the raw argument array |
| `CommandRequest` | Immutable parsed command |
| `HelpPrinter` | Produces consistent help text |
| `CliValidationException` | Represents usage errors |

### Core options

| Long option | Short | Value | Meaning |
|---|---:|---|---|
| `--plugin` | `-p` | plugin ID | Selects the dataset plugin |
| `--file` | `-f` | path | Optional parameter-file override |
| `--help` | `-h` | none | Displays help |
| `--version` | `-V` | none | Displays application version |
| `--list-plugins` | none | none | Lists installed plugins |
| `--dry-run` | none | none | Validates and plans without database mutation |

A plugin must never receive or interpret the raw argument array.

## 3. Execution core

### Package

```text
com.towermarsh.opendata.core
```

### Responsibilities

- Coordinate one application run.
- Allocate the run ID.
- Resolve the plugin.
- Load effective configuration.
- Create the execution context.
- Invoke the plugin.
- Convert failures into a run report and exit code.

### Suggested classes

| Class | Responsibility |
|---|---|
| `RunCoordinator` | Governs the complete lifecycle |
| `RunId` | Type-safe unique run identifier |
| `RunStatus` | Success, partial success, failure, or skipped |
| `RunReport` | Structured run outcome |
| `RecordCounts` | Read, accepted, rejected, inserted, updated, unchanged |
| `FailureCategory` | Stable error classification |
| `ClockProvider` | Injectable time source for repeatable tests |

## 4. Plugin contracts

### Package

```text
com.towermarsh.opendata.plugin
```

### Core interface

```java
public interface DatasetPlugin {
    PluginDescriptor descriptor();

    ConfigurationSchema configurationSchema();

    RunReport execute(PluginExecutionContext context);
}
```

### Supporting types

| Type | Purpose |
|---|---|
| `PluginDescriptor` | ID, name, version, description, capabilities |
| `PluginExecutionContext` | Shared services and validated plugin configuration |
| `PluginConfiguration` | Immutable effective settings |
| `ConfigurationSchema` | Required keys, types, defaults, and validation |
| `PluginRegistry` | Resolves plugin IDs |
| `PluginNotFoundException` | Unknown plugin |
| `PluginCapability` | Download, file import, incremental load, full refresh, etc. |

## 5. Configuration service

### Package

```text
com.towermarsh.opendata.config
```

### Responsibilities

- Load plugin default properties.
- Load an optional override file.
- Merge values deterministically.
- Convert strings to typed values.
- Validate keys and cross-field rules.
- Produce a safe configuration summary.
- Calculate a fingerprint without exposing secrets.

### Suggested classes

| Class | Responsibility |
|---|---|
| `ConfigurationService` | Coordinates the complete configuration process |
| `ParameterFileLoader` | Reads UTF-8 `.properties` files |
| `ConfigurationMerger` | Applies override precedence |
| `ConfigurationValidator` | Performs schema validation |
| `ConfigurationValueConverter` | Converts to URI, path, integer, boolean, duration, enum |
| `ConfigurationFingerprint` | Stable hash of non-secret effective settings |
| `SecretValue` | Prevents accidental rendering of protected values |

## 6. Download service

### Package

```text
com.towermarsh.opendata.download
```

### Responsibilities

- Execute HTTP/HTTPS requests.
- Apply connection and read timeouts.
- Apply safe redirect rules.
- Use bounded retries where appropriate.
- Capture response metadata.
- Stream content to storage.
- Reject unsupported content.

### Suggested interfaces and classes

| Type | Responsibility |
|---|---|
| `DownloadService` | Public acquisition contract |
| `JdkHttpDownloadService` | `java.net.http.HttpClient` implementation |
| `DownloadRequest` | URI, headers, timeouts, and expected content |
| `DownloadResult` | Status, headers, size, checksum, and stored path |
| `RetryPolicy` | Bounded retry strategy |
| `ContentTypePolicy` | Allowed media types |
| `DownloadException` | Acquisition failure |

Only this package may directly use `HttpClient`.

## 7. File and checksum services

### Package

```text
com.towermarsh.opendata.file
```

### Responsibilities

- Create deterministic archive paths.
- Prevent path traversal.
- Write source content atomically.
- Calculate checksums.
- Maintain temporary and final file states.
- Provide retention and cleanup hooks.

### Suggested classes

| Class | Responsibility |
|---|---|
| `SourceFileStore` | Stores source files and metadata |
| `ArchivePathFactory` | Builds plugin/date/run based paths |
| `ChecksumService` | Calculates SHA-256 |
| `AtomicFileWriter` | Writes temporary file then moves atomically |
| `FileRetentionPolicy` | Defines safe cleanup rules |

## 8. Persistence infrastructure

### Package

```text
com.towermarsh.opendata.persistence
```

### Responsibilities

- Create SQL Server connections.
- Define transactions.
- Execute prepared statements.
- Support JDBC batch operations.
- Persist run and source metadata.
- Translate database exceptions into domain failures.

### Suggested classes

| Class | Responsibility |
|---|---|
| `SqlServerConnectionFactory` | Creates configured JDBC connections |
| `TransactionManager` | Executes work within transaction boundaries |
| `JdbcBatchWriter` | Shared prepared-statement batching |
| `RunMetadataRepository` | Stores and updates run status |
| `SourceMetadataRepository` | Stores source provenance |
| `DatabaseExceptionTranslator` | Produces meaningful persistence exceptions |

Only the persistence package and plugin repository implementations may depend on JDBC types.

## 9. Ofgem plugin

### Package

```text
com.towermarsh.opendata.plugins.ofgem
```

### Suggested subpackages

```text
plugins.ofgem
├── acquisition
├── config
├── domain
├── parsing
├── persistence
├── service
└── validation
```

### Suggested classes

| Class | Responsibility |
|---|---|
| `OfgemPlugin` | Implements `DatasetPlugin` and coordinates Ofgem processing |
| `OfgemConfiguration` | Typed immutable settings |
| `OfgemSourceLocator` | Determines source endpoint or file |
| `OfgemParser` | Converts source rows into candidate records |
| `OfgemValidator` | Applies dataset rules |
| `OfgemMapper` | Converts candidates into persistence records |
| `OfgemRepository` | Performs SQL Server writes |
| `OfgemLoadService` | Coordinates parse, validate, and persist |
| `OfgemRecord` | Typed dataset domain record |
| `OfgemReject` | Captures rejected record and reason |

## 10. Observability

### Package

```text
com.towermarsh.opendata.logging
```

### Responsibilities

- Initialise JUL configuration.
- Establish logger naming conventions.
- Attach the run ID to messages through a formatting strategy.
- Prevent secrets in diagnostic output.
- Provide standard run-summary logging.

### Suggested classes

| Class | Responsibility |
|---|---|
| `LoggingBootstrap` | Loads `logging.properties` |
| `OpenDataLogFormatter` | Formats timestamp, level, logger, run ID, and message |
| `RunLogContext` | Makes the run ID available without passing it into every log call |
| `SafeLogValue` | Marks values approved for logging |

## 11. Component collaboration

```mermaid
flowchart LR
    CLI[CliParser] --> CMD[CommandRequest]
    CMD --> COORD[RunCoordinator]
    COORD --> REG[PluginRegistry]
    COORD --> CFG[ConfigurationService]
    REG --> PLUGIN[DatasetPlugin]
    CFG --> PCFG[PluginConfiguration]
    COORD --> CTX[PluginExecutionContext]
    CTX --> PLUGIN
    PLUGIN --> DL[DownloadService]
    PLUGIN --> FS[SourceFileStore]
    PLUGIN --> TX[TransactionManager]
    TX --> REPO[Plugin Repository]
    PLUGIN --> REPORT[RunReport]
    REPORT --> COORD
