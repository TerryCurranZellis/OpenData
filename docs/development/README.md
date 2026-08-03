# Developer Documentation

**Document ID:** DEV-INDEX-001  
**Version:** 2.0  
**Status:** Version 2.0.0 baseline  
**Baseline date:** 2 August 2026  
**Minimum Java version:** 17

---

- [Repository structure](repository-structure.md)
- [Local build, test and run](local-build-test-run.md)
- [Build, CI and release guide](Build-CI-and-Release-Guide.md)
- [Dependency management](dependency-management.md)
- [Release and versioning](release-and-versioning.md)
- [Adding a plugin](../guides/adding-a-plugin.md)
- [Version 1.x to 2.0.0 migration](../migration/version-1-to-version-2.md)
- [Project standards](../standards/README.md)

The repository is a single Maven modular monolith. Source, SQL, tests,
documentation, data-source notices and ADRs for one change should be reviewed
together. Version 2.0.0 plugins follow the
`initialise -> extract -> transform -> load -> finalise` lifecycle and use the
shared plugin exception boundary.
