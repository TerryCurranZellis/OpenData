# Plugin Properties Reference

**Document ID:** REF-PLUGIN-PROP-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


Identity uses `plugin.*` and `dataset.id`. Named endpoints use
`endpoint.<name>.*`; typed settings use `property.<name>.*`; credentials use
`credential.<name>.*`.

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

Credential blocks contain references, never actual keys.
