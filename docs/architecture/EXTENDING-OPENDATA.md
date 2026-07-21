# Adding a New Dataset Plugin

## 1. Choose a plugin identifier

Use a stable lower-case identifier:

```text
ons
environment-agency
met-office
```

The identifier must be unique and suitable for:

```text
--plugin <id>
```

## 2. Create the package

```text
src/main/java/com/towermarsh/opendata/plugins/<id>/
```

Recommended contents:

```text
<id>/
├── package-info.java
├── <Name>Plugin.java
├── acquisition/
├── config/
├── domain/
├── parsing/
├── persistence/
├── service/
└── validation/
```

## 3. Add default configuration

```text
src/main/resources/plugins/<id>/default.properties
```

Every key must be documented in the plugin's configuration schema.

## 4. Implement the contract

Implement:

```java
DatasetPlugin
```

Provide:

- Descriptor.
- Configuration schema.
- Execution behaviour.
- Structured run report.

## 5. Define the source contract

Document:

- Source owner.
- Endpoint or retrieval process.
- Format.
- Encoding.
- Required columns.
- Natural key.
- Update frequency.
- Licensing and attribution.
- Expected volume.
- Known quality issues.

## 6. Implement acquisition

Prefer the shared `DownloadService`.

A plugin-specific acquisition adapter may determine:

- URI.
- Query parameters.
- Request headers.
- Expected content type.
- Source date.
- Conditional request values.

## 7. Implement parsing and validation

- Keep source parser separate from domain validation.
- Preserve source row identity.
- Define stable validation codes.
- Add representative fixtures.
- Add a test for provider format changes.

## 8. Implement persistence

- Add versioned DDL.
- Define natural and surrogate keys.
- Add required indexes.
- Implement idempotent insert/update.
- Store run lineage.
- Add integration tests.

## 9. Register the plugin

For explicit registration, add it in the application bootstrap.

Do not add `if (plugin.equals(...))` logic to the core.

## 10. Add documentation

Create:

```text
docs/datasets/<id>/
├── README.md
├── SOURCE.md
├── CONFIGURATION.md
├── DATABASE.md
└── OPERATIONS.md
```

## 11. Complete the checklist

- [ ] Unique plugin ID
- [ ] `package-info.java`
- [ ] Descriptor
- [ ] Default properties
- [ ] Configuration schema
- [ ] Source contract
- [ ] Parser tests
- [ ] Validation tests
- [ ] DDL migration
- [ ] Persistence integration tests
- [ ] Idempotency test
- [ ] Dry-run test
- [ ] JUL logging only
- [ ] No committed secrets
- [ ] Dataset documentation
- [ ] Architecture contract tests pass
