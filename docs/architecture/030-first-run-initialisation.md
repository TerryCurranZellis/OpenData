# First-run initialisation architecture (v4.0.0)

**Document ID:** ARCH-030  
**Status:** Implemented source, environment verification pending

## Flow and boundaries

`OpenData.main` reads `init` from `ApplicationBootstrapPropertiesLoader`
**before** accessing secrets or SQL Server. With `init=true` it launches
JavaFX regardless of invocation type. `OpenDataGuiApplication.start` then
shows `InitialisationWizard` instead of the normal splash/main window.

JavaFX form validation produces immutable `InitialisationRequest`. A background
JavaFX `Task` calls `InitialisationService.initialise`, which performs:

1. `PluginRegistrationResolver` + `PluginConfigurationDirectoryScanner`:
   resolve/validate packaged and external plugin definitions before network I/O.
2. `InitialisationService.prepareKey`: create or validate a 32-byte Base64 key
   file. No overwrite is permitted.
3. `SqlServerInitialiser.initialise`: use temporary administrator SQL connections
   to create the database, SQL login, and core tables from resource copies of
   `sql/002`, `003`, `003a` and `006`, using the existing `GO` parser.
4. `SqlServerInitialiser.installPluginSchemas`: invoke each plugin's
   `RegisterPlugin` hook using the administrator connection, allowing its
   DDL and `GRANT` statements.
5. `SQLServerResource` + `JdbcConfigurationPropertiesSource`: persist application
   runtime settings and AES ciphertext. `JdbcPluginRegistry.register` writes
   each disabled plugin and properties; `setEnabled(false)` ensures disabled
   status even when retrying against an existing row.
6. `ApplicationBootstrapPropertiesLoader.completeInitialisation` writes the
   configured deployment properties through an atomic temporary file and
   switches `init=false`. This is the final success marker.
7. JavaFX **Finish** loads the main application view; no plugin execution.

The `setup` package is in `opendata-core`; no plugin depends on it. The API
JPMS module exports contracts only, and the common module remains an automatic
module. The core JPMS module declares JavaFX requirements and opens the GUI
package to `javafx.fxml` for controller injection.

## Failure semantics

Database DDL is idempotent where possible, but creation of server principals,
core tables and individual plugin schemas is not a single transaction. On any
failure, the writable flag remains `init=true` and the user can retry after
examining the SQL state. Administrator credentials are not persisted.

## Compatibility and planned hardening

`BootstrapConfigurationLoader` remains for legacy Phase-1 resource clients;
**new first-run code must use** `ApplicationBootstrapPropertiesLoader`.
The old AES key file JVM/environment locations are deprecated and should be
replaced by the file setting `security.aes-key-file`.

The live SQL Server and JavaFX integration need verification on the supported
Windows/JDK 24+ environment; local JDK 21 compilation cannot validate the
full version 4 runtime reactor.
