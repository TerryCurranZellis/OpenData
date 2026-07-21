# Contributing to the OpenData Framework

Thank you for your interest in contributing to the OpenData Framework.

Whether you are fixing a bug, improving the documentation, adding a new dataset plugin or implementing a new feature, your contribution is appreciated.

This document describes the development standards and processes used throughout the project. Following these guidelines helps keep the framework consistent, maintainable and easy to extend.

---

# Project Philosophy

The OpenData Framework is designed as an enterprise-grade Java framework for downloading, validating, transforming and importing Open Data into relational databases.

The project is built around the following principles:

- Documentation First
- Architecture Before Implementation
- Simplicity Over Cleverness
- High Code Quality
- Modular Design
- Plugin-Based Architecture
- Testable Components
- Long-Term Maintainability

Every contribution should support these principles.

---

# Development Workflow

The recommended workflow is:

1. Fork the repository.
2. Create a feature branch.
3. Make your changes.
4. Update any affected documentation.
5. Add or update unit tests.
6. Verify the project builds successfully.
7. Submit a Pull Request.

No changes should be committed directly to the main branch.

---

# Development Environment

The recommended development environment is:

| Component | Version |
|-----------|---------|
| Java | 17 (LTS) |
| Maven | Latest Stable |
| IDE | Apache NetBeans (recommended) |
| Database | Microsoft SQL Server |
| Git | Latest Stable |
| PowerShell | 5.2 or later |

The project should compile on any operating system supporting Java 17.

---

# Repository Structure

The repository is organised into the following major areas.

```
docs/
examples/
scripts/
sql/
src/
tools/
```

Documentation should always be updated alongside code changes.

---

# Coding Standards

## General Principles

Code should be:

- Easy to read
- Easy to maintain
- Well documented
- Self-explanatory
- Consistent

Avoid unnecessary complexity.

---

## Java Version

Java 17 LTS is the minimum supported version.

Do not introduce language features requiring newer Java versions without discussion.

---

## Formatting

Use four spaces for indentation.

Do not use tabs.

Use UTF-8 encoding.

Maximum line length should normally be 120 characters.

Opening braces should appear on the following line for classes and methods where this matches the established project style.

---

## Class Design

Each source file should contain one public class.

Classes should have a single responsibility.

Prefer composition over inheritance.

Keep classes focused.

Large classes should be refactored.

---

## Methods

Methods should:

- Perform one logical task.
- Have meaningful names.
- Be kept reasonably small.
- Avoid excessive nesting.
- Avoid duplicated logic.

If a method becomes difficult to understand, consider extracting helper methods.

---

## Interfaces

Program against interfaces rather than implementations wherever practical.

Examples include:

- DataDownloader
- DataParser
- Validator
- DatabaseRepository
- PipelineStep

---

## Dependency Injection

Constructor injection should be used throughout the project.

Avoid creating dependencies using "new" inside business logic.

Dependencies should be supplied by the application context.

---

## Immutability

Immutable objects are preferred wherever practical.

Configuration objects and domain models should normally be immutable.

---

## Exceptions

Never silently ignore exceptions.

Catch exceptions only when they can be handled appropriately.

Wrap low-level exceptions in framework-specific exceptions where appropriate.

---

## Logging

Do not use System.out.println().

Use the framework logging infrastructure.

Log messages should:

- Be meaningful
- Include sufficient context
- Avoid sensitive information

---

## Comments

Write comments explaining *why* something exists rather than *what* the code is doing.

Poor code should not be hidden behind comments.

Improve the code instead.

---

# JavaDoc Standards

All public:

- classes
- interfaces
- enums
- records
- public methods

must include JavaDoc.

JavaDoc should describe:

- purpose
- parameters
- return values
- exceptions
- usage where appropriate

---

# Package Structure

Packages should follow the documented architecture.

Do not create new top-level packages without discussion.

Related classes should remain together.

Avoid circular dependencies between packages.

---

# Documentation Standards

Documentation is treated as a first-class deliverable.

Any significant code change should include updates to the relevant documentation.

Documentation should be written in Markdown.

Architecture diagrams should be maintained using PlantUML.

Do not allow documentation to become out of date.

---

# Architecture Decision Records

Major architectural decisions should be recorded using an Architecture Decision Record (ADR).

Examples include:

- Introducing a new framework
- Changing the plugin architecture
- Database changes
- Build process changes

---

# Testing

All new functionality should include unit tests where practical.

Bug fixes should include regression tests whenever possible.

The project should always build successfully before a Pull Request is submitted.

---

# Git Commit Messages

Commit messages should be concise and descriptive.

Examples:

```
Add CSV parser implementation

Refactor validation pipeline

Improve SQL Server repository

Update architecture documentation
```

Avoid messages such as:

```
Fixed stuff

Changes

Update

Misc
```

---

# Branch Naming

Suggested branch names:

```
feature/csv-parser

feature/pipeline-engine

bugfix/sql-connection

documentation/architecture

refactor/database-layer
```

---

# Pull Requests

Each Pull Request should:

- Describe the change.
- Explain the motivation.
- Reference any related issue.
- Include documentation updates.
- Include tests where applicable.

Large Pull Requests should be broken into smaller logical changes whenever possible.

---

# Code Review Checklist

Reviewers should verify:

- Architecture follows project principles.
- Coding standards are met.
- Documentation is updated.
- JavaDoc is complete.
- Tests are included.
- Logging is appropriate.
- No unnecessary dependencies are introduced.

---

# Dependencies

New dependencies should be introduced only when they provide significant value.

Before adding a dependency consider:

- Is it actively maintained?
- Is it well documented?
- Does Java already provide equivalent functionality?
- Does it introduce licensing concerns?

---

# Security

Never commit:

- passwords
- API keys
- connection strings containing credentials
- certificates
- private keys

Configuration should be externalised.

---

# Performance

Performance should be considered throughout development.

Avoid:

- unnecessary object creation
- duplicated processing
- excessive memory allocation

Optimise only where measurement demonstrates a need.

---

# Documentation-First Development

This project follows a documentation-first approach.

The preferred sequence is:

1. Requirements
2. Architecture
3. Design
4. Documentation
5. Implementation
6. Testing
7. Review

Documentation should lead implementation, not follow it.

---

# Asking Questions

If you are unsure about a design decision, please open a Discussion or Issue before beginning significant work.

Early discussion often avoids unnecessary implementation effort.

---

# Thank You

Every contribution, whether it is code, documentation, testing or feedback, helps improve the OpenData Framework.

Thank you for contributing.