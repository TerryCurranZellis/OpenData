# OpenData

OpenData is a Java 17 command-line framework that acquires external data,
transforms it into validated plugin-specific records and loads it into Microsoft
SQL Server. It is implemented as a modular monolith with independently
registered and selectable plugins.

![OpenData project overview](docs/diagrams/generated/project-overview.svg)

## Current baseline

The repository and documentation describe **OpenData 2.1.0** as a development
and release-candidate baseline. It is not yet a production-ready release. The
remaining mandatory blockers and environment-dependent acceptance checks are
listed in the [final release checklist](docs/release/Final-Release-Checklist.md)
and [current readiness assessment](docs/review/RELEASE-READINESS-STATUS-2.0.0.md).

Version 2.1.0 centralises application validation, exception messages, elapsed
duration formatting and build-derived application identity, and logs the
product name and version at startup. Version 2.0.0 introduced a persistent plugin registry, database-backed
configuration, certificate-based bootstrap password protection, the common
five-phase plugin lifecycle, local Octopus statement ingestion, and an explicit
`--Execute`/`-x` execution gate. Version 1.0.0 material is retained as historical
release documentation.

## Implemented capabilities

- Register one, several, or all packaged plugins with `--register`.
- Register one named plugin from an external UTF-8 properties file with
  `--register --file <filename>`.
- Persist registered plugin metadata, enabled/disabled status and complete
  plugin configuration in SQL Server.
- List, enable, disable and unregister plugins from the command line.
- Run one, several, or all enabled registered plugins only when execution is
  explicitly authorised with `--Execute` or `-x`.
- Perform side-effect-free dry runs for Ofgem, OpenMeteo and Octopus without
  plugin data writes or generic run-audit rows; dry-runs also require
  `--Execute`.
- Execute multiple plugins concurrently with bounded parallelism.
- Build Technical, Administrator, Developer and API manuals from JSON manifests,
  Markdown and generated SVG diagrams.

## Important current limitations

- The supplied private key, development PFX password and bootstrap password are
  not production-safe.
- The environment-variable PFX password path is defective; use the JVM system
  property only for controlled development until the code is fixed.
- The JDBC URL trusts the server certificate in the development configuration.
- The SQL Server JDBC dependency is a preview build.
- The Maven JAR is not a verified self-contained `java -jar` distribution.
- Target-environment Maven, SQL Server and PowerShell acceptance evidence is
  still required before release approval.

## Installed plugin definitions

| ID | Source | Version 2.1.0 acquisition |
|---|---|---|
| `ofgem` | Ofgem Energy Price Cap publication | Web discovery and workbook download |
| `openmeteo` | Open-Meteo Historical Weather API | HTTPS JSON API |
| `octopus` | Customer-supplied Octopus statement PDFs | Local configured directory; no website/email/API download |

Packaged definitions are registration sources. The authoritative runtime list is
stored in `core.plugin_registry` and displayed with `--list-plugins`.

## Requirements

- Java 17 or later while compiling for Java 17.
- Maven 3.9 or later.
- Microsoft SQL Server for registration, registry administration, configuration
  loading and write-mode processing.
- PowerShell 5.1 or later for supplied Windows scripts.
- Pandoc and PlantUML when rebuilding generated manuals/diagrams.

## Build and launch

```powershell
git clone https://github.com/TerryCurranZellis/OpenData.git
Set-Location OpenData
mvn clean verify
mvn package
```

The reviewed POM creates `target/opendata-2.1.0.jar` but does not establish a
verified self-contained executable. Launch `com.towermarsh.opendata.OpenData`
from NetBeans or another classpath-aware launcher.

## Database migration and initial registration

Install the SQL scripts in the order documented in `sql/README.md`, including
`003a-create-plugin-registry.sql`, and create a deployment certificate pair. For
the first controlled registration, the bootstrap file uses database-properties
mode `false` and a temporary plaintext database password.

```text
--plugin all --register
```

Confirm the rewritten password begins with `{enc}`, restart, and verify:

```text
--list-plugins
```

Replace the supplied development certificate/private key and rotate any password
present in tracked history before production use.

## Common commands

```text
--help
--about
--list-plugins
--plugin all --register
--plugin example --register --file C:\OpenData\example.properties
--plugin octopus --disable
--plugin octopus --enable
--plugin octopus --unregister
--plugin ofgem --Execute
--plugin ofgem --Execute --dry-run
--plugin all --Execute --dry-run --parallelism 3
--plugin openmeteo --plugin ofgem --Execute --parallelism 2
```

Normal and dry-run plugin execution requires `--Execute` or `-x`. The short
option `-d` is reserved for `--disable`; use `-n` for `--dry-run`.

## Documentation map

| Need | Entry point |
|---|---|
| Quick start | [docs/guides/quick-start.md](docs/guides/quick-start.md) |
| User guide | [docs/user-guide/README.md](docs/user-guide/README.md) |
| Administrator operations | [docs/operations/README.md](docs/operations/README.md) |
| Architecture | [docs/architecture/ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md) |
| Plugin development | [docs/development/README.md](docs/development/README.md) |
| API/configuration reference | [docs/reference/README.md](docs/reference/README.md) |
| Unix manual page | [docs/reference/opendata.1](docs/reference/opendata.1) |
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
