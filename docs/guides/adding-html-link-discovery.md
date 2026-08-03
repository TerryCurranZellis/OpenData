# Adding HTML Link Discovery

**Document ID:** GUIDE-HTML-001  
**Version:** 2.0  
**Status:** Version 2.0.0 developer procedure  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

Prefer a stable API or direct file URI. Use HTML discovery only when the
publisher exposes a changing file link from a stable landing page.

## Configuration

A landing-page endpoint uses:

```properties
endpoint.source.type=LANDING_PAGE
endpoint.source.url=https://example.invalid/downloads
endpoint.source.method=GET
endpoint.source.format=HTML
endpoint.source.strategy=HTML_LINK_DISCOVERY
endpoint.source.link-discovery.css-selector=a[href]
endpoint.source.link-discovery.href-pattern=(?i).*\.xlsx$
endpoint.source.link-discovery.text-pattern=(?i).*latest.*
endpoint.source.link-discovery.select-last=false
```

`HtmlLinkResolver` applies the CSS selector, matches the raw `href`, optionally
matches visible text, resolves relative links and returns the first or last
matching URI. No match is a download failure.

## Safety and maintainability

- Use the narrowest stable CSS selector.
- Anchor regular expressions where practical.
- Avoid relying solely on page order.
- Reject ambiguous matches when the publisher page can contain multiple valid
  files; the separate scoring abstractions may be more appropriate.
- Test relative, absolute, redirected, missing and multiple links with local
  HTML fixtures.

Browser automation, authenticated API download and HTML-table extraction are
represented in configuration enums but do not have an executable shared
strategy in the current baseline. Do not select them merely because the enum
value exists.
