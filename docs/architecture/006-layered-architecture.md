# Layered Architecture

**Document ID:** ARCH-006  
**Version:** 2.0  
**Status:** Baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

## Layers

1. **Entry:** `OpenData` and the CLI translate process input and operator output.
2. **Application:** coordinates control commands, configuration sources,
   resources, selection and execution.
3. **Plugin/domain:** represents provider-specific datasets and rules.
4. **Processing:** extract, parse, validate, transform, load and finalise.
5. **Infrastructure:** HTTP, file system, encryption, logging and JDBC.

Upper layers call lower layers through constructors, records and interfaces.
Lower layers must not inspect CLI options or call the `OpenData` entry point.

Mutable third-party objects such as `Workbook`, `CSVParser`, `Connection` and
`ResultSet` remain inside short resource scopes. Immutable records cross package
boundaries.

`List<Map<String,String>>` remains a tolerated shared parser result. A universal
`DataRecord`/`DataTable` abstraction is future work and is not required by the
current provider-specific typed pipelines.
