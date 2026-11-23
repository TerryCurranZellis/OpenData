# OpenData Quick Start (current Maven reactor)

**Document ID:** GUIDE-QUICKSTART-001  
**Source version:** 4.2.0 Maven project  
**Historical feature baseline:** 4.0.0 JavaFX first-run wizard  
**Build requirements:** Maven 3.9+ and JDK 26+ (compiler target 24)

## 1. Build and launch

From the repository root:

```powershell
mvn -Dquality.failOnViolation=true clean verify
mvn clean install
mvn -pl opendata-launcher exec:java
```

The launcher assembles Core, Common and the six provider JARs; do not run Core alone when you need packaged provider discovery. The first-run wizard requires a Windows graphical desktop with JavaFX.

## 2. Configure the database on a new installation

Copy `config/application.properties.example` to `config/application.properties` or use the packaged first-run defaults. Keep `init=true`. Launch OpenData and complete the three-step [first-run wizard](first-run-initialisation.md), followed by its progress/finish page.

Provide a reachable SQL Server, temporary SQL administrator credentials, a separate application SQL login and an external AES key-file path. The wizard creates/reuses the database and account, installs core and available plugin schemas, stores the encrypted application password and registers all discovered providers **disabled**. It sets `init=false` only when installation succeeds.

For an existing installation, **migrate the current bootstrap properties and AES key** rather than reinitialising production data. The [database configuration guide](database-configuration-and-security.md) explains the property `security.aes-key-file`.

## 3. Review and enable plugins

With initialisation complete, use the GUI table or the CLI to review registrations. All six providers should remain disabled until you approve their settings:

```text
opendata --list-plugins
opendata --plugin ofgem --detail
opendata --plugin ofgem --enable
```

If a provider was not discovered, inspect the packaged `config/plugins/index.properties` catalogue or use the GUI's Register/From File actions. Provider implementation classes must be present in the launcher classpath.

## 4. Safe execution

```text
opendata --plugin ofgem --dry-run
opendata --plugin ofgem --execute
opendata --plugin all --dry-run --parallelism 3
```

`--dry-run` (or `-n`) is separate from write-mode `--execute` (or `-x`). Check each provider's source availability, settings, log and audit before scheduling write-mode runs.

See the [technical user guide](../user-guide/README.md), [CLI reference](../reference/command-line-reference.md) and [SQL Server setup guide](sql-server-bootstrap.md) for further details.
