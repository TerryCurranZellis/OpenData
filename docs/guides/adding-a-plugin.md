# Adding a Plugin

**Document ID:** GUIDE-PLUGIN-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Start from the Java template

Copy the [plugin Java template](../templates/plugin-java/README.md) package into
`src/main/java/com/towermarsh/opendata/plugin`, rename `example` to the stable
lowercase plugin id, and replace every `Example` symbol. The template is a
compilable structural starting point; its loader deliberately remains
unimplemented so a write run cannot report false success.

## Required package ownership

Every provider plugin owns its source-specific implementation below
`com.towermarsh.opendata.plugin.<id>`:

| Package | Owns |
|---|---|
| plugin root | `OpenDataPlugin` workflow facade only |
| `config` | conversion from `PluginDefinition` to typed settings |
| `download` | provider request/discovery and source acquisition |
| `extract` | source-format decoding into an extracted representation |
| `transform` | conversion into domain records |
| `transform.model` | immutable provider domain records |
| `transform.validate` | provider-specific cross-record and response validation |
| `load` | SQL, transaction boundary, provenance and load counts |
| `exception` when needed | provider-specific checked boundary failures |

Shared `download`, `discovery`, `parser`, `database`, `plugin`, `validation` and
exception packages remain provider-neutral. Do not place provider classes in a
top-level package such as `com.towermarsh.opendata.ofgem`.

## Workflow contract

The root plugin facade implements
`OpenDataPlugin.execute(PluginExecutionContext)` and orders the stages:

1. download;
2. extract;
3. transform and validate;
4. return read/skipped metrics for `context.dryRun()`;
5. load in one provider-owned transaction;
6. return accurate `PluginMetrics`.

Keep plugin objects thread-confined. Borrow a JDBC connection inside the load
operation, never retain it on the plugin, and restore pooled-session state before
closing the logical connection.

## Registry and configuration

1. Choose a stable lowercase id.
2. Add `src/main/resources/config/plugins/<id>.properties`.
3. Set `plugin.implementation-class` to the root plugin facade.
4. Add the id to `config/plugins/index.properties`.
5. Define endpoints, typed properties and credential references.
6. Never add provider selection to `Main`.

## Tests

Mirror production packages below `src/test/java`. At minimum test:

- typed configuration defaults and invalid values;
- request URI/discovery rules and download failures;
- representative extraction fixtures;
- transformation and cross-record validation;
- dry-run absence of database access;
- transaction commit, rollback and accurate load counts;
- registry construction from the properties resource.

Mocked JDBC tests are unit tests. Retain a separate live database acceptance
record for schema installation, permissions, write, repeat-load and rollback.

## Documentation and SQL

Add or update:

- plugin overview and configuration reference;
- data dictionary and source mapping;
- run guide and failure/recovery notes;
- architecture/sequence diagrams;
- ordered SQL migration and least-privilege grants;
- ADR when the plugin introduces a durable architectural choice.

Run the documentation link, PlantUML, DOCX and PDF checks before publishing.
