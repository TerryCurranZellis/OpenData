# OpenData

OpenData is a Java 17 command-line framework for acquiring, validating,
transforming and loading public datasets into Microsoft SQL Server. Dataset
integrations are implemented as configuration-driven plugins within a modular
monolith.

> **Project status:** active development and release preparation. Ofgem and
> OpenMeteo workflows have been exercised through dry runs, but the repository is
> not yet declared production-ready. Executable packaging, clean-database
> acceptance tests, live write and rollback verification, and audit-model
> consolidation remain release gates.

## Capabilities

- Select one or more plugins, including `--plugin all`.
- Run plugins concurrently with bounded parallelism.
- Perform side-effect-free dry runs.
- Discover and process Ofgem price-cap workbooks.
- Download and persist OpenMeteo historical daily weather.
- Parse CSV, JSON, HTML-linked files, Excel workbooks and PDF text through shared
  infrastructure.
- Use pooled SQL Server connections and plugin execution auditing.
- Generate technical and user manuals from Markdown, PlantUML and a
  configuration-driven documentation manifest.

## Requirements

- Java 17 or later runtime with Java 17 compilation compatibility.
- Maven 3.9 or later.
- Microsoft SQL Server for persistence workflows.
- PowerShell 5.1 or later for repository automation.
- Pandoc and PlantUML only when generating manuals and diagrams.

## Build

```powershell
git clone https://github.com/TerryCurranZellis/OpenData.git
Set-Location OpenData
mvn clean test
mvn package
```

The current Maven build produces a library JAR without bundled dependencies or a
`Main-Class`. Run `com.towermarsh.opendata.Main` from NetBeans or another
classpath-aware launcher until executable packaging is completed. Do not use
`java -jar target/opendata-1.0.0.jar` as a production command.

## Configuration

Runtime configuration is loaded from:

```text
src/main/resources/config/application.properties
src/main/resources/config/plugins/index.properties
src/main/resources/config/plugins/<plugin-id>.properties
```

An external `--file` may override application and plugin values. When several
plugins share one override file, plugin settings must use the
`plugin.<id>.<key>` prefix. Never commit real passwords or access tokens.

The complete configuration model is described in the
[configuration reference](docs/reference/configuration-reference.md).

## Command-line examples

```powershell
# Show available options
--help

# List registered plugins
--list-plugins

# Validate an Ofgem run without writing data
--plugin ofgem --dry-run

# Run all configured plugins with bounded concurrency
--plugin all --parallelism 2

# Apply external settings
--plugin openmeteo --file C:\OpenData\config\local.properties
```

The exact launcher prefix depends on the IDE or classpath runner until executable
packaging is added. See the
[command-line reference](docs/reference/command-line-reference.md).

## Documentation

| Area | Entry point |
|---|---|
| User guide | [docs/user-guide/README.md](docs/user-guide/README.md) |
| Architecture | [docs/architecture/ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md) |
| Development | [docs/development/README.md](docs/development/README.md) |
| Operations | [docs/operations/README.md](docs/operations/README.md) |
| Reference | [docs/reference/README.md](docs/reference/README.md) |
| ADR register | [docs/decisions/ADR-REGISTER.md](docs/decisions/ADR-REGISTER.md) |
| Adding a plugin | [docs/guides/adding-a-plugin.md](docs/guides/adding-a-plugin.md) |
| Documentation framework | [docs/README.md](docs/README.md) |

Generate documentation with `scripts/Invoke-Documentation.ps1`; render PlantUML
sources with `scripts/Convert-PlantUml.ps1`.

## Contributing and support

Read [CONTRIBUTING.md](CONTRIBUTING.md) before submitting a change. Participation
is governed by [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). Security vulnerabilities
must be reported privately as described in [SECURITY.md](SECURITY.md).

For defects and feature requests that are not security-sensitive, use the GitHub
issue tracker and include reproducible steps, relevant logs with secrets removed,
and the version or commit tested.

## Licence

OpenData is licensed under the Apache License, Version 2.0. See [`LICENSE`](LICENSE), [`NOTICE`](NOTICE), and the [licensing policy](docs/Licensing-Policy.md).
