# Release and Versioning

**Document ID:** DEV-RELEASE-001  
**Version:** 1.0  
**Status:** Proposed release process  
**Baseline date:** 26 July 2026

---

## Version source

The Maven project version is the build version. Packaged runtime output should
obtain the same value from the JAR manifest. An unpackaged IDE run reports
`development`.

## Release gate

A release candidate requires:

- clean `mvn test` and package output on Java 17;
- no preview runtime dependencies unless explicitly accepted;
- executable-package and exit-code verification;
- clean/repeat database installation;
- Ofgem and OpenMeteo dry and write runs;
- rollback, idempotency and least-privilege tests;
- documentation validation and successful PlantUML rendering;
- resolved critical gaps or explicit release waivers.

## Procedure

1. update the Maven version and change log;
2. freeze ADR statuses and the documentation baseline date;
3. run the release gate from a clean checkout;
4. tag the exact verified commit;
5. publish checksums with the application package;
6. retain the test evidence and database schema version.

## Compatibility

Patch releases must preserve documented CLI and schema behaviour. A breaking CLI,
plugin-property or database change requires migration notes and an appropriate
version increment.
