# Integrated Repository Review

**Date:** 23 July 2026  
**Repository:** OpenData  
**Status:** Implementation overlay supplied

## Executive Summary

The repository contains the record-based plugin configuration model, the Ofgem
and OpenMeteo plugin resources, and the initial CLI and ETL foundations.
However, several earlier batches were integrated side-by-side rather than
consolidated.

The most visible defect is that `--list-plugins` is hard-coded in `Main`, so
OpenMeteo cannot appear even though its properties file and Java package exist.

This overlay introduces a Phase 1 classpath plugin registry, consolidates CSV
parsing around Apache Commons CSV, and supplies the next logical components:
HTML link discovery, streamed HTTP file download, and XLS/XLSX parsing.

## Findings

### Java baseline

Java 17 is the minimum supported version and the bytecode/API target.

The previous POM used source and target 25. It is replaced with
`maven.compiler.release=17`. The `release` option is important because it checks
both language features and the availability of Java 17 public APIs.

### Plugin listing

The current `Main` prints:

```text
Installed plugins: ofgem
```

This is not registry output. The new `ClasspathPluginRegistry` reads:

```text
config/plugins/index.properties
```

and then reads the metadata from each named plugin properties file. Both Ofgem
and OpenMeteo are included in the supplied index.

An explicit index is used because resource-directory scanning is unreliable in
packaged JAR files.

### Configuration overlap

The `config` package currently contains both:

- the earlier flat-map loader/service classes; and
- the newer structured `PluginDefinition` loader and records.

For this overlay, the existing `ConfigurationService` remains in place so the
current execution path is not broken. The new registry is responsible only for
installed-plugin metadata.

The next consolidation should make
`ApplicationConfigurationService`/`PropertiesPluginDefinitionLoader` the
canonical configuration route, then remove or deprecate the superseded flat
loader types.

### Duplicate command-line model

The repository contains both:

```text
com.towermarsh.opendata.app.CommandLineArguments
com.towermarsh.opendata.cli.CommandLineArguments
```

`Main` and the current processor use the `cli` package. The `app` version should
be checked for references and removed if unused.

### CSV parsing

The previous implementation split text using `String.split(",")`. That cannot
correctly parse quoted delimiters, embedded line breaks, escaped quotes or all
empty trailing fields.

The replacement uses Apache Commons CSV, which was already declared as a Maven
dependency.

### HTTP download handling

The existing HTTP downloader reads the complete response into memory and
interrupts the thread for both `IOException` and `InterruptedException`.

The new direct strategy streams to a `.part` file, only interrupts after an
actual `InterruptedException`, validates 2xx status codes, and moves the
completed file into place.

### HTML and Excel support

`HtmlLinkResolver` uses JSoup CSS selectors and regex rules to locate links on a
landing page. `HtmlLinkDiscoveryStrategy` then downloads the selected file.

`ExcelDataParser` uses Apache POI `WorkbookFactory`, supporting both XLS and
XLSX. It formats cell values, optionally evaluates formulas, skips hidden and
blank rows, and preserves column order.

## Files to Review for Removal

After confirming no references remain:

```text
src/main/java/com/towermarsh/opendata/app/CommandLineArguments.java
```

After the structured configuration route is wired into `Main`, review these
earlier flat-configuration classes:

```text
ConfigurationLoader
ConfigurationSource
ResolvedConfigurationValue
StandardConfigurationValidator
```

Do not remove them until compilation confirms they have no remaining callers.

## Verification Limitation

The repository could be inspected through GitHub, but this execution environment
could not clone GitHub and does not provide Maven. Consequently, a complete
`mvn clean test` run could not be performed here.

The supplied code is written for Java 17 and includes focused JUnit tests. Run
the commands in `IMPLEMENTATION.md` after applying the overlay.
