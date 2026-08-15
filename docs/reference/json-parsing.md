# JSON Parsing Reference

**Document ID:** REF-JSON-001  
**Version:** 3.0.0  
**Status:** Generic flat-array parser implemented  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

`JsonDataParser` uses a default Jackson `ObjectMapper` and implements
`DataParser`.

It expects a top-level JSON array that can be deserialised as:

```java
List<Map<String, String>>
```

This is appropriate for flat records whose values are textual or naturally
coercible to strings by the configured Jackson mapping. It is not a general
schema-aware JSON ingestion engine.

Provider responses containing envelopes, nested objects, arrays, typed numeric
or boolean fields, metadata sections or error variants should use dedicated
Jackson records/classes and provider validation. OpenMeteo follows this
provider-specific approach.

I/O and deserialisation failures are wrapped in `ImportException`. The generic
parser has no configurable `ObjectMapper`, schema validation, streaming mode or
archive behaviour in the current baseline.
