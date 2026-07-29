# Security Policy

## Supported versions

OpenData is currently preparing its first public release. Until a supported
release is published, security fixes are applied to the current `main` branch.

| Version | Supported |
|---|---|
| `main` development branch | Yes |
| Unreleased local archives or historical snapshots | No |

This table will be replaced with version-specific support periods when formal
releases begin.

## Reporting a vulnerability

Do not open a public issue for a suspected vulnerability. Send a private report
to `terry.curran@towermarsh.co.uk` with the subject `OpenData security report`.

Include, where possible:

- the affected version, commit or branch;
- the component and configuration involved;
- steps to reproduce the issue;
- expected and observed behaviour;
- the likely impact;
- a proof of concept that does not expose real credentials or personal data;
- any suggested remediation.

You should receive an acknowledgement within five working days. The maintainer
will assess the report, may request clarification, and will coordinate a fix and
disclosure date appropriate to the risk. Please allow reasonable time for a fix
before publishing details.

## Security expectations

OpenData processes external files and writes to SQL Server. Deployments must:

- keep database credentials outside source control;
- use least-privilege database accounts;
- validate remote locations and downloaded content;
- restrict writable work, archive and log directories;
- review logs before sharing them;
- keep Java, Maven dependencies, SQL Server and build tooling patched;
- treat plugins and configuration files as trusted code and configuration;
- test backups and restoration before production use.

The current repository is not yet declared production-ready. Known security and
release gaps are tracked in the documentation review records.

## Disclosure and credit

Validated reports will be credited in release notes unless the reporter requests
anonymity. Reports that concern unsupported third-party systems or cannot be
reproduced may be closed with an explanation.
