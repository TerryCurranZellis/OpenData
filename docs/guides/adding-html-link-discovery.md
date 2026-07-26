# Adding HTML Link Discovery

**Document ID:** GUIDE-HTML-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


Prefer API/direct file. Configure stable page, narrow selector, href and text
patterns, relative URL resolution and deterministic tests.

`HtmlLinkResolver` fails on no match and chooses the configured first or last
match when several links match. Narrow the rules enough that this choice is
safe. Use `HighestScoringLinkSelector` when scored candidates and tie rejection
are required. Browser automation is modelled but not implemented and should not
be selected for static HTML.
