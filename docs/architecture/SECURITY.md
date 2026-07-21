# Security Architecture

## 1. Security objectives

- Protect database credentials.
- Prevent untrusted source content from escaping approved boundaries.
- Maintain source integrity and provenance.
- Minimise database privileges.
- Avoid sensitive diagnostic output.
- Keep dependencies reviewed and current.

## 2. Threat boundaries

Untrusted inputs include:

- Command-line values.
- Parameter files.
- Remote URLs and redirects.
- HTTP headers.
- Downloaded files.
- CSV, JSON, XML, spreadsheet, or other source values.
- Provider filenames.
- Database errors containing environmental detail.

## 3. Credentials

Credentials must not be:

- Hard-coded.
- Committed to Git.
- Printed in logs.
- Included in exception messages.
- Included in configuration hashes.

Approved initial approaches include:

- A protected local password file referenced by the parameter file.
- Integrated authentication where the environment supports it.
- An operating-system credential facility introduced through an ADR.

## 4. Database permissions

The runtime account should receive only:

- Connect permission.
- Execute permission on approved load procedures, or targeted DML permissions.
- Read/write access to the required OpenData schemas.
- No server-administrator role.
- No rights to unrelated databases.

Migration credentials should be separate from runtime credentials.

## 5. Network security

- Use HTTPS for remote acquisition.
- Validate TLS certificates.
- Allow redirects only to approved schemes and, where required, approved hosts.
- Set bounded connection and read timeouts.
- Do not disable certificate validation in production.
- Restrict proxy configuration to explicit approved settings.

## 6. File security

- Normalise and validate archive paths.
- Reject traversal such as `..`.
- Sanitize provider-supplied filenames.
- Write temporary files in the target filesystem.
- Move completed files atomically where supported.
- Restrict directory permissions.
- Apply retention rules deliberately.
- Do not execute downloaded content.

## 7. Input validation

- Limit maximum source size where practical.
- Limit field lengths.
- Use streaming parsers for large content.
- Disable dangerous XML features when XML is supported.
- Reject unexpected columns or media types according to plugin policy.
- Use prepared SQL statements.
- Never concatenate source values into SQL.

## 8. Logging

The following must be redacted:

- Passwords.
- Tokens.
- API keys.
- Credential-bearing URLs.
- Full sensitive connection strings.
- Secret-file content.

Log only the minimum source data necessary to diagnose a rejected record.

## 9. Dependency management

- Pin dependency versions.
- Review transitive dependencies.
- Prefer libraries with active maintenance.
- Record exceptions for libraries that bring another logging framework.
- Bridge or suppress third-party logging so project logging remains JUL.
- Run vulnerability scanning as part of release preparation.

## 10. Security review triggers

A security review is required when:

- Adding credential storage.
- Adding XML parsing.
- Adding archive extraction.
- Supporting user-supplied SQL.
- Introducing a web interface.
- Adding independently loaded external plugin JARs.
- Downloading from user-selected arbitrary hosts.
