# Configuring API Credentials

**Document ID:** GUIDE-CREDENTIAL-001  
**Version:** 1.1  
**Status:** Model defined; runtime provider not implemented  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


The plugin-definition model can describe a credential reference, authentication
type and request location. The current runtime does not resolve those references
from a secret provider, and both shipped endpoints are public.

Do not add a credential-dependent production endpoint until the provider
boundary is implemented and reviewed. That implementation must store the secret
outside Git, inject only the resolved value at request time, require HTTPS,
redact logs and generated diagnostics, and include tests proving the secret
does not enter committed or generated files.
