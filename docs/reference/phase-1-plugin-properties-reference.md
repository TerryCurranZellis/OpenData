# Phase 1 Plugin Properties Reference

**Document ID:** REF-CONFIG-002  
**Version:** 1.0  
**Status:** Draft  
**Last Updated:** 22 July 2026

## Purpose

This document defines the Phase 1 properties format used to create structured
plugin definitions.

## Design Rule

Plugins must never consume raw `Properties`. The framework parses each file into
immutable records:

```text
properties file
  -> PropertiesPluginDefinitionLoader
  -> PluginDefinition
  -> ApplicationConfig
  -> OpenDataPlugin
```

This allows a later database-generated JSON representation to populate the same
records.

## Main Sections

| Prefix | Purpose |
|---|---|
| `plugin.*` | Plugin identity, implementation and version |
| `dataset.*` | Logical dataset identity |
| `endpoint.<name>.*` | Download/API/landing-page endpoints |
| `property.<name>.*` | Typed plugin-specific settings |
| `credential.<name>.*` | References to externally stored secrets |

## Endpoint Example

```properties
endpoint.current.type=file
endpoint.current.url=https://example.org/current.xlsx
endpoint.current.method=GET
endpoint.current.format=xlsx
endpoint.current.strategy=direct-http
endpoint.current.enabled=true
endpoint.current.order=10
```

## HTML Link Discovery

```properties
endpoint.current.type=landing-page
endpoint.current.format=html
endpoint.current.strategy=html-link-discovery
endpoint.current.link-discovery.css-selector=a[href]
endpoint.current.link-discovery.href-pattern=(?i).*\.xlsx$
endpoint.current.link-discovery.text-pattern=(?i).*latest model.*
```

## Typed Property Example

```properties
property.excel.header-row.value=2
property.excel.header-row.type=integer
property.excel.header-row.sensitive=false
```

## Credential Reference Example

```properties
credential.api-key.authentication-type=api-key
credential.api-key.provider=properties-file
credential.api-key.secret-reference=ofgem.api.key
credential.api-key.location=header
credential.api-key.parameter-name=X-API-Key
```

The secret itself must not be stored in the plugin definition.

## Future Database Mapping

The prefixes correspond to future normalised database entities:

- `plugin.*` → Plugin
- `endpoint.*` → PluginEndpoint
- `property.*` → PluginProperty
- `credential.*` → PluginCredentialReference

The database can later return the complete definition as JSON.
