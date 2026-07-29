# Java Quality Guide

## Purpose

OpenData uses Maven-based quality checks to detect likely defects, inconsistent source formatting and maintainability problems before release.

## Quality tools

The `verify` lifecycle runs:

- **Checkstyle** for source-layout and basic coding rules.
- **SpotBugs** for bytecode-level defect detection.
- **PMD** for likely errors and maintainability findings.
- **Javadoc** generation through the Maven Javadoc plugin.

The initial Batch 5 configuration reports findings without failing the build. This allows the existing codebase to establish a measured baseline before enforcement is enabled.

## Commands

Run the standard verification:

```powershell
mvn clean verify
```

Run the supplied PowerShell wrapper:

```powershell
./scripts/Invoke-Code-Quality.ps1
```

Make all configured violations fail the build:

```powershell
./scripts/Invoke-Code-Quality.ps1 -Strict
```

Equivalent Maven command:

```powershell
mvn clean verify -Dquality.failOnViolation=true
```

Generate API documentation:

```powershell
mvn javadoc:javadoc
```

The generated API documentation is written beneath `target/reports/apidocs` or the Maven plugin's configured report directory.

## Configuration

Quality rules are stored under `config/quality/`:

- `checkstyle.xml`
- `pmd-ruleset.xml`

Rules should be changed deliberately and reviewed like application source. Avoid suppressing a warning globally when a narrow source-level correction or documented suppression is possible.

## Package documentation

Every production package containing Java classes should have a `package-info.java` file describing its responsibility. Public APIs should include useful Javadoc describing behaviour, parameters, return values and exceptional conditions rather than merely repeating method names.

## Enforcement roadmap

1. Run the reporting baseline on every change.
2. Correct high-confidence SpotBugs and PMD findings.
3. Reduce Checkstyle warnings.
4. Enable strict mode in CI once the baseline is acceptably clean.
5. Prevent new violations from being introduced.
