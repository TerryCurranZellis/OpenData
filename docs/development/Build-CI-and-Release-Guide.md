# Build, CI and Release Guide

## Purpose

OpenData uses Maven and GitHub Actions to apply repeatable build, test, quality, documentation and release checks.

![Build, documentation and release pipeline](../diagrams/generated/ci-release-pipeline.svg)

## Local verification

Run the same principal checks used by continuous integration:

```powershell
mvn clean verify
./scripts/Validate-Documentation.ps1 -FailOnWarning
```

For strict static-analysis enforcement:

```powershell
mvn clean verify -Dquality.failOnViolation=true
```

## Continuous integration

`.github/workflows/build.yml` compiles the project, runs tests, executes the configured quality tools and publishes reports. `.github/workflows/documentation.yml` validates documentation references and builds HTML manuals.

## Releases

A release tag must use the form `vMAJOR.MINOR.PATCH`, and the numeric part must match the Maven version in `pom.xml`. The release workflow performs strict verification, creates the application JAR, produces a clean source archive, generates SHA-256 checksums and publishes the files to GitHub Releases.

A local package can be prepared with:

```powershell
./scripts/New-ReleasePackage.ps1 -Version 1.0.0
```

Before tagging, update `CHANGELOG.md`, confirm the version in `pom.xml`, run strict verification, and ensure the working tree is clean.
