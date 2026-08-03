# Operations Runbook

**Document ID:** OPS-RUNBOOK-001  
**Version:** 2.0  
**Status:** Version 2.0.0 pre-production baseline  
**Baseline date:** 3 August 2026

---

![OpenData operational lifecycle](../diagrams/generated/operational-lifecycle.svg)

## Preflight

1. record application/schema/documentation versions;
2. verify launcher working directory;
3. apply SQL scripts including registry migration/grants;
4. replace/protect bootstrap credentials and certificate material;
5. verify log/input/work/archive permissions and capacity;
6. back up SQL Server and protected configuration.

## Register and administer

```text
opendata --plugin all --register
opendata --list-plugins
opendata --plugin octopus --disable
opendata --plugin octopus --enable
```

External definitions are accepted only as
`--plugin <id> --register --file <complete-file>`.

## Safe acceptance

1. dry-run each enabled plugin and then `--plugin all --dry-run`;
2. perform one controlled write per plugin;
3. verify audit/provider rows, idempotent replay and archive behaviour;
4. test repeated plugin selection and bounded parallelism;
5. test lifecycle operations in a disposable environment.

## Stop conditions

Stop writes after transaction/permission failure, unexpected source-layout
change, unexplained hash/row-count change, persistent pool exhaustion, secret or
statement leakage, archive warning, or stale audit state.

The Java process does not currently propagate `ExecutionStatus.statusCode()` to
the operating system. Inspect final status and plugin summaries.
