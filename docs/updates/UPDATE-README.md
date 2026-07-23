# README Update Instructions

Update the project requirements to state:

```text
Java 17 or later is required. The Maven build targets Java 17 bytecode and APIs.
```

Update the current-status section to state:

- Phase 1 plugins are indexed by `config/plugins/index.properties`.
- `--list-plugins` is registry-driven.
- Ofgem and OpenMeteo are installed.
- CSV parsing uses Apache Commons CSV.
- HTML link discovery uses JSoup.
- XLS/XLSX parsing uses Apache POI.
