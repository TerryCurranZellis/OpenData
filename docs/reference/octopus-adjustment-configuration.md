# Octopus Adjustment Configuration Reference

**Document ID:** REF-OCTOPUS-ADJUSTMENT-CONFIG-001  
**Version:** 3.1.0  
**Baseline date:** 15 August 2026  

---

## Plugin definition

Recommended Version 3.1.0 definition:

```properties
plugin.id=octopus-adjustment
plugin.display-name=Octopus Energy Adjustments
plugin.description=Parses Octopus Energy adjustment PDF files and persists recalculated electricity and gas billing records separately.
plugin.implementation-class=com.towermarsh.opendata.plugin.octopusadjustment.OctopusAdjustmentPlugin
plugin.enabled=true
plugin.configuration-version=1

dataset.id=octopus-energy-adjustments

property.account.number.value=A-5F191685
property.account.number.type=string
property.account.number.sensitive=false
property.account.number.description=Octopus account number used as the required adjustment PDF filename prefix.

property.input.directory.value=C:\\Attachments\\octopus
property.input.directory.type=path
property.input.directory.sensitive=false
property.input.directory.description=Directory containing Octopus Energy adjustment PDF files.

property.working.directory.value=work\\octopus-adjustment
property.working.directory.type=path
property.working.directory.sensitive=false
property.working.directory.description=Temporary working directory for adjustment processing.

property.archive.directory.value=archive\\octopus-adjustment
property.archive.directory.type=path
property.archive.directory.sensitive=false
property.archive.directory.description=Directory receiving successfully committed adjustment PDF files.
```

## Properties

| Property | Type | Required | Sensitive | Purpose |
|---|---|---:|---:|---|
| `account.number` | string | Yes | No | Required filename prefix |
| `input.directory` | path | Yes | No | Source adjustment PDF directory |
| `working.directory` | path | Yes | No | Temporary processing directory |
| `archive.directory` | path | Yes | No | Successful-source archive |

## Account-number validation

The initial configured value is:

```text
A-5F191685
```

The configuration should reject:

- missing account number;
- blank account number;
- values containing path separators; and
- values that cannot safely be used as a filename prefix.

The account number is operational metadata rather than a credential, but it is
still personal account information and should not be unnecessarily exposed.

## Filename selection

Given:

```properties
property.account.number.value=A-5F191685
```

the extract stage accepts names such as:

```text
A-5F191685-419015087-1.pdf
A-5F191685-123456789-2.PDF
```

and rejects unrelated files.

The suffix structure after the account number must not be interpreted unless a
future Octopus format specification requires it.

## Path validation

The input directory must exist and be readable before extraction.

For write mode, the archive directory must be usable before source movement.

Directory creation policy should follow the existing OpenData plugin
configuration conventions.

## Plugin registration

New plugins are registered through the OpenData **GUI only**. The CLI must not
be used to register `octopus-adjustment`.

Place the Version 3.1.0 plugin definition in the normal plugin configuration
location, start the GUI, and use the GUI **Register** action to discover and
register the new plugin definition.

There is deliberately no `--register` CLI example for this plugin because CLI
registration of new plugins is not supported.

## Detail example

```text
opendata --plugin octopus-adjustment --detail
```

The detail display should mask any future sensitive properties using the normal
OpenData sensitive-value rules.
