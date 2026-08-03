# Batch 4 Implementation Notes

> **Historical implementation note:** The later command-line and persistent plugin-registry update supersedes statements here about standalone registration, invocation override files, or Octopus/`all` dry-run limitations. Current CLI and release documents take precedence.

**Completed:** 3 August 2026  
**Scope:** Complete user and administrator operations documentation

## Updated

- Full Version 2.0.0 user guide, including installation, registration,
  configuration, CLI, plugins, parallelism, dry run, troubleshooting and
  maintenance.
- Administrator runbook, logging/pool operations, monitoring, backup and
  recovery.
- SQL Server bootstrap, database security and database troubleshooting guides.
- Command-line, configuration, database-configuration and audit references.
- Technical User Guide and Administrator Guide manifests and documentation
  indexes.

## Added

- Dedicated Octopus Energy statement user chapter.
- Operational lifecycle PlantUML source and rendered SVG.

## Important implementation variances recorded

- The Maven JAR is not self-contained and has no `Main-Class` manifest entry.
- Bootstrap and certificate paths are coupled to the repository source tree and
  `user.dir`.
- Registration is not atomic across SQL Server updates and the bootstrap-file
  rewrite.
- `ExecutionStatus` numeric codes are not propagated to the operating system.
- Octopus dry run and therefore `all --dry-run` are not usable.
- Octopus archive movement happens after database commit and can fail without
  rolling back persisted records.
- `working.directory` is currently unused and blank Octopus archive paths resolve
  to the process working directory.
- The keystore environment-variable constant is incorrect; the JVM system
  property is the only dependable external PFX-password mechanism.
- Tracked bootstrap credentials/private-key material remain release blockers.

## Exclusions

No Java, SQL, PowerShell, workflow or build files were changed.
