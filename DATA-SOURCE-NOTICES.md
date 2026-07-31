# OpenData Data Source Notices

**Project:** OpenData  
**Project version:** 1.0.0  
**Notice reviewed:** 31 July 2026  
**Project copyright:** Copyright 2026 Terry Curran

## Purpose

OpenData downloads, transforms and stores information obtained from external
data providers. Those data are not owned by the OpenData project and are not
licensed under the Apache License, Version 2.0 merely because OpenData processes
them.

This document:

- identifies the external data sources used by the current OpenData plugins;
- records the applicable data licences and service-access terms;
- provides attribution wording for reports, applications and redistributed
  datasets;
- distinguishes source data from OpenData's transformations; and
- records limitations that downstream users must preserve.

Provider terms can change independently of OpenData. The current terms must be
reviewed whenever a plugin, endpoint, data product or release is changed.

## Current data sources

| OpenData plugin | Data provider | Data product | Access method | Principal reuse terms |
|---|---|---|---|---|
| `ofgem` | Office of Gas and Electricity Markets (Ofgem) | Energy Price Cap — Final levelised cap rates model | Ofgem public website and downloadable XLSX workbook | Crown copyright; reusable under the Open Government Licence v3.0 unless otherwise stated |
| `openmeteo` | OpenMeteo GmbH / Open-Meteo.com | Historical Weather API daily weather data | `https://archive-api.open-meteo.com/v1/archive` | API data under Creative Commons Attribution 4.0 International; separate API service terms also apply |

## Ofgem Energy Price Cap data

### Source

The OpenData `ofgem` plugin begins at Ofgem's official Energy Price Cap
publication page and discovers the current workbook whose link text identifies
the **Final levelised cap rates model**.

Official source:

<https://www.ofgem.gov.uk/energy-regulation/domestic-and-non-domestic/energy-pricing-rules/energy-price-cap>

Ofgem normally updates the price-cap information every three months. Workbook
names, publication periods and download URLs can therefore change.

### Rights and licence

Ofgem states that material on its website is subject to Crown copyright unless
otherwise indicated and that Crown copyright information, excluding logos, may
be reused under the Open Government Licence.

The relevant licence is:

**Open Government Licence, version 3.0**

<https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/>

The Open Government Licence permits copying, publishing, adapting, exploiting
and combining the information, including commercially, subject to its
conditions.

The principal conditions relevant to OpenData are:

- acknowledge Ofgem as the information provider and identify the source;
- provide a link to the Open Government Licence where reasonably practicable;
- do not imply official status, sponsorship or endorsement;
- do not mislead others or misrepresent the information or its source;
- do not use Ofgem or Crown logos unless separately authorised; and
- obtain separate permission for material identified as third-party copyright.

A downloaded workbook must be checked for any workbook-specific copyright,
licensing or third-party notices. The general Ofgem website permission does not
override rights expressly assigned to another party.

### OpenData processing

OpenData may:

- discover and download the published XLSX workbook;
- archive the original source file;
- select and interpret workbook sheets and cells;
- evaluate or read spreadsheet formula results;
- normalise names, dates, regions, payment methods and units;
- map source values into SQL Server columns; and
- combine records from multiple publication periods.

These operations create an OpenData transformation of the Ofgem source. They
do not make Ofgem responsible for the transformed records.

### Required attribution

The following attribution should accompany Ofgem-derived records when they are
displayed, exported, published or redistributed:

> Contains public sector information published by Ofgem and licensed under the
> Open Government Licence v3.0, except where otherwise stated. OpenData has
> transformed the source workbook; Ofgem has not reviewed or endorsed the
> transformed data.

For a specific publication, include the price-cap period and retrieval date
where practical, for example:

> Source: Ofgem, Final levelised cap rates model, 1 July to 30 September 2026,
> retrieved 31 July 2026. Licensed under the Open Government Licence v3.0.
> Data transformed by OpenData; no Ofgem endorsement is implied.

The period in that example must be replaced with the period represented by the
actual workbook.

### Data limitations

Ofgem price-cap figures are regulatory source information. They are not a
quotation for an individual household and should not be represented as the
exact amount a particular customer will pay. Applicable rates can depend on
region, fuel, meter type, payment method, tariff status and the period in
force.

OpenData does not warrant that a downloaded workbook is complete, current,
error-free or suitable for a particular financial or regulatory decision.

## Open-Meteo historical weather data

### Source

The OpenData `openmeteo` plugin calls:

<https://archive-api.open-meteo.com/v1/archive>

It requests daily historical weather values for configured coordinates and
dates. The current implementation requests daily maximum, minimum and mean
temperature, sunrise, sunset, daylight duration and weather code.

The plugin does not currently specify a particular reanalysis model. Open-Meteo
therefore applies its default **Best Match** selection. At the date of this
notice, Open-Meteo documents that Best Match combines ECMWF IFS, ERA5 and
ERA5-Land data seamlessly.

Historical Weather API documentation:

<https://open-meteo.com/en/docs/historical-weather-api>

### Data origins

Open-Meteo's Historical Weather API uses reanalysis and modelled weather data.
Its documented sources include:

- ECMWF Integrated Forecasting System data;
- ERA5 reanalysis data;
- ERA5-Land reanalysis data; and
- Copernicus Climate Change Service data supporting the reanalysis products.

Reanalysis data combine observations from sources such as weather stations,
aircraft, buoys, radar and satellites with numerical weather models. The
returned values are therefore gridded estimates and should not be described as
direct measurements from a weather station at the requested coordinates.

The exact model mix can vary with date, location, API defaults and changes made
by Open-Meteo. For reproducible scientific use, the selected model and query
parameters should be fixed and recorded explicitly.

### Data licence

Open-Meteo states that its API data are offered under:

**Creative Commons Attribution 4.0 International (CC BY 4.0)**

<https://creativecommons.org/licenses/by/4.0/>

The licence permits sharing and adaptation, including commercial reuse,
provided that users:

- give appropriate credit;
- provide a link to the licence;
- indicate whether changes were made;
- do not suggest endorsement by the licensor; and
- do not apply additional legal or technical restrictions that prevent others
  from exercising the licensed rights.

Open-Meteo's licence page also requires a link next to any place where
Open-Meteo data are displayed.

Official licence information:

<https://open-meteo.com/en/licence>

### Required attribution

Where Open-Meteo data are displayed in a user interface, report, export,
dashboard or other output, include a visible link such as:

> [Weather data by Open-Meteo.com](https://open-meteo.com/) — licensed under
> [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/). Data transformed
> by OpenData.

For plain-text or printed output:

> Weather data by Open-Meteo.com, licensed under CC BY 4.0. OpenData transformed
> the API response into daily database records. No endorsement is implied.

Where space permits, identify the principal underlying datasets:

> Weather data by Open-Meteo.com using ECMWF IFS, ERA5 and ERA5-Land sources,
> licensed under CC BY 4.0. Data transformed by OpenData.

Open-Meteo also recommends the following citation for academic or research use:

> Zippenfenig, P. (2023). Open-Meteo.com Weather API [Computer software].
> Zenodo. <https://doi.org/10.5281/ZENODO.7970649>

### OpenData processing

OpenData may:

- request data for configured coordinates and date ranges;
- receive JSON from the Historical Weather API;
- parse and validate the response;
- convert timestamps and dates to the configured timezone;
- map weather variables and weather codes into database columns;
- store daily records in SQL Server; and
- update previously stored records.

These are modifications or transformations for CC BY 4.0 purposes and must be
identified when the resulting data are redistributed.

### Free API service terms

The CC BY 4.0 data licence and the terms for accessing Open-Meteo's hosted API
are separate matters.

At the date of this notice, Open-Meteo's free/open-access API service:

- is limited to non-commercial use;
- permits fewer than 10,000 API calls per day;
- permits fewer than 5,000 API calls per hour;
- permits fewer than 600 API calls per minute; and
- may block applications or IP addresses that misuse the service.

Open-Meteo identifies commercial products, subscription-supported services,
advertising-supported services, promotional activity and undisclosed research
at commercial organisations as examples of commercial use.

A commercial OpenData deployment must not assume that the public free endpoint
is permitted merely because CC BY 4.0 permits commercial reuse of data already
obtained. Commercial or higher-volume API access requires the appropriate
Open-Meteo subscription, customer endpoint and credentials.

Official terms:

<https://open-meteo.com/en/terms>

The service terms and limits may change. They must be reviewed before each
release and before any deployment changes from personal or non-commercial use
to commercial use.

### Data limitations

Open-Meteo provides its service and data without guarantees of accuracy,
completeness, uninterrupted availability or suitability for a specific
purpose.

Historical Weather API values are modelled gridded estimates. They may differ
from nearby weather-station observations because of model resolution,
elevation, terrain, coastlines, local microclimates, model updates and
statistical processing.

OpenData does not warrant that Open-Meteo data are suitable for safety-critical,
medical, legal, insurance, engineering or emergency-response decisions.

## Combined attribution

Where an OpenData product displays information from both current providers, the
following concise statement may be used:

> Data sources: Ofgem energy price-cap information, licensed under the Open
> Government Licence v3.0; and weather data by Open-Meteo.com, licensed under
> CC BY 4.0. OpenData has transformed the source data. Neither provider
> endorses OpenData or its outputs.

The Open-Meteo text should remain a working hyperlink in digital output.

## Provenance records

To support attribution, auditing and reproducibility, OpenData releases and
stored datasets should preserve, where available:

- provider name;
- dataset or publication name;
- source landing-page URL;
- resolved download or API URL;
- source publication period or requested date range;
- retrieval timestamp;
- source-file name and cryptographic checksum;
- request parameters, excluding secrets;
- selected API model or default-selection status;
- source licence identifier;
- plugin ID and OpenData version;
- transformation or schema version; and
- whether records were subsequently corrected or replaced.

Archiving the original Ofgem workbook is useful for auditability. Raw API
responses or sufficient request metadata should be retained for Open-Meteo
where storage, privacy and provider terms permit.

## Redistribution requirements

When OpenData-derived data are exported or redistributed:

1. retain the applicable provider attribution;
2. include links to the OGL or CC BY 4.0 licence;
3. identify material transformations made by OpenData;
4. preserve source and retrieval metadata where practicable;
5. do not use provider names or logos in a way that suggests endorsement;
6. do not remove workbook-specific or response-specific notices;
7. do not relicense source data solely under the OpenData Apache 2.0 software
   licence; and
8. ensure any downstream application continues to satisfy the provider's
   current terms.

The OpenData source code, database schema and transformation logic can remain
under Apache License 2.0. The imported records retain their source-data terms.

## Provider names and trademarks

“Ofgem”, “Open-Meteo”, “ECMWF”, “ERA5”, “ERA5-Land” and related provider names
are used only to identify sources, services and data products.

No OpenData plugin, document, database or output is sponsored, certified,
approved or endorsed by Ofgem, OpenMeteo GmbH, ECMWF, the Copernicus Climate
Change Service or any other underlying provider.

Provider logos should not be copied into OpenData or its generated outputs
without separate permission.

## Maintenance

Review this document whenever:

- a data-source plugin is added, removed or changed;
- an endpoint, workbook, API product or underlying model changes;
- new variables or datasets are requested;
- source data are used commercially;
- an API subscription or authentication method changes;
- a provider updates its copyright, licence, attribution or service terms;
- a transformed dataset is published outside the local OpenData database; or
- an OpenData release is prepared.

The review should confirm:

- the source URL still identifies the intended official provider;
- the current licence and version;
- the exact attribution statement;
- any requirement to identify modifications;
- commercial-use restrictions;
- request and rate limits;
- third-party content exceptions;
- warranty and liability wording; and
- whether the provider requires attribution at every display location.

Where this document conflicts with a provider's current official terms, the
provider's terms control.

## Official references

### Ofgem and public-sector information

- Ofgem Energy Price Cap:
  <https://www.ofgem.gov.uk/energy-regulation/domestic-and-non-domestic/energy-pricing-rules/energy-price-cap>
- Ofgem copyright and disclaimer:
  <https://www.ofgem.gov.uk/c-ofgem-2026>
- Open Government Licence v3.0:
  <https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/>
- OGL attribution guidance:
  <https://www.nationalarchives.gov.uk/information-management/re-using-public-sector-information/uk-government-licensing-framework/open-government-licence/copyright-notices-attribution-statements/>

### Open-Meteo and weather data

- Open-Meteo Historical Weather API:
  <https://open-meteo.com/en/docs/historical-weather-api>
- Open-Meteo licence:
  <https://open-meteo.com/en/licence>
- Open-Meteo terms:
  <https://open-meteo.com/en/terms>
- Creative Commons Attribution 4.0:
  <https://creativecommons.org/licenses/by/4.0/>
- Open-Meteo recommended software citation:
  <https://doi.org/10.5281/ZENODO.7970649>
