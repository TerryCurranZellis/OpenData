# Dependency Rules

**Document ID:** 005
**Version:** 1.0
**Status:** Draft

## Purpose

Documents permitted package dependencies.

## Scope

This document forms part of the OpenData Framework Software Architecture Manual and should be read alongside the related architecture documents.

## Overview

The OpenData Framework is an enterprise-grade, plugin-based Java 17 framework for acquiring, validating, transforming and loading Open Data into relational databases. This document describes the architectural aspects related to **Dependency Rules**.

## Design Principles

- Documentation-first development
- Interface-driven design
- Low coupling / high cohesion
- Constructor injection
- Immutable models where practical
- Java 17
- Maven build
- SQL Server initial target
- Plugin extensibility

## Responsibilities

- Define architectural responsibilities.
- Describe design constraints.
- Identify extension points.
- Provide implementation guidance.

## Key Concepts

| Topic | Description |
|-------|-------------|
| Architecture | Enterprise layered design |
| Documentation | Markdown source, Pandoc output |
| UML | PlantUML source diagrams |
| Testing | Unit testing and integration testing |

## Related Documents

- 001-project-vision.md
- 003-high-level-architecture.md
- 004-package-structure.md

## Future Enhancements

This document will be expanded as implementation progresses with UML diagrams, examples and detailed design decisions.

## Revision History

| Version | Date | Description |
|---------|------|-------------|
|1.0|2026-07-22|Initial draft|
