# OpenData 1.0.0

**Release date:** 29 July 2026  
**Licence:** Apache License 2.0

OpenData 1.0.0 is the first supported public release of the framework. It provides a Java 17, Maven-based plugin platform for downloading structured data from external sources and loading it into a local SQL Server database.

## Release highlights

- Ofgem and Open-Meteo reference plugins.
- Concurrent plugin execution and dry-run support.
- SQL Server connection pooling, ingestion auditing and bootstrap scripts.
- Configuration-driven technical and user documentation.
- Apache 2.0 licensing and open-source contribution policies.
- Checkstyle, PMD, SpotBugs, JaCoCo and Javadoc build reporting.
- GitHub Actions build, documentation and tagged-release automation.

## Requirements

- Java 17 or later.
- Maven 3.9 or later when building from source.
- SQL Server for persistent ingestion.
- PowerShell 5.1 or later for the supplied Windows automation scripts.
- Pandoc and PlantUML when rebuilding all documentation outputs.

## Verification

After extracting the source archive, run:

```powershell
mvn clean verify
./scripts/Validate-Documentation.ps1 -FailOnWarning
```

## Compatibility

This release uses semantic versioning. Configuration and database schema changes in later minor or major versions will be documented in `CHANGELOG.md` and the release notes.

## Known release considerations

- Database integration tests require access to a configured SQL Server instance and are environment dependent.
- The repository does not distribute database credentials or a local PlantUML JAR.
- Generated PDF appearance can vary slightly according to the locally installed Pandoc and TeX versions.
