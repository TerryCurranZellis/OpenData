# Configure Database Access Securely

**Document ID:** GUIDE-DB-SECURITY-001  
**Version:** 2.0  
**Status:** Version 2.0.0 procedure with known implementation limitation  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

1. Create a replacement X.509 public certificate and matching PKCS#12 private
   key store.
2. Place them at the paths currently required beneath
   `src/main/resources/config/security` for the controlled installation.
3. Create a protected bootstrap override file with
   `application.database.url`, `application.database.user` and
   `application.database.password`.
4. Run `opendata --register --file <bootstrap.properties>`.
5. Confirm the configuration tables and rewritten bootstrap file contain the
   expected encrypted password and database-backed switch.
6. Remove the plaintext override file when operational policy permits, or retain
   it only in an approved secret store.
7. Restrict the private key and bootstrap file to the application identity.

## Local development URL

```properties
application.database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
application.database.user=OpenData
application.database.password=<local-password>
```

## Production URL principle

Use `encrypt=true`, a SQL Server certificate trusted by the JVM and
`trustServerCertificate=false`. Do not embed credentials in the URL.

## PKCS#12 password

The supported mechanism is the JVM property:

```text
-Dopendata.config.keystore.password=<password>
```

The intended environment-variable mechanism is defective in this baseline
because the code reads an environment variable literally named `nopassword`.
Do not create or depend on such an environment variable; correct the code in a
future source release.

## Release blockers

The uploaded baseline includes a plaintext bootstrap credential and private PFX
under source control. Remove both, rotate the database password, replace the
certificate pair and purge sensitive history as appropriate before publication
or production use.
