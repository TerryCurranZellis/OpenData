# ADR-0009: Use a common staged ETL pipeline

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

Every dataset requires acquisition and persistence, but the exact source format and validation rules differ. A consistent execution lifecycle is needed for logging, error handling, testing, run reporting, and future scheduling.

## Decision

Model dataset processing as a common ordered pipeline:

1. Discover
2. Download
3. Validate
4. Parse
5. Transform
6. Load
7. Verify

The framework controls the lifecycle and shared concerns. Plugins supply dataset-specific stage implementations or hooks.

## Consequences

### Positive

- Provides a predictable lifecycle for every plugin.
- Allows shared logging, metrics, run metadata, retries, and failure reporting.
- Makes individual stages independently testable.
- Supports future orchestration without rewriting plugin logic.

### Negative or limiting

- Some datasets may not require every stage.
- Intermediate models and stage boundaries add design overhead.
- Stage contracts must avoid leaking source-specific assumptions into the core.

## Alternatives considered

### A single plugin `execute` method

Rejected as the long-term model because it hides lifecycle stages and limits common instrumentation.

### A workflow engine dependency

Not selected for the initial version because it would add unnecessary operational and configuration complexity.

## Implementation notes

Stages may initially be coordinated by a simple in-process pipeline service. Optional stages may return an explicit skipped result. A failed mandatory stage stops subsequent processing and records the failure.
