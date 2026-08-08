# Operations Runbook

**Document ID:** OPS-RUNBOOK-001  
**Version:** 2.2  
**Status:** Version 2.0.0 pre-production baseline  
**Baseline date:** 8 August 2026

---

![OpenData operational lifecycle](../diagrams/generated/operational-lifecycle.svg)

## Preflight

1. record application/schema/documentation versions;
2. verify launcher working directory;
3. apply SQL scripts including registry migration/grants;
4. replace/protect bootstrap credentials and certificate material;
5. verify log/input/work/archive permissions and capacity;
6. back up SQL Server and protected configuration.

## Register, inspect and administer

```text
opendata --plugin all --register
opendata --list-plugins
opendata --plugin octopus --detail
opendata --plugin octopus --disable
opendata --plugin octopus --enable
```

External definitions are accepted only as
`--plugin <id> --register --file <complete-file>`.

Use `--plugin <id> --detail` to confirm the configuration stored in SQL Server
for one registered plugin. The command requires exactly one named plugin and
does not use `--Execute`.

Administration operations and `--detail` do not use `--Execute`.

## Safe acceptance

1. register the required plugins;
2. use `--detail` for each plugin to verify its stored configuration;
3. dry-run each enabled plugin and then
   `opendata --plugin all --Execute --dry-run`;
4. perform one controlled write per plugin with `--Execute`;
5. verify audit/provider rows, idempotent replay and archive behaviour;
6. test repeated plugin selection and bounded parallelism;
7. test lifecycle operations in a disposable environment.

Examples:

```text
opendata --plugin ofgem --detail
opendata --plugin ofgem --Execute --dry-run
opendata --plugin ofgem --Execute
opendata --plugin openmeteo --plugin ofgem --Execute --parallelism 2
```

The explicit execution switch is a safety gate: `--plugin ofgem` by itself is
rejected and cannot start a data load. `--detail` is an inspection command and
does not execute the plugin.

## Stop conditions

Stop writes after transaction/permission failure, unexpected source-layout
change, unexplained hash/row-count change, persistent pool exhaustion, secret or
statement leakage, archive warning, or stale audit state.

The Java process does not currently propagate `ExecutionStatus.statusCode()` to
the operating system. Inspect final status and plugin summaries.

---
