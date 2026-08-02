# Project Vision

**Document ID:** ARCH-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


## Vision

OpenData provides a reusable foundation for collecting public data from
independent publishers without creating a separate application for every source.
A new source should normally require a plugin definition and source-specific
transformation, not changes throughout the framework.

## Goals

- support APIs, direct files and HTML publication pages;
- preserve original source artefacts for audit and reprocessing;
- validate before persistence;
- isolate dataset rules from reusable infrastructure;
- provide repeatable command-line execution;
- remain understandable to a small development team;
- keep documentation beside the code.

## Current baseline

CSV, JSON, XLS/XLSX, static HTML link discovery, SQL Server and manually
invoked runs are implemented foundations. Ofgem and OpenMeteo are the current
executable reference plugins, and Octopus is partially implemented through a
typed PDF transform stage. Graphical administration, distributed services,
browser automation, internal scheduling and database-managed plugin definitions
are deferred or shelved.

Ofgem can retain its source workbook when write-mode archiving is enabled.
OpenMeteo does not currently archive the raw JSON response, so raw-data
preservation remains plugin-specific rather than universal.

## Principles

Java 17 minimum; records for immutable values; configuration before custom code;
interfaces at infrastructure boundaries; raw-data preservation; documentation as
code.

## Success criteria

Ofgem and OpenMeteo execute through the same lifecycle, Octopus already fits the
same plugin-registration model, plugins can be added without modifying `Main`,
and format differences are handled by reusable strategies and parsers.
