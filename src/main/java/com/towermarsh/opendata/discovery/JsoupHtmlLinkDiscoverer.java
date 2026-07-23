/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import com.towermarsh.opendata.exception.DiscoveryException;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Jsoup implementation of HTML link discovery.
 */
public final class JsoupHtmlLinkDiscoverer implements HtmlLinkDiscoverer {

    private static final Logger LOGGER = Logger.getLogger(
            JsoupHtmlLinkDiscoverer.class.getName());
    private static final String DEFAULT_USER_AGENT =
            "OpenData/1.0 (+https://github.com/TerryCurranZellis/OpenData)";

    private final Duration timeout;
    private final String userAgent;

    /**
     * Creates a discoverer with a 30-second timeout.
     */
    public JsoupHtmlLinkDiscoverer() {
        this(Duration.ofSeconds(30), DEFAULT_USER_AGENT);
    }

    /**
     * Creates a configured discoverer.
     *
     * @param timeout request timeout
     * @param userAgent HTTP user agent
     */
    public JsoupHtmlLinkDiscoverer(Duration timeout, String userAgent) {
        this.timeout = Objects.requireNonNull(timeout, "timeout");
        this.userAgent = Objects.requireNonNull(userAgent, "userAgent");
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
    }

    @Override
    public List<DiscoveredLink> discover(LinkDiscoveryRequest request)
            throws DiscoveryException {
        Objects.requireNonNull(request, "request");
        try {
            LOGGER.log(Level.INFO, "Discovering file links on {0}", request.pageUri());
            Document document = Jsoup.connect(request.pageUri().toString())
                    .userAgent(userAgent)
                    .timeout(Math.toIntExact(timeout.toMillis()))
                    .followRedirects(true)
                    .get();
            return discoverDocument(document, request);
        } catch (IOException | ArithmeticException ex) {
            throw new DiscoveryException(
                    "Unable to discover links on " + request.pageUri(), ex);
        }
    }

    /**
     * Parses supplied HTML. This method is also useful for repeatable tests and
     * for callers that have already downloaded the source page.
     *
     * @param pageUri base URI used for resolving relative links
     * @param html HTML content
     * @param request discovery criteria
     * @return accepted links
     * @throws DiscoveryException if the HTML cannot be parsed
     */
    public List<DiscoveredLink> discoverHtml(
            URI pageUri,
            String html,
            LinkDiscoveryRequest request) throws DiscoveryException {
        Objects.requireNonNull(pageUri, "pageUri");
        Objects.requireNonNull(html, "html");
        Objects.requireNonNull(request, "request");
        try {
            Document document = Jsoup.parse(html, pageUri.toString());
            return discoverDocument(document, request);
        } catch (RuntimeException ex) {
            throw new DiscoveryException("Unable to parse HTML for " + pageUri, ex);
        }
    }

    private List<DiscoveredLink> discoverDocument(
            Document document,
            LinkDiscoveryRequest request) {
        Map<URI, DiscoveredLink> uniqueLinks = new LinkedHashMap<>();

        for (Element anchor : document.select("a[href]")) {
            String absoluteHref = anchor.absUrl("href");
            if (absoluteHref.isBlank()) {
                continue;
            }

            URI target;
            try {
                target = URI.create(absoluteHref);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.FINE, "Ignoring invalid link: {0}", absoluteHref);
                continue;
            }

            String fileName = fileName(target);
            String extension = extension(fileName);
            DiscoveredLink link = new DiscoveredLink(
                    request.pageUri(),
                    target,
                    anchor.text(),
                    anchor.attr("title"),
                    fileName,
                    extension);

            if (request.matches(link)) {
                uniqueLinks.putIfAbsent(target, link);
            }
        }

        List<DiscoveredLink> results = List.copyOf(uniqueLinks.values());
        LOGGER.log(Level.INFO, "Discovered {0} matching link(s)", results.size());
        return results;
    }

    private static String fileName(URI uri) {
        String path = Objects.requireNonNullElse(uri.getPath(), "");
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private static String extension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
