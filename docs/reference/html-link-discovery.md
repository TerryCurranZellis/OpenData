# HTML Link Discovery Reference

**Document ID:** REF-HTML-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


`HtmlLinkDiscoveryStrategy` downloads the landing page, then
`HtmlLinkResolver` selects candidate elements with CSS, applies href and
optional text regular expressions, resolves relative URLs and chooses the first
or last match according to configuration. No match is a download failure.

The separate `JsoupHtmlLinkDiscoverer` and `HighestScoringLinkSelector`
contracts remain available for callers that need scored discovery; the current
Ofgem plugin uses the configured first/last resolver. Browser automation is
modelled but not implemented.
