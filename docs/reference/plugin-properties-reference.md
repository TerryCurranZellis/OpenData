# Plugin Properties Reference

**Document ID:** REF-PLUGIN-PROP-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

Plugin definitions are classpath properties parsed by
`PropertiesPluginDefinitionLoader` into immutable records. Plugins do not
receive raw `Properties`.

Identity uses `plugin.*` and `dataset.id`. Named endpoints use
`endpoint.<name>.*`; typed settings use `property.<name>.*`; credentials use
`credential.<name>.*`.

| Prefix | Purpose |
|---|---|
| `plugin.*` | Plugin identity, implementation class, enabled state and configuration version |
| `dataset.*` | Logical dataset identity |
| `endpoint.<name>.*` | URI, method, format, strategy, headers and discovery rules |
| `property.<name>.*` | Typed plugin-specific values |
| `credential.<name>.*` | References to externally managed credentials |

```properties
plugin.id=example
plugin.display-name=Example
plugin.implementation-class=com.towermarsh.opendata.plugin.example.ExamplePlugin
plugin.enabled=true
plugin.configuration-version=1
dataset.id=example-dataset
endpoint.current.type=file
endpoint.current.url=https://example.org/data.csv
endpoint.current.method=GET
endpoint.current.format=csv
endpoint.current.strategy=direct-http
property.csv.delimiter.value=,
property.csv.delimiter.type=string
```

Supported property types are `string`, `integer`, `long`, `boolean`, `decimal`,
`duration`, `path` and `uri`. Enum-like endpoint values are parsed
case-insensitively with hyphens mapped to underscores.

HTML discovery adds:

```properties
endpoint.current.type=landing-page
endpoint.current.format=html
endpoint.current.strategy=html-link-discovery
endpoint.current.link-discovery.css-selector=a[href]
endpoint.current.link-discovery.href-pattern=(?i).*\.xlsx$
endpoint.current.link-discovery.text-pattern=(?i).*latest model.*
endpoint.current.link-discovery.select-last=false
```

Credential blocks contain references, never actual keys. Although the record
model can represent credential metadata, production secret resolution is not
yet implemented. Database-backed plugin definitions remain shelved.
