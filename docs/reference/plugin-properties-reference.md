# Plugin Properties Reference

**Document ID:** REF-PLUGIN-PROP-001  
**Version:** 2.2  
**Status:** Version 2.0.0 implementation reference  
**Baseline date:** 8 August 2026  
**Minimum Java version:** 17

---

## Lifecycle

Packaged plugin property files are the default registration definitions.
`--plugin <id|all> --register` parses selected packaged definitions, while
`--plugin <id> --register --file <filename>` parses one complete external
definition. Registration stores flattened properties and metadata in SQL Server.
Ordinary runs load their property set from `core.plugin_property` and reconstruct
the immutable `PluginDefinition`.

Editing a classpath file does not change an existing runtime definition until
registration is run again.

The stored property set for one registered plugin can be inspected with:

```text
opendata --plugin <id> --detail
```

`--detail` reads the plugin's current rows from `core.plugin_property`. It
requires exactly one named registered plugin and does not execute that plugin.

## Identity

```properties
plugin.id=example
plugin.display-name=Example Plugin
plugin.description=Example provider integration.
plugin.implementation-class=com.towermarsh.opendata.plugin.example.ExamplePlugin
plugin.enabled=true
plugin.configuration-version=1
dataset.id=example-dataset
```

For packaged definitions, catalogue ID, `plugin.id` and resource filename must
agree. For external registration, `plugin.id` must match the command-line ID.

## Endpoint groups

```properties
endpoint.source.type=API
endpoint.source.url=https://example.invalid/data.json
endpoint.source.method=GET
endpoint.source.format=JSON
endpoint.source.strategy=DIRECT_HTTP
endpoint.source.enabled=true
endpoint.source.order=1
endpoint.source.credential=
```

Supported model values include:

- endpoint types: `API`, `FILE`, `LANDING_PAGE`, `HTML_TABLE`, `METADATA`,
  `AUTHENTICATION`;
- formats: `CSV`, `JSON`, `XML`, `XLS`, `XLSX`, `HTML`, `ZIP`, `TEXT`,
  `BINARY`;
- strategies: `DIRECT_HTTP`, `AUTHENTICATED_API`, `HTML_LINK_DISCOVERY`,
  `HTML_TABLE`, `BROWSER_AUTOMATION`.

The enums describe the model; not every value has an executable shared runtime
strategy. See [supported data formats](supported-data-formats.md).

Endpoint headers and query parameters are non-secret:

```properties
endpoint.source.header.Accept=application/json
endpoint.source.query.timezone=Europe/London
```

## HTML link discovery

```properties
endpoint.source.link-discovery.css-selector=a[href]
endpoint.source.link-discovery.href-pattern=(?i).*\.xlsx$
endpoint.source.link-discovery.text-pattern=(?i).*latest.*
endpoint.source.link-discovery.select-last=false
```

## Typed provider properties

```properties
property.request-timeout-seconds.value=60
property.request-timeout-seconds.type=INTEGER
property.request-timeout-seconds.sensitive=false
property.request-timeout-seconds.description=Complete request timeout.
```

Declared types are `STRING`, `INTEGER`, `LONG`, `BOOLEAN`, `DECIMAL`,
`DURATION`, `PATH` and `URI`. The declaration is registration metadata. The
typed plugin configuration chooses a Java accessor and applies domain rules.

Use `PluginPropertyValues` rather than plugin-local parsing helpers:

```java
final var properties = new PluginPropertyValues(definition);
final int timeoutSeconds = ValidationRules.requireRange(
        properties.integer("request-timeout-seconds", 60),
        1,
        600,
        "request-timeout-seconds");
```

Supported shared conversions include text, integer, long, double,
`BigDecimal`, boolean, ISO-8601 duration, ISO date, path and URI. A plugin may
supply `ValueParser<T>` for a provider-specific type.

Boolean values accept `true`, `false`, `yes`, `no`, `1`, `0`, `on` and `off`
case-insensitively.

Missing or invalid values produce messages identifying the plugin and property
without echoing the configured value. Property names are normalised to lowercase
for lookup.

For full method details see
[Shared Validation and JDBC Reference](shared-validation-and-jdbc-reference.md).

## Inspecting stored values

After registration or re-registration, use:

```text
opendata --plugin example --detail
```

The command displays registry identity/status information followed by the stored
property names and values. This gives an operator a direct view of the
configuration OpenData will use for that plugin without running it.

Because the values are written to standard output, do not redirect or distribute
the output if a deployment has stored values that should not be disclosed.

## Configurable SQL identifiers

When a property supplies a schema or table name, validate it with
`SqlIdentifiers`. Identifiers cannot be parameterised, but all data values still
must use prepared-statement parameters.

```java
final String qualifiedTable = SqlIdentifiers.qualify(schema, table);
```

Do not accept arbitrary SQL fragments through plugin properties.

## Credential references

Credential metadata can define authentication type, provider, secret reference,
request location and parameter name. Actual secret resolution and application
are not implemented. Never store an actual key or token in the properties file.

## Enabled status

`plugin.enabled` sets initial status only for a newly registered row.
Re-registration preserves current persistent status. Use `--enable` and
`--disable` for lifecycle administration.

The current status and stored `plugin.enabled` property can be inspected with
`--plugin <id> --detail`.

---
