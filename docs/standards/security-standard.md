# Security Standard

**Document ID:** STD-SECURITY-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

## Secrets

- Passwords, tokens and private keys MUST NOT be committed.
- Logs, exception messages and generated documentation MUST NOT expose secrets.
- Override files containing secrets MUST have access restricted to the runtime
  identity.
- Classpath defaults MUST contain a blank password or a non-secret reference.

The current classpath password violates the target rule and is tracked as a
critical gap.

## Database

- The application MUST use a least-privilege database principal.
- Production TLS MUST validate a certificate trusted by the JVM.
- Plugins MUST use parameterised SQL for data values.
- Configurable SQL identifiers MUST be allow-list validated.
- Transactions MUST be bounded and rolled back on failure.

## Network and files

Use HTTPS, explicit timeouts and bounded downloads. Treat publisher files as
untrusted input. Working/archive directories must not allow untrusted users to
replace files consumed by the application.

## Dependency and release controls

Review dependency advisories and licences. Preview dependencies require explicit
acceptance. Release evidence must not contain secrets or production source data
unless access is controlled.
