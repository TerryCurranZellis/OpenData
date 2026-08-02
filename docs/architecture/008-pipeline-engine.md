# Pipeline Engine

**Document ID:** ARCH-008  
**Version:** 1.2  
**Status:** Implemented coordinator; partial generic pipeline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


## Standard stages

The application-level sequence is implemented:

1. parse arguments and control commands;
2. load application overrides and runtime configuration;
3. resolve enabled plugins and plugin definitions;
4. initialise logging and, for a write run, pooled database access;
5. submit one task per plugin to a bounded executor;
6. create audit rows, execute plugins and aggregate metrics/status;
7. close the executor, database resource and logging system.

Inside a task, the root provider facade owns only workflow ordering. Concrete
work is separated into plugin-local `download`, `extract`, `transform`,
`transform.validate` and `load` packages. Ofgem and OpenMeteo implement this
same structural pipeline end to end, while Octopus already follows it with
placeholder extract/load/finalise implementations.

`ExtractService`, `TransformService` and `LoadService` remain reusable stage
contracts. They are not composed by a generic `PipelineEngine` in the current
runtime.

A plugin failure stops that task, not other selected plugins. Database writes are
transactional. The aggregate result is unsuccessful when any plugin fails or is
cancelled.

::: {.landscape}
![Plugin pipeline sequence](../diagrams/generated/pipeline-sequence.svg){width=22.5cm}
:::
