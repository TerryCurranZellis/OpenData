# Licensing Policy

**Baseline:** OpenData 2.0.0  
**Reviewed:** 3 August 2026

OpenData source and project-authored documentation are distributed under the
Apache License, Version 2.0, except where a file explicitly states otherwise.

## Canonical repository files

- `LICENSE` — canonical Apache 2.0 text.
- `LICENSE.md` — Markdown copy retained for documentation links.
- `NOTICE` — project notice and attribution pointer.
- `THIRD-PARTY-NOTICES.md` — software dependency/tooling inventory.
- `DATA-SOURCE-NOTICES.md` — external data, service and customer-document terms.

## Source headers

Project-authored Java, PowerShell and SQL source should use:

```text
Copyright © 2026 Terry Curran
SPDX-License-Identifier: Apache-2.0
```

A missing header does not automatically change the repository licence, but
release review should correct inconsistent headers.

## Contributions

Contributors must have the right to submit their work. Unless explicitly agreed
otherwise, contributions are accepted under Apache 2.0 as described in
`CONTRIBUTING.md`. Do not copy code, examples, diagrams, data or documentation
from another source merely because it is publicly visible.

## Third-party software

Dependencies are not relicensed by OpenData. A binary distribution must preserve
upstream licences and notices. A shaded or combined executable requires a
resolved licence inventory and manual review before publication.

## External data and customer documents

Ofgem and Open-Meteo data retain provider licences and attribution requirements.
Octopus statements remain private customer documents. They are not OpenData
sample data and must not be included in source or public release archives.

## Names and marks

`com.towermarsh.opendata` is a technical namespace. Provider and vendor names are
used only to identify interoperability or provenance. Their names and marks
remain the property of their owners and must not be used to imply endorsement.

## Release evidence

For every public release retain:

- the reviewed licence/notice files;
- the resolved dependency inventory;
- a list of bundled third-party artifacts;
- source-data attribution review;
- archive-content and secret scans; and
- any approved licence or security waiver.
