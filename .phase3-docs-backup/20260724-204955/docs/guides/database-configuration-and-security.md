# Configure Database Access Securely

**Document ID:** GUIDE-DB-SECURITY-001  
**Version:** 1.0  
**Baseline date:** 24 July 2026

1. Copy `config/database.properties.example` to a location outside the repository.
2. Set the URL, user and password locally.
3. Restrict the file to the account that runs OpenData.
4. Point the existing configuration layering mechanism at the external file.
5. Run the application database health check before an import.
6. Confirm logs do not contain the password or authentication properties.

## Local development URL

```properties
database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
```

## Production URL principle

Use `encrypt=true`, a trusted server certificate and
`trustServerCertificate=false`. Avoid embedding credentials in the URL.

## Permission test

Connect as `OpenData` and verify ordinary reads/writes succeed but schema changes
fail. For example, a controlled test environment should reject:

```sql
CREATE TABLE core.should_not_be_allowed (id int);
```

Remove any test object if the command unexpectedly succeeds and correct the
role grants before deployment.
