# Command-line and persistent plugin-registry update

**Document ID:** REVIEW-CLI-REGISTRY-2026-08-03  
**Version:** 2.0.0 update  
**Status:** Implemented; target-environment acceptance pending  
**Date:** 3 August 2026

## Scope

This update completes the command-line lifecycle requested after the Batch 8
documentation baseline. It changes both runtime code and documentation.

Implemented operations:

- repeated `--plugin/-p` selection or `--plugin all`;
- `--register/-r` for packaged definitions or one complete external
  `--file/-f` definition;
- `--unregister/-u`, with `--remove` as a long-form compatibility alias;
- `--enable/-e` and `--disable/-d`;
- `--parallelism/-j` constrained to 1-64;
- `--dry-run` with short form `-n`;
- `--verbose/-v`, `--help/-h`, `--about/-a` and `--list-plugins/-l`.

## Short-option collision

The requested specification assigned `-d` to both disable and dry-run. One short
option cannot identify two different operations. This implementation assigns
`-d` to `--disable` and `-n` to `--dry-run`. The long form `--dry-run` is
unchanged.

## Persistent state

`core.plugin_registry` is now authoritative for installed plugin metadata and
enabled status. `core.plugin_property` remains the complete registered property
store. Packaged `config/plugins/index.properties` is a registration catalogue,
not the runtime installed-plugin list.

Re-registration replaces metadata and properties but preserves an existing
enabled/disabled state. Enable/disable synchronises the registry row and stored
`plugin.enabled` property. Unregistration removes both the registry row and
stored plugin properties.

## Dry-run correction

Octopus dry-run now skips the processed-file ledger. All enabled plugins can be
selected with `--plugin all --dry-run`; provider data writes, run-audit writes
and Octopus archive movement remain disabled.

## Verification status

The update includes parser, selection, registry-status and Octopus dry-run
regression tests. Static Java compilation and documentation validation were run
in the delivery environment. A full Maven test/package run and SQL Server
migration/acceptance run remain required on the target development machine.
