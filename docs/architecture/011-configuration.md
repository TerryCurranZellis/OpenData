# Configuration Architecture

**Document ID:** ARCH-011  
**Version:** 2.0  
**Status:** Database-backed registration implemented  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Configuration categories

### Bootstrap file

`src/main/resources/config/application.properties` is the writable bootstrap
file used before any database-backed lookup. Its intended contents are limited
to:

```properties
application.version=2.0.0
application.use-database-properties=<true|false>
database.url=<JDBC URL>
database.user=<database user>
database.password=<plain text for first registration or {enc}... afterwards>
```

The current implementation reads this repository-local path first and falls back
to the classpath resource only when the file is absent. Because registration
rewrites it, an installed deployment must provide a writable external equivalent
before packaging can be treated as complete.

### Installed plugin registry

`src/main/resources/config/plugins/index.properties` lists installed plugin ids.
This remains classpath-backed in both modes.

### Application and plugin values

When `application.use-database-properties=false`,
`ClasspathConfigurationPropertiesSource` reads packaged application defaults and
`config/plugins/<id>.properties`.

When the flag is `true`, `JdbcConfigurationPropertiesSource` reads:

- `core.application_property` for runtime application values;
- `core.plugin_property` for plugin property values.

`PropertiesPluginDefinitionLoader` parses either source into the same
storage-neutral `PluginDefinition` model.

## Registration

`--register` performs the following operation:

1. load bootstrap values and require a non-blank database password;
2. connect using the bootstrap URL, user and decrypted password;
3. merge application defaults with classpath application values;
4. force `application.use-database-properties=true`;
5. encrypt the database password and upsert application values, marking the
   password row as encrypted;
6. upsert every installed plugin's classpath values into
   `core.plugin_property`;
7. rewrite the bootstrap file with the encrypted password and database mode
   enabled.

The SQL objects must exist and the application role must have the required
configuration-table permissions before registration.

## Override precedence

An optional `--file` is loaded before bootstrap and runtime resolution:

- application entries use `application.<key>`;
- a single-plugin run may use unscoped plugin entries;
- a multi-plugin run uses `plugin.<id>.<key>`;
- unscoped plugin entries in a multi-plugin file are rejected.

Invocation overrides take precedence over the selected classpath or database
property source. Keys are normalised case-insensitively.

## Encryption boundary

`RsaConfigurationPasswordCipher` encrypts the database password with the public
certificate and decrypts it with the matching PKCS#12 private key store. Values
are marked with `{enc}` and Base64 encoded. The encryption mechanism protects a
value at rest only when the private key store and its password are protected
separately from the encrypted data.

See [Security and Credentials](017-security-and-credentials.md) for the current
release-blocking source-tree issues.
