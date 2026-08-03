# Plugin Properties Reference

**Document ID:** REF-PLUGIN-PROP-001  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation reference  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Lifecycle

Classpath plugin property files are the authoritative registration definitions.
`--register` parses and validates them, then stores the flattened properties in
SQL Server. Ordinary plugin runs load their property set from
`core.plugin_property` and reconstruct the immutable `PluginDefinition`.

Editing a classpath file does not change an existing runtime definition until
registration is run again.

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

The classpath registry ID, `plugin.id` and resource filename must agree.

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
`DURATION`, `PATH` and `URI`. The definition retains the declaration but
`requireProperty` returns text; provider typed configuration performs conversion
and domain validation.

Property names are normalised to lowercase for lookup.

## Credential references

Credential metadata can define authentication type, provider, secret reference,
request location and parameter name. Actual secret resolution and application
are not implemented. Never store an actual key or token in the properties file.
