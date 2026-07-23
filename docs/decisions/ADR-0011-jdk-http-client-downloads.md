# ADR-0011: Use JDK HTTP and streamed downloads

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Java 17 includes HTTP and large files must not be buffered fully.

## Decision

Use HttpClient, validate status, stream to .part, move on success and restore interrupt status.

## Consequences

No extra HTTP client dependency; retries/authentication require explicit policies.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
