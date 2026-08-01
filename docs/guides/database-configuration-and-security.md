# Configure Database Access Securely

**Document ID:** GUIDE-DB-SECURITY-001  
**Version:** 2.0  
**Status:** Updated  
**Baseline date:** 01 August 2026  
**Minimum Java version:** 17

---

1. Run `. .\\scripts\\New-ConfigurationCertificate.ps1` and then
   `New-ConfigurationCertificate` to create
   `src/main/resources/config/security/opendata-config-public.cer` and
   `src/main/resources/config/security/opendata-config-private.pfx`.
2. Create a local bootstrap override file containing `application.database.url`,
   `application.database.user`, and `application.database.password`.
3. Run `opendata --register --file <bootstrap.properties>`.
4. Confirm `src/main/resources/config/application.properties` now contains an
   encrypted database password and `application.use-database-properties=true`.
5. Apply `sql/sqlserver/015-create-configuration-store.sql` and the permission
   grants before relying on database-backed configuration.
6. Restrict the bootstrap file and the generated certificate files.
7. Confirm logs do not contain the password or decrypted password value.

## Local development URL

```properties
application.database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
application.database.user=OpenData
application.database.******
```

## Production URL principle

Use `encrypt=true`, a trusted SQL Server certificate and
`trustServerCertificate=false`. Avoid embedding credentials in the URL.

## Permission test

Connect as `OpenData` and verify ordinary reads/writes succeed but schema changes
fail. The role must be able to read and update the configuration store tables,
but it must not be granted broad schema-management rights.
