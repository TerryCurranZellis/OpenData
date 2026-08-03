# Release Compliance Checklist

Use with the technical final release checklist.

## Licensing and dependencies

- [ ] `LICENSE`, `NOTICE`, third-party and data-source notices reviewed.
- [ ] Resolved runtime/test dependency inventory retained.
- [ ] Bundled artifacts and upstream notices identified.
- [ ] Preview dependency decision recorded.
- [ ] No unapproved third-party code, images, fonts or templates included.

## Data and privacy

- [ ] No Octopus statements or extracted personal data in source or archives.
- [ ] Test/documentation examples are synthetic or irreversibly redacted.
- [ ] Open-Meteo attribution and applicable service plan verified.
- [ ] Ofgem attribution and third-party-material exclusions reviewed.
- [ ] Retention and deletion rules documented for production data.

## Security and distribution

- [ ] No live passwords, private deployment keys or database backups included.
- [ ] Committed development credentials rotated before production use.
- [ ] Archive contents reviewed independently of `.gitignore`.
- [ ] Checksums generated after final packaging.
- [ ] Any unresolved item has an owner, severity and explicit waiver/decision.
