# Layered Architecture

**Document ID:** ARCH-006  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Layers

1. **Entry:** `Main` and CLI translate process input and output.
2. **Application:** coordinates use cases and object composition.
3. **Plugin/domain:** represents datasets and source-specific rules.
4. **Processing:** download, parse, validate, transform and load.
5. **Infrastructure:** HTTP, file system, credentials and JDBC.

Upper layers call lower layers through constructors and interfaces. Lower layers
must not inspect CLI options or call `Main`.

Records cross boundaries; mutable third-party objects such as `Workbook`,
`CSVParser`, `Connection` and `ResultSet` remain inside short resource scopes.

`List<Map<String,String>>` is a tolerated Phase 1 parser result; a typed
`DataRecord`/`DataTable` model is future work.
