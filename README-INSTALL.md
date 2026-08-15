# OpenData Octopus Adjustment Plugin 3.1.0 — Additive Code Pack

This pack implements the new `octopus-adjustment` plugin without replacing or editing any existing OpenData `main` source file.

## Contents

- new Java package `com.towermarsh.opendata.plugin.octopusadjustment`;
- new plugin definition `src/main/resources/config/plugins/octopus-adjustment.properties`;
- new SQL script `sql/011-create-octopus-adjustment-tables.sql`;
- new JUnit tests under `src/test/java/.../octopusadjustment`.

## Installation

1. Copy the files into the matching repository-relative paths. All paths in this pack are new.
2. Run `sql/011-create-octopus-adjustment-tables.sql` against the OpenData database.
3. Build and test the project.
4. Start OpenData in GUI mode.
5. Use the GUI **Register** action to register `octopus-adjustment.properties`. New plugins are registered through the GUI only; do not use the CLI to register this plugin.
6. Verify the registration with:

   ```text
   opendata --plugin octopus-adjustment --detail
   ```

7. Test without persistent side effects:

   ```text
   opendata --plugin octopus-adjustment --dry-run --execute
   ```

8. Run normally after validating the dry-run output:

   ```text
   opendata --plugin octopus-adjustment --execute
   ```

## Important boundaries

The plugin writes only to:

- `octopus.adjustment_electric_data`
- `octopus.adjustment_gas_data`
- `octopus.adjustment_file`

It does not write to the ordinary Octopus tables:

- `octopus.electric_data`
- `octopus.gas_data`
- `octopus.statement_file`

The transform stage reuses the existing public `OctopusStatementParser.parseAllFromFile(Path)` implementation and the existing `ElectricityRecord` / `GasRecord` model records. Adjustment source filenames are selected by the configured account-number prefix; bill dates are derived from PDF content by the existing parser, not from the filename.

## Versioning

All Java code in this pack is introduced at Version 3.1.0. Existing Version 3.0.0 source and documentation are intentionally untouched.
