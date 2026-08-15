# Distribution Contents

**Version:** 3.0.0  
**Status:** Current release-candidate packaging baseline  
**Baseline date:** 15 August 2026

## Source distribution

Include project source, SQL, scripts, configuration examples, documentation,
`LICENSE`, `NOTICE`, third-party notices and data-source notices. Exclude local
IDE state, build outputs, databases, customer files, backups, credentials and
private deployment keys.

## Documentation distribution

Include generated manuals and required project notices. Generated outputs must
match the release source manifests and diagrams. When the Windows Help build is
part of the release, retain the compiled Technical User Guide CHM with the
packaged application.

## Windows runtime distribution

`scripts/Build-Windows-Package.ps1` provides the Version 3.0.0 `jpackage` path.
Its default `app-image` output creates:

- the normal `OpenData` GUI launcher;
- an additional console launcher named `OpenData-CLI` using the same main class;
- the application JAR and runtime dependency set; and
- compiled `OpenData-Technical-User-Guide.chm` under `help` when the CHM exists.

If compiled Help is absent, the packaged GUI continues to use the built-in
JavaFX Help fallback. Installer types `exe` and `msi` are also supported by the
script, subject to the normal Windows `jpackage` prerequisites.

Treat the script as a packaging implementation, not as release evidence. Test
the resulting app image/installer on a clean Windows environment, verify both
GUI and CLI launchers, verify Help behaviour, and review the included JDK/runtime
and third-party licence/notice obligations before publication.

## Mandatory archive review

Inspect the actual ZIP, app image or installer inputs rather than relying on
`.gitignore`. Search for passwords, deployment-specific `{enc}` values, PFX or
private-key files, Octopus PDFs, account data, database backups, local absolute
paths and obsolete backup directories.
