# Adding a Plugin

**Document ID:** GUIDE-PLUGIN-001  
**Version:** 2.0  
**Status:** Version 2.0.0 developer procedure  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

![Plugin development lifecycle](../diagrams/generated/plugin-development-lifecycle.svg)

## 1. Define the source and ownership

Before coding, record:

- publisher and dataset name;
- source URI and retrieval method;
- format, update frequency and expected volume;
- licence/terms and required attribution;
- stable business key and idempotency rule;
- target schema/tables;
- retention and archive requirements; and
- whether authentication or personal data is involved.

Update data-source notices and create an ADR for a durable new architectural
choice.

## 2. Copy the maintained template

Copy `docs/templates/plugin-java/.../example` to:

```text
src/main/java/com/towermarsh/opendata/plugin/<id>
```

Rename the package and every `Example` symbol. The stable plugin ID uses
lowercase letters, digits and hyphens at the CLI/resource boundary; Java package
names must remain valid lowercase identifiers.

The root class implements `OpenDataPlugin` and has a public
`PluginDefinition` constructor. `ReflectionPluginFactory` uses that constructor
before falling back to a no-argument constructor.

## 3. Implement the five stages

| Stage | Required outcome |
|---|---|
| `initialise` | Parse typed configuration and orchestrate all stages |
| `extract` | Obtain and decode the provider source |
| `transform` | Produce immutable domain records |
| `transform.validate` | Reject invalid response or record sets |
| `load` | Own SQL transaction and return accurate counts |
| `finalise` | Cleanup/archive/report without hiding primary failures |

Dry run must stop before load and before archive or other persistent side
effects.

## 4. Add classpath registration properties

Create:

```text
src/main/resources/config/plugins/<id>.properties
```

Add the ID to:

```text
src/main/resources/config/plugins/index.properties
```

The descriptor includes `plugin.*`, `dataset.id`, one or more
`endpoint.<name>.*` groups and typed `property.<name>.*` groups. Classpath
properties are the registration source; `--register` writes them to SQL Server,
and ordinary runs resolve plugin configuration from the database.

Do not add provider selection code to the application main class.

## 5. Add SQL

Use ordered, idempotent SQL scripts for schema, tables, constraints, indexes,
permissions and verification. The application principal receives only required
rights. The load component owns its borrowed connection and transaction.

## 6. Test

At minimum cover:

- required/default/invalid configuration;
- endpoint construction and extraction failures;
- representative and malformed source fixtures;
- transformation and cross-record validation;
- dry-run absence of database and archive side effects;
- transaction commit, rollback, repeat load and accurate metrics;
- registry loading and reflection construction; and
- live SQL Server permissions and idempotency.

Mock JDBC tests are not SQL Server integration tests.

## 7. Document and register

Add:

- plugin overview;
- operator/user chapter;
- configuration and schema reference;
- source-to-column mapping or data dictionary;
- failure/recovery notes;
- architecture/sequence and data-model diagrams;
- source notice and third-party notice changes; and
- ADR status updates.

Run documentation validation, diagram rendering, `mvn clean verify`, strict
quality review and the release acceptance matrix.
