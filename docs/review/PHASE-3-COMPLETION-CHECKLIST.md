# Phase 3 Completion Checklist

**Document ID:** REVIEW-PHASE3-001  
**Version:** 1.1  
**Status:** Historical phase record  
**Baseline date:** 26 July 2026

This checklist records the Phase 3 hand-off. Items subsequently implemented are
marked below; current unresolved work is tracked in the
[26 July gap analysis](DOCUMENTATION-GAP-ANALYSIS-2026-07-26.md).

## Delivered

- [x] managed SQL Server connection pool;
- [x] external database credentials;
- [x] pool configuration validation and snapshot;
- [x] SQL Server database/login/user/role scripts;
- [x] shared `core` audit schema;
- [x] normalised `ofgem` price-cap schema;
- [x] reference data seeds;
- [x] typed Ofgem workbook extraction;
- [x] transactional period fact replacement;
- [x] Java 17 static compilation checks;
- [x] architecture documentation and diagrams;
- [x] ADR register and Phase 3 ADRs;
- [x] operator/reference/plugin documentation.

## Required locally before acceptance

- [x] apply Phase 2 and Phase 3 overlays to the current branch;
- [x] implement the Ofgem plugin orchestration from discovery through audit completion;
- [ ] resolve any merge conflicts with local uncommitted code;
- [ ] run `mvn clean test`;
- [ ] install the database on a clean SQL Server instance;
- [ ] rerun all installation scripts to verify idempotence;
- [ ] test login `OpenData` and application role permissions;
- [ ] run database health check;
- [ ] import a representative Annex 9 workbook;
- [ ] verify expected row count and representative cell lineage;
- [ ] induce a persistence failure and verify rollback;
- [x] verify final run status and duration are covered by unit tests and logging;
- [x] verify pool closure paths are covered by unit tests;
- [ ] render PlantUML diagrams or validate their syntax;
- [ ] commit code, SQL and documentation together.

## Known scope limits

- detailed Ofgem component extraction is not implemented;
- a live SQL Server integration test was not available during generation;
- scheduling remains external/deferred;
- protected production secret-provider integration remains future hardening;
- production certificate configuration is environment-specific.

The current runtime additionally has two audit models:
`core.PluginRun` for the coordinator and `core.ingestion_run` for Ofgem source
provenance. Their unification is post-Phase-3 work.
