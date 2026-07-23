# ADR-0024: Store credential references, not secrets

**Status:** Accepted  
**Date:** 23 July 2026

## Context

API keys/tokens must not enter Git or snapshots.

## Decision

Store CredentialReference metadata and resolve via a provider just before requests.

## Consequences

At least one protected provider is needed for authenticated plugins.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
