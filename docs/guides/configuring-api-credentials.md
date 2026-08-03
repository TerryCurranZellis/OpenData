# Configuring API Credentials

**Document ID:** GUIDE-CREDENTIAL-001  
**Version:** 2.0  
**Status:** Credential-reference model implemented; runtime secret provider absent  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

Plugin properties can describe a credential reference with:

- authentication type;
- provider identifier;
- provider-specific secret reference;
- request location; and
- header/query/cookie/body parameter name.

The model supports API key, basic, bearer token, OAuth2 client credentials, form
login and cookie concepts. It does **not** resolve a secret or apply it to an
HTTP request in the current runtime.

A credential block is metadata, not a secure secret store. Never put the actual
secret in `credential.*`, endpoint headers, query parameters or documentation.

## Required implementation before use

A credential-dependent production plugin requires a reviewed provider boundary
that:

1. loads the secret from outside Git;
2. returns it only to the requesting execution;
3. applies it immediately before the HTTPS request;
4. redacts logs, exceptions and diagnostics;
5. clears or limits in-memory copies where practical;
6. has expiry/rotation failure handling; and
7. includes tests proving the value is absent from committed and generated
   files.

Until that exists, shipped production endpoints must remain public or use
provider-specific code whose secure deployment is explicitly designed and
reviewed.
