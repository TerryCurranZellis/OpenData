# 1. Introduction

**Document ID:** USER-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

OpenData downloads public datasets, converts them into typed records and can
store them in SQL Server. It is a Java 17 command-line application.

Two plugins are installed:

| Plugin id | Data |
|---|---|
| `ofgem` | Current Ofgem energy price-cap workbook |
| `openmeteo` | Historical daily weather from Open-Meteo |

You may list plugins, run one plugin, run both together or perform a dry run.
Dry run is the safest first action because it verifies remote access and parsing
without using the database.

The application is pre-production. The current Maven package requires an IDE or
classpath-aware launcher, and live SQL Server acceptance remains outstanding.
