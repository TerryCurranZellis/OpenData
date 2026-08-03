# OpenData Data Source Notices

**Project version:** 2.0.0  
**Review date:** 3 August 2026

## Purpose

OpenData software is licensed under Apache 2.0. Data downloaded, read or stored by
OpenData is not automatically covered by that licence. Each operator remains
responsible for the provider terms, attribution, privacy, retention and permitted
use that apply to the data they process.

## Source summary

| Plugin | Source | Acquisition in Version 2.0.0 | Rights/handling summary |
|---|---|---|---|
| `ofgem` | Ofgem Energy Price Cap publications | Public web discovery and workbook download | Crown copyright material is generally reusable under the Open Government Licence unless marked otherwise |
| `openmeteo` | Open-Meteo Historical Weather API | HTTPS API request | API data are offered under CC BY 4.0; service-plan terms and underlying-dataset attribution also apply |
| `octopus` | Customer-provided Octopus Energy statement PDFs | Local directory only | Private customer documents; no redistribution licence is granted by OpenData |

## Ofgem Energy Price Cap material

### Source and ownership

OpenData obtains Energy Price Cap workbooks from Ofgem's public website. Ofgem
states that material on its website is subject to Crown copyright unless
otherwise indicated and may be reused under the Open Government Licence. Ofgem
logos and third-party material are excluded from that general permission.

Official references:

- https://www.ofgem.gov.uk/copyright
- https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/

### Attribution

Where Ofgem material is reproduced or redistributed, use an attribution such as:

> Contains public sector information licensed under the Open Government Licence v3.0. Source: Ofgem Energy Price Cap publication.

Do not imply Ofgem endorsement. Preserve the workbook's source URL, retrieval
time, publication/period details and file hash in operational provenance where
available.

### Limitations

Price-cap publications can be revised, restructured or contain third-party
material. Operators must review the specific publication and must not assume that
logos, branding or every workbook cell are Crown copyright.

## Open-Meteo historical weather data

### Licence and service terms

Open-Meteo states that API data are offered under Creative Commons Attribution
4.0 International (CC BY 4.0). Attribution must identify Open-Meteo, link to the
service/licence and indicate changes where applicable.

The free/open-access endpoint is subject to service terms, including
non-commercial-use and request-rate conditions. Commercial, high-volume or
service-level use may require an appropriate subscription. Operators must verify
the current terms for their deployment rather than treating project inclusion as
permission for every use.

Official references:

- https://open-meteo.com/en/licence
- https://open-meteo.com/en/terms
- https://open-meteo.com/en/pricing

### Attribution

A suitable display attribution is:

> Weather data by Open-Meteo.com, licensed under CC BY 4.0.

For redistributed datasets, also include the request URL/parameters, access date,
transformations and any attribution required by the selected underlying weather
model or dataset. Open-Meteo documents underlying sources on its licence and API
pages; requirements can differ by endpoint and model.

### OpenData processing

OpenData requests configured daily historical variables, validates the response
and stores relational records. Stored values may be transformed by type
conversion, unit handling, validation and database normalisation. Those changes
must not be presented as unmodified provider data.

Open-Meteo provides data without a guarantee of accuracy, completeness or
continuous availability. OpenData likewise does not make weather data suitable
for safety-critical decisions.

## Octopus Energy customer statements

### Source boundary

Version 2.0.0 scans locally supplied files matching the configured Octopus PDF
pattern. It does not sign in to the Octopus website, scrape the customer portal,
read email, or use the Octopus API to download statements.

Octopus Energy publishes APIs for customers and partner organisations, but API
use and statement-download capability require separate technical and terms
assessment. The existence of an API is not evidence that billing-statement PDFs
can or should be downloaded through it.

Official references:

- https://developer.octopus.energy/
- https://octopus.energy/policies/terms-of-use/
- https://octopus.energy/policies/

### Customer rights and confidentiality

A customer may process their own statements for personal record keeping, subject
to their contract and applicable law. OpenData does not grant a licence to
publish, redistribute or commercialise statement layouts, Octopus branding or
another person's account information.

Statements and extracted records can contain names, addresses, account numbers,
meter identifiers, tariff details, payment information, consumption and billing
history. Treat source PDFs, database rows, logs, backups and test fixtures as
confidential personal data.

### Required controls

- Use only statements the operator is authorised to process.
- Keep input, archive and failure directories access controlled.
- Do not commit PDFs, extracted text or live account data.
- Redact examples before sharing them.
- Define retention and deletion periods for PDFs and database records.
- Do not use Octopus names or logos in a way that implies endorsement.
- Review website/API terms before adding any automated acquisition feature.

## Provenance minimum

For each external acquisition or local customer document, retain as applicable:

- provider and dataset/document type;
- source URL or controlled local origin;
- retrieval or receipt timestamp;
- original filename and cryptographic hash;
- requested parameters, period and location;
- plugin and application version;
- transformation/validation result; and
- load/run identifier.

## Combined attribution example

A report containing both provider datasets may state:

> Ofgem material contains public sector information licensed under the Open Government Licence v3.0. Weather data by Open-Meteo.com are licensed under CC BY 4.0. OpenData is an independent project and is not endorsed by either provider.

Do not include Octopus customer information in public combined datasets unless a
separate lawful basis, permission, redaction and disclosure review exist.

## Maintenance

Review these notices whenever a source URL, API plan, endpoint, workbook,
statement format, provider term, attribution rule or plugin acquisition method
changes. Provider terms can change independently of an OpenData release.
