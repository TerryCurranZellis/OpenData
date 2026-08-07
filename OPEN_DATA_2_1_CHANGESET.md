# OpenData 2.1 change set

This archive contains the complete OpenData source tree with the version 2.1
refactoring applied.

## Batch 1 — application identity and shared utilities

- Maven project version is `2.1.0`.
- The JAR manifest receives the Maven implementation version.
- Maven filters `application-metadata.properties` from the project version.
- `ApplicationInfo` uses manifest metadata first and filtered metadata as its
  IDE fallback.
- Startup logging records program name/version, Java runtime, operating system,
  working directory, command, dry-run state and verbose state.
- `ExceptionMessages` centralises cycle-safe root-cause message extraction.
- `DurationFormatter` centralises total-hours `HH:mm:ss` formatting.

## Batch 2 — shared validation

- `ApplicationPropertyValues` centralises typed application and bootstrap
  property parsing.
- Runtime, bootstrap and standard configuration validation use the shared
  reader.
- Configuration records reuse `ValidationRules` for common invariants.
- Plugin configuration continues to use `PluginPropertyValues` and
  plugin-specific typed validators.
- The unused legacy `Validator`, `DataQualityValidator` and `ValidationResult`
  classes have been removed.

## Batch 3 — tests and documentation

- Unit tests cover typed application values, version metadata, exception cause
  traversal and duration formatting, including durations over 24 hours.
- ADR-0050 records the architectural decision and the ADR register is updated.
- README baseline and artifact names are updated for 2.1.0.
- All changed Java production/test classes are identified as `@version 2.1`.

## Verification

The available Java 17 compiler successfully compiled all new utilities,
metadata/configuration classes, validation integrations and the modified plugin
execution coordinator. `git diff --check` reports no whitespace errors.

This build environment does not provide Maven or cached third-party
dependencies, so run the full project verification after extracting:

```powershell
mvn clean verify
```

Then confirm the packaged metadata:

```powershell
jar xf target/opendata-2.1.0.jar META-INF/MANIFEST.MF application-metadata.properties
Get-Content META-INF/MANIFEST.MF
Get-Content application-metadata.properties
```

Both metadata locations should report version `2.1.0`.
