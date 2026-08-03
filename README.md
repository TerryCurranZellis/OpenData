# OpenData

OpenData is a Java 17 command-line framework that acquires external data,
transforms it into validated plugin-specific records and loads it into Microsoft
SQL Server. It is implemented as a modular monolith with independently selectable
plugins.

![OpenData project overview](docs/diagrams/generated/project-overview.svg)

## Current baseline

The repository and documentation describe **OpenData 2.0.0** as a development and
release-candidate baseline. It is not yet a production-ready release. The
remaining mandatory blockers and environment-dependent acceptance checks are
listed in the [final release checklist](docs/release/Final-Release-Checklist.md)
and [current readiness assessment](docs/review/RELEASE-READINESS-STATUS-2.0.0.md).

Version 2.0.0 introduces database-backed configuration registration,
certificate-based bootstrap password protection, the common five-phase plugin
lifecycle, and local Octopus statement ingestion. Version 1.0.0 material is
retained as historical release documentation.

## Implemented capabilities

- `--register` stores application and plugin configuration in SQL Server.
- The bootstrap properties file retains database connection details and an
  encrypted database password after registration.
- Ofgem and OpenMeteo support dry-run and write-mode execution.
- Octopus discovers local statement PDFs, prevents duplicate completed-file
  processing, batch transforms new statements, persists records transactionally
  and archives successfully loaded files.
- Multiple plugins can execute concurrently with bounded parallelism.
- Technical, administrator, developer and API manuals are composed from JSON
  manifests, Markdown and generated SVG diagrams.

## Important current limitations

- Octopus dry-run still reads its processed-file ledger and therefore requires a
  database resource that the framework intentionally does not provide in dry-run
  mode. Do not use Octopus or `--plugin all` as release dry-run acceptance until
  that source defect is corrected.
- The supplied private key, development PFX password and bootstrap password are
  not production-safe.
- The environment-variable PFX password path is defective; use the JVM system
  property only for controlled development until the code is fixed.
- The JDBC URL trusts the server certificate in the development configuration.
- The SQL Server JDBC dependency is a preview build.
- The Maven JAR is not a verified self-contained `java -jar` distribution.

## Installed plugins

| ID | Source | Version 2.0.0 acquisition |
|---|---|---|
| `ofgem` | Ofgem Energy Price Cap publication | Web discovery and workbook download |
| `openmeteo` | Open-Meteo Historical Weather API | HTTPS JSON API |
| `octopus` | Customer-supplied Octopus statement PDFs | Local configured directory; no website/email/API download |

## Requirements

- Java 17 or later while compiling for Java 17.
- Maven 3.9 or later.
- Microsoft SQL Server for registration and write-mode processing.
- PowerShell 5.1 or later for supplied Windows scripts.
- Pandoc and PlantUML when rebuilding generated manuals/diagrams.

## Build and launch

```powershell
git clone https://github.com/TerryCurranZellis/OpenData.git
Set-Location OpenData
mvn clean verify
mvn package
```

The reviewed POM creates `target/opendata-2.0.0.jar` but does not establish a
verified self-contained executable. Launch `com.towermarsh.opendata.OpenData`
from NetBeans or another classpath-aware launcher.

## Initial registration

Install SQL scripts in numeric order and create a deployment certificate pair.
For the first controlled registration the bootstrap file uses database mode
`false` and a temporary plaintext database password. Run:

```text
--register
```

Confirm the rewritten password begins with `{enc}`, restart, and verify database
configuration loading. Replace the supplied development certificate/private key
and rotate any password present in tracked history before production use.

## Common commands

```text
--help
--list-plugins
--register
--plugin ofgem --dry-run
--plugin openmeteo --dry-run
--plugin octopus
--plugin openmeteo,ofgem --parallelism 2
```

Do not present `--plugin octopus --dry-run` or `--plugin all --dry-run` as valid
in the current source baseline.

## Documentation map

| Need | Entry point |
|---|---|
| Quick start | [docs/guides/quick-start.md](docs/guides/quick-start.md) |
| User guide | [docs/user-guide/README.md](docs/user-guide/README.md) |
| Administrator operations | [docs/operations/README.md](docs/operations/README.md) |
| Architecture | [docs/architecture/ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md) |
| Plugin development | [docs/development/README.md](docs/development/README.md) |
| API/configuration reference | [docs/reference/README.md](docs/reference/README.md) |
| Governance and compliance | [docs/governance/README.md](docs/governance/README.md) |
| Release process | [docs/release/Release-Process.md](docs/release/Release-Process.md) |
| Documentation framework | [docs/README.md](docs/README.md) |

## Data, security and licensing

OpenData is Apache-2.0 licensed. Dependencies and data retain separate terms.
Review [SECURITY.md](SECURITY.md), [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md)
and [DATA-SOURCE-NOTICES.md](DATA-SOURCE-NOTICES.md). Never publish private keys,
credentials, Octopus statements, extracted customer data or database backups.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) and
[CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). Report sensitive vulnerabilities
privately as described in `SECURITY.md`.
