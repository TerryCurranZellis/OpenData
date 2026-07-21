# Logging and Operations

## 1. Logging standard

OpenData uses only:

```java
java.util.logging
```

Project code must not directly depend on Log4j, Logback, or SLF4J.

When a required third-party library uses another framework:

- Prefer configuring it to use JUL.
- Otherwise use the smallest appropriate bridge.
- Exclude unnecessary logging implementations.
- Document the bridge in dependency documentation.

## 2. Logger naming

Use the class logger:

```java
private static final Logger LOGGER =
        Logger.getLogger(CurrentClass.class.getName());
```

Package names provide natural categories.

## 3. Levels

| JUL level | Use |
|---|---|
| `SEVERE` | Run cannot continue or data integrity is at risk |
| `WARNING` | Recoverable anomaly, rejected records, deprecated configuration |
| `INFO` | Start, end, source selected, counts, major lifecycle stage |
| `CONFIG` | Safe effective settings and component configuration |
| `FINE` | Detailed diagnostic flow |
| `FINER` | Per-batch or internal state details |
| `FINEST` | Very verbose diagnostics, normally disabled |

Avoid per-record `INFO` logging.

## 4. Required run messages

A run should log:

- Application and plugin version.
- Run ID.
- Selected plugin.
- Safe configuration summary.
- Source selected.
- Download completion and checksum.
- Parse and validation counts.
- Persistence counts.
- Final status and elapsed duration.
- Failure category and exception stack when appropriate.

## 5. Log format

Recommended fields:

```text
timestamp level runId logger message
```

Example:

```text
2026-07-21T21:15:31.412Z INFO 1bb... com.towermarsh.opendata.core.RunCoordinator Run started: plugin=ofgem
```

## 6. Exception logging

- Log an exception stack once at the correct boundary.
- Do not log and rethrow repeatedly at every layer.
- Wrap exceptions only when adding meaningful domain context.
- Preserve the cause.
- Ensure user-facing messages are concise and safe.

## 7. Run summary

At completion:

```text
Run ID:             ...
Plugin:             ofgem
Status:             SUCCEEDED
Source checksum:    ...
Records read:       120
Records accepted:   120
Records rejected:   0
Records inserted:   12
Records updated:    3
Records unchanged:  105
Duration:            PT4.281S
```

## 8. Operational checks

Before enabling a schedule:

- `--help` works.
- `--list-plugins` displays `ofgem`.
- `--dry-run` succeeds.
- Archive directory is writable.
- SQL connectivity is verified.
- Logging directory is writable.
- The service account can read parameter and secret files.
- A failed run returns a non-zero exit code.

## 9. Recovery

### Download failure

- Retain diagnostic metadata.
- Remove incomplete temporary files.
- Retry only according to bounded policy.
- Re-run normally after the provider recovers.

### Parse or validation failure

- Retain source according to policy.
- Record source checksum and failure.
- Compare the provider's format with plugin contract tests.
- Do not load partially trusted data unless plugin policy explicitly allows it.

### Database failure

- Confirm transaction rollback.
- Resolve connectivity, permissions, capacity, or schema issue.
- Re-run using the same source where possible.
- Idempotency must prevent duplicate results.

## 10. Support bundle

A future support-bundle command may collect:

- Application version.
- Plugin descriptors.
- Safe configuration summary.
- Recent logs.
- Run metadata.
- Source metadata without source content.
- JVM and operating-system information.

It must never include secrets.
