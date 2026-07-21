# ADR-0007: Begin as a modular monolith

- Status: Accepted
- Date: 2026-07-21

## Context

The application is a command-line batch tool with one operator, one selected plugin per run, shared infrastructure, and no demonstrated need for distributed services.

## Decision

Build one deployable Java process with strong internal package and module boundaries.

## Consequences

- Build, deployment, and debugging remain simple.
- Calls between modules are ordinary Java calls.
- Architecture rules are required to prevent boundary erosion.
- Independent plugin packaging may be added later if justified.
