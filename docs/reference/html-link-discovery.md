# HTML Link Discovery Reference

**Document ID:** REF-HTML-001  
**Version:** 3.0.0  
**Status:** Static HTML discovery implemented  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

`HtmlLinkDiscoveryStrategy` downloads a landing page with Java `HttpClient`,
then delegates deterministic parsing to `HtmlLinkResolver`. The resolved file is
downloaded by `DirectHttpDownloadStrategy`.

`HtmlLinkResolver`:

1. parses HTML using Jsoup and the landing-page base URI;
2. selects elements using the configured CSS selector;
3. matches the raw `href` against a regular expression;
4. optionally matches visible link text;
5. resolves relative links; and
6. chooses the first or last match.

No match is a `DownloadException`. The resolver does not reject multiple matches
unless configuration makes first/last selection safe.

The separate `JsoupHtmlLinkDiscoverer` and `HighestScoringLinkSelector`
abstractions support scored candidate selection for callers that need it.

Browser automation and HTML-table strategy values exist in the configuration
model but do not have an executable shared implementation in this baseline.
