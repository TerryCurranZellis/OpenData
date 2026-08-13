# Configure Database Access Securely

**Document ID:** GUIDE-DB-SECURITY-001  
**Version:** 2.0  
**Status:** Version 2.0.0 procedure with known hardening gaps  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

1. Create a replacement X.509 certificate and matching protected PKCS#12 private key outside public source control.
2. Install all SQL scripts, including `003a-create-plugin-registry.sql` and grants.
3. Set the bootstrap JDBC URL/user and temporary plain password with database-properties mode `false`.
4. Supply the PFX password with `-Dopendata.config.keystore.password=<password>` when required.
5. Run `opendata --plugin all --register`.
6. Verify the bootstrap password is `{enc}...`, database mode is `true`, and `--list-plugins` succeeds.
7. Remove/rotate development secrets and verify release archives contain no private key.

`--file` does not supply bootstrap credentials. It is reserved for one complete
named plugin registration:

```text
opendata --plugin example --register --file C:\OpenData\example.properties
```

The environment-variable keystore password route is not reliable in the current
baseline. Production also requires SQL Server certificate validation rather than
`trustServerCertificate=true`.
