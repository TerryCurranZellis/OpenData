# 2. Prerequisites and Installation

**Document ID:** USER-002  
**Current checked-in source:** OpenData Maven 4.2.0  
**First-run GUI feature baseline:** OpenData 4.0.0

## Required software

- **JDK 26 or later for the current complete Maven build.** The compiler release target is 24, but this is not a runtime-compatibility promise for the checked-in JavaFX 27 dependencies.
- Maven 3.9 or later, a working JavaFX desktop on Windows, and Microsoft SQL Server.
- Network access to the selected providers' APIs or input data, subject to their terms.
- Windows PowerShell 5.1 for the repository automation scripts.
- For documentation builds: Pandoc; Java and PlantUML for diagrams; XeLaTeX plus SVG conversion for PDF; Microsoft HTML Help Workshop for CHM.

## Compile and launch

From the repository root:

```powershell
mvn -Dquality.failOnViolation=true clean verify
mvn clean install
mvn -pl opendata-launcher exec:java
```

`opendata-launcher` is the runnable composition module. Core has no provider implementation dependencies. In NetBeans, run the launcher with the repository root as its working directory and the launcher runtime dependencies on its classpath.

## Initial installation

Copy the example file to writable `config/application.properties` (or use the packaged first-run defaults), leaving `init=true`. OpenData then starts the [three-step SQL Server first-run wizard](../guides/first-run-initialisation.md), followed by its progress/completion screen.

The wizard configures the database, application SQL login, AES password encryption and packaged providers. It registers discovered providers disabled and writes `init=false` on success. Do **not** use the old `.pfx`/`{enc}` bootstrap from 3.x guides for a new installation.

Existing installations should preserve their data, copy/migrate the current `config/application.properties` and external AES key, and start with `init=false` only after verifying the database.

## Runtime directories

Provide writable log and plugin input/archive locations, as configured for your environment. For example:

```text
C:\OpenData\logs
C:\Attachments\octopus\incoming
C:\Attachments\octopus\archive
```

Protect energy statements and application secrets as private data. See [Configuration](04-configuration.md), [SQL Server setup](03-sql-server-setup.md) and the [quick start](../guides/quick-start.md).
