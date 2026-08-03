# Distribution Contents

## Source distribution

Include project source, SQL, scripts, configuration examples, documentation,
`LICENSE`, `NOTICE`, third-party notices and data-source notices. Exclude local
IDE state, build outputs, databases, customer files, backups, credentials and
private deployment keys.

## Documentation distribution

Include generated manuals and required project notices. Generated outputs must
match the release source manifests and diagrams.

## Runtime distribution

The current Maven output is not yet documented as a verified self-contained
runtime. Before publishing a runtime archive, define the launch command,
dependency layout, Java requirement, configuration locations and licence/notice
contents, then test on a clean machine.

## Mandatory archive review

Inspect the actual ZIP contents rather than relying on `.gitignore`. Search for
passwords, `{enc}` bootstrap values that should remain deployment-specific, PFX or
private-key files, Octopus PDFs, account data, database backups and local absolute
paths.
