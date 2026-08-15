# Download and Discovery Reference

**Document ID:** REF-DOWNLOAD-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation reference  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Generic HTTP download

`HttpDataDownloader` streams a GET response to a sibling `.part` file, enforces
configured overwrite and maximum-size rules, then moves the completed file into
place. It follows normal redirects, uses explicit connect/request timeouts and
restores the interrupted flag.

The returned `DataFile` contains local path, byte size and download timestamp.

## Strategy download

`DirectHttpDownloadStrategy` provides a second direct-download abstraction used
by provider pipeline code. It applies non-secret headers, writes a `.part` file,
uses atomic move when supported and returns `ResolvedDownload` including
requested/resolved URI, local path, size, content type and completion time.

`HtmlLinkDiscoveryStrategy` resolves a file URI from a static landing page and
then uses the direct strategy.

## Security constraints

- Use HTTPS for production endpoints.
- Do not put secrets in configured headers or query parameters.
- Apply bounded download sizes where the selected abstraction supports them.
- Treat the final file as untrusted until provider validation completes.
- Keep working directories inaccessible to untrusted writers.
- Delete or quarantine failed partial content.

## Model versus executable support

The configuration model names direct HTTP, authenticated API, HTML link
discovery, HTML table and browser automation strategies. The shared executable
implementation currently covers direct HTTP and static HTML link discovery.
