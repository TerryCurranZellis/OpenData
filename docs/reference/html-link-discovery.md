# HTML Link Discovery Reference

HTML link discovery is used when a publisher provides a stable landing page but
changes the downloadable file URL for each release.

`HtmlLinkResolver` applies:

1. a CSS selector to locate candidate elements;
2. a regular expression to each `href`;
3. an optional regular expression to visible link text;
4. first- or last-match selection.

Relative links are resolved against the landing-page URI.

Browser automation is not used for static pages. It should be introduced only
where an underlying API or static HTML solution is unavailable.
