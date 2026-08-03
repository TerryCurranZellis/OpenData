# Security Standard

**Document ID:** STD-SECURITY-001  
**Version:** 2.0  
**Status:** Version 2.0.0 target standard with known release blockers  
**Baseline date:** 3 August 2026

---

## Secrets and key material

- Passwords, tokens, private keys and customer data MUST NOT be committed.
- Logs, exceptions, diagnostics and generated manuals MUST NOT expose secrets.
- Bootstrap files and external registration files containing secrets MUST be
  restricted to the runtime identity.
- Classpath defaults MUST contain blank secrets or non-secret references.
- A public certificate MAY be distributed; the private key MUST be provisioned
  separately and protected.

The uploaded Version 2.0.0 baseline contains a plaintext bootstrap credential and
a private PFX under the source tree. This violates the target standard and is a
release blocker requiring removal from history, replacement and credential/key
rotation.

## Configuration encryption

RSA encryption protects the stored database password from casual disclosure; it
does not make a source-controlled private key safe. The private key, certificate
password and encrypted value require separate access controls.

The implemented external PFX-password mechanism is the JVM system property used
by the code. The documented environment-variable name is not dependable in the
current implementation and must not be presented as a supported control until
the Java constant is corrected and tested.

## Database

- Use a least-privilege application principal.
- Production TLS MUST validate a certificate trusted by the JVM.
- Use parameterised SQL for values and allow-list validated identifiers.
- Bound transactions and roll back on failure.
- Never retain a pooled connection on a plugin object.
- Registration and bootstrap-file updates should be treated as a coordinated
  administrative change; the current implementation is not atomic across the
  database and file rewrite.

## Network and files

Use HTTPS, explicit timeouts and bounded downloads. Treat publisher files,
workbooks, JSON, CSV and PDFs as untrusted input. Working and archive directories
must prevent untrusted replacement of files consumed by OpenData. Archive
movement after database commit must be monitored because a filesystem failure
cannot roll back committed rows.

## Credential-reference model

Plugin definitions can describe API-key, basic, bearer, OAuth, form and cookie
credential references, but no runtime secret-provider implementation resolves
them. Do not create a credential-dependent production plugin until that boundary
is implemented, reviewed and tested for redaction.

## Dependency and release controls

Review dependency advisories and licences. Preview dependencies require explicit
acceptance. Release artefacts and retained evidence must exclude secrets,
private keys, customer statements and database backups.
