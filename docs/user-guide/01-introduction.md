# 1. Introduction

**Document ID:** USER-001  
**Version:** 2.0  
**Status:** Version 2.0.0 baseline  
**Baseline date:** 2 August 2026

---

OpenData is a Java 17 command-line application that acquires source data,
transforms it into validated records and loads it into Microsoft SQL Server.
Version 2.0.0 uses SQL Server for normal runtime and plugin configuration after a
one-time registration step.

Three plugins are installed:

| Plugin ID | Data source | Input form |
|---|---|---|
| `ofgem` | Ofgem Energy Price Cap | Public HTML publication page and XLSX workbook |
| `openmeteo` | Open-Meteo historical weather | Public JSON API |
| `octopus` | Octopus Energy customer statements | User-supplied local PDF files |

The Octopus plugin is not a website downloader. Place legitimately obtained
statements in the configured input directory using the required filename pattern.
The default directory is `C:\Attachments\octopus`.

A normal first installation proceeds as follows:

1. build the application;
2. install the SQL Server scripts;
3. create or provide the configuration certificate files;
4. set the initial bootstrap database password;
5. run `--register`;
6. restart and verify `--list-plugins`;
7. dry-run each plugin; and
8. enable write-mode processing only after reviewing the output and security
   settings.

The Maven JAR is not currently a self-contained executable. Use Apache NetBeans
or another classpath-aware launcher with main class
`com.towermarsh.opendata.OpenData`.
