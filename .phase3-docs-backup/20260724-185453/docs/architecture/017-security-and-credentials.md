# Security and Credentials

**Document ID:** ARCH-017  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


Plugin definitions store `CredentialReference`, not resolved values. The first
provider may be a protected external properties file; later providers can use
Windows Credential Manager or a vault.

Authentication metadata can describe API key, Basic, bearer, OAuth2, form login
or cookie placement, but only required types should be implemented.

Credentials are resolved immediately before request construction, never placed
in `ApplicationConfig`, logs, Git or execution snapshots. HTTPS and certificate
verification remain enabled.

A future plugin-definition database also stores references only.
