# ADR-0061: Keep tests with the module that owns the production code

**Version:** 3.1.0  
**Status:** Accepted for Version 3.1.0 implementation  
**Date:** 2026-09-18  
**Decision owners:** OpenData maintainers

---

## Context

The module split moved shared JDBC, validation, and support classes out of
`opendata-core`, but some of their tests still remained in the core test tree.
That layout weakens module ownership and allows a sibling module to pass without
running the tests for code it actually compiles.

## Decision

Place unit tests under the Maven module that owns the production classes they
verify.

For the current split:

- shared JDBC helper tests belong in `opendata-common`;
- shared validation tests belong in `opendata-common`; and
- reusable support-strategy tests belong in `opendata-common`.

## Consequences

### Positive

- module verification exercises the code built by that module;
- future refactors can move code and tests together; and
- build failures identify the correct owner module more clearly.

### Negative or limiting

- cross-module helper usage still requires integration coverage in `opendata-core`;
- moving production code later may require matching test moves; and
- historical references to old test locations become stale.

## Rejected alternatives

### Keep shared-code tests in `opendata-core`

Rejected because it hides ownership and prevents split-out modules from proving
that their own code passes its direct unit tests.
