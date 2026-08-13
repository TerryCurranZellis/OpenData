# Adding a JSON Plugin

**Document ID:** GUIDE-JSON-001  
**Version:** 2.0  
**Status:** Version 2.0.0 developer procedure  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

## Choose the correct JSON path

The generic `JsonDataParser` accepts a top-level JSON array whose members can be
deserialised as `Map<String,String>`. It is suitable only for flat, text-valued
records.

```java
var rows = new JsonDataParser().parse(file);
```

Nested objects, arrays, numeric/boolean values requiring typed handling or
provider-specific envelopes should use a dedicated Jackson model and extractor,
as the OpenMeteo plugin does.

## Plugin responsibilities

- enforce the expected root shape and required fields;
- reject unexpected or incomplete responses;
- convert text/numbers into immutable provider records;
- validate date ranges, units and cross-record invariants;
- preserve source identifiers needed for audit; and
- keep database work in `load`.

Do not silently coerce a nested provider response into string maps merely to use
the generic parser.

## Tests

Include representative responses, missing fields, `null`, unexpected types,
empty arrays, unknown fields, malformed JSON, provider error payloads, date/unit
boundaries and large responses. Tests should use local fixtures rather than a
mutable live API.
