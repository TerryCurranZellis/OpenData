# Historical electricity plugins (Great Britain)

## Overview

Two independent OpenData plugins, `electric-demand` and `electric-supply`, load historical Great Britain datasets supplied by the National Energy System Operator (NESO). Each depends only on `opendata-api` and its own third-party parsing libraries, never on the other plugin. No existing core Java modifications are required. Northern Ireland is not covered.

## Installation

1. Copy both `opendata-plugin-*` directories into the existing `opendata-plugins` folder.
2. Add the two `<module>` entries shown in `integration/opendata-plugins-pom-module-additions.xml` to `opendata-plugins/pom.xml`.
3. Apply the two SQL scripts to the configured `OpenData` SQL Server database.
4. Build with Maven using the project's standard reactor build. Both modules have JUnit Jupiter test dependencies (test-scoped); no test JARs are redistributed.
5. Register `electric-demand.properties` and `electric-supply.properties` using the existing OpenData registration mechanism, or their packaged classpath discovery. Do not duplicate plugin ids when registering.
6. Run `opendata --plugin electric-demand --execute --dry-run` then `opendata --plugin electric-supply --execute --dry-run`. Finally run without `--dry-run`.

## Configuration

Both plugin `.properties` definitions include `start-date` and `end-date` (ISO yyyy-MM-dd, inclusive), `api.base-url`, `api.dataset-id`, and HTTP connection/request timeout seconds. All values are read from OpenData plugin definitions; no environment variables, JVM properties, secrets or API tokens are needed. Edit individual plugin properties to change ranges. Initial backfill defaults to **2009-01-01 through 2025-12-31**; extend end-date to import later years. NESO may revise older records: reruns update existing primary keys.

## Data definitions

**Demand:** one row per GB settlement date and settlement period. `ND` and `TSD` are different operational demand measures; do not add them. GB settlement periods can number 46, 48 or 50 during clock changes. SQL records settlement period instead of deriving a UTC timestamp. Other measures cover England/Wales demand, estimated embedded wind/solar and pumped storage demand. All power columns are MW (not MWh).

**Supply:** one row per NESO generation mix UTC timestamp, with fuel-category MW, imports MW, generation total and reported carbon intensity. Fuel totals and carbon intensity are sourced values, not derived. Embedded renewable figures are estimates. `OTHER` currently encompasses battery net discharge and transmission-connected solar; `STORAGE` represents pumped storage, according to NESO's dataset notes. The dataset applies corrections and seasonal processing; this is not a direct raw power station meter feed.

## Extraction and loading

Each plugin independently queries CKAN `datapackage_show`, discovers matching CSV resource URLs, streams the CSV using Apache Commons CSV, validates and maps dates/numeric values, and inserts or updates SQL Server rows based on the business keys. Reruns are intended to be idempotent. Dry-run downloads and parses but performs no database writes. JDBC transactions are committed every 500 affected records for bounded transaction size, with rollback of the active transaction on failure. Previously committed batches remain committed if a later error occurs. Empty numeric cells map to SQL NULL.

**Important operational notes:** Full-history generation mix CSVs can be large and may require several minutes and substantial bandwidth; initial import into SQL Server can be slow due to row-by-row upserts. Set the request timeout according to network behaviour. The supplied code assumes source CSV headers/CKAN metadata fields remain consistent; test against a recent file before scheduling production collection. If NESO changes resource layouts or canonical dataset identifiers, update the parser and property definition. The supplied package was not integration-tested against live NESO or a SQL Server instance in this environment.

## SQL checks

```sql
SELECT YEAR(SettlementDate) AS Year, COUNT(*) AS Intervals, AVG(NationalDemandMW) AS AverageDemandMW
FROM electricdemand.Demand GROUP BY YEAR(SettlementDate) ORDER BY Year;

SELECT YEAR(DateTimeUtc) AS Year, COUNT(*) AS Intervals, AVG(WindMW) AS AvgWindMW, AVG(GasMW) AS AvgGasMW
FROM electricsupply.GenerationMix GROUP BY YEAR(DateTimeUtc) ORDER BY Year;
```

## Provenance

- NESO Historic Demand: https://www.neso.energy/data-portal/historic-demand-data
- NESO Historic Generation Mix and Carbon Intensity: https://www.neso.energy/data-portal/historic-generation-mix
- NESO CKAN API: https://api.neso.energy/api/3/action/datapackage_show
- Always confirm current NESO Open Data Licence terms and preserve source attribution when publishing derived data.
