# ADR-0053: Keep JavaFX controllers behind asynchronous application-service boundaries

**Status:** Accepted  
**Date:** 13 August 2026

## Context

The Version 3 GUI must display persistent plugin registry information and, in
later batches, perform plugin administration and execution. Those operations
can involve SQL Server, configuration decryption, file I/O, network I/O and
long-running plugin work.

Putting those operations directly into JavaFX controllers would couple FXML
presentation code to persistence details and could block the JavaFX application
thread. The existing OpenData architecture already has registry, configuration,
database and execution components that should remain authoritative.

## Decision

JavaFX controllers remain presentation coordinators. They must not contain SQL
or directly own OpenData processing logic.

GUI backend work is exposed through focused application-service or adapter
classes. Data returned to the controller should be plain immutable Java data
where practical; JavaFX properties are created only in the presentation layer.

Potentially blocking operations must execute away from the JavaFX application
thread. JavaFX `Task` is the default boundary for work initiated by a
controller. Completion and failure handlers update controls on the JavaFX
application thread.

Batch 3 applies this decision as follows:

```text
OpenDataMainController
        |
        | JavaFX Task
        v
PluginTableDataLoader
        |
        +--> JdbcPluginRegistry
        |
        +--> PluginTableDataService --> core.PluginRun
        |
        v
PluginTableEntry (plain immutable data)
        |
        v
PluginRow (JavaFX presentation properties)
```

`PluginTableDataService` reads the persistent registry through the existing
`PluginRegistry` contract and performs one read-only query for the latest run
audit of each plugin. The FXML controller does not know JDBC details.

The same direction applies to later GUI administration, detail and execution
features: controller -> GUI application service/adapter -> existing OpenData
services.

## Consequences

- SQL Server access does not occur on the JavaFX application thread.
- The GUI remains responsive while plugin metadata is loaded.
- Existing CLI registry behaviour and contracts remain unchanged.
- GUI view models do not become database entities.
- Backend read logic can be tested without starting the JavaFX toolkit.
- Later state-changing GUI operations should refresh the table through the same
  controller refresh boundary after they complete successfully.
- Service extraction may expose reusable core administration operations in
  later batches, but this ADR does not require duplicating CLI command parsing.

## Related documents

- [ADR-0051: JavaFX graphical interface](ADR-0051-javafx-graphical-interface.md)
- [ADR-0052: Java 24, JavaFX lifecycle and Swing retirement](ADR-0052-java-24-javafx-lifecycle-and-swing-retirement.md)
- [JavaFX GUI architecture](../development/javafx-gui-architecture.md)
- [Graphical interface user guide](../user-guide/12-graphical-interface.md)
- [GUI specification](../specifcations/OpenData%20Specifcation%20v3.md)
