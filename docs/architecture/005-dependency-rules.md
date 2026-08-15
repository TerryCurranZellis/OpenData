# Dependency Rules

**Document ID:** ARCH-005  
**Version:** 3.0.0  
**Status:** Baseline  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Rules

- `OpenData` and `OpenDataApplication` must not depend directly on Ofgem,
  OpenMeteo or Octopus implementation classes.
- The generic `plugin` package may depend on contracts and infrastructure, but
  must not import `plugin.<id>` classes.
- A provider package may use shared configuration, download, parser, database,
  logging and exception packages.
- Provider-to-provider dependencies are prohibited.
- Plugin SQL and transaction logic belongs in `plugin.<id>.load` repositories.
- CLI classes do not open database connections or execute plugins.
- Infrastructure classes do not inspect command-line options.
- Configuration records and provider models should be immutable.
- Borrowed JDBC connections, streams, parsers and workbooks must remain inside
  bounded resource scopes.
- Dry-run branches must not call plugin persistence, audit or final archive
  operations.
- Secrets must not be logged or copied into plugin definitions.

The classpath registry may name provider implementation classes as data in
`index.properties` and plugin properties. That is registration metadata, not a
compile-time dependency from the application layer.
