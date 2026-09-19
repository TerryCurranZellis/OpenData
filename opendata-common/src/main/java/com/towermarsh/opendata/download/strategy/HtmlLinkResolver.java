/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.download.strategy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import com.towermarsh.opendata.config.model.LinkDiscoveryDefinition;
import com.towermarsh.opendata.exception.DownloadException;

/**
 * Resolves a downloadable link from an already-downloaded HTML document.
 *
 * <p>
 * Network access is deliberately separate from HTML parsing so link matching
 * can be tested deterministically.</p>
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public class HtmlLinkResolver {

    /**
     * Resolves one matching link.
     *
     * @param landingPageUri base URI used for relative links
     * @param html HTML document
     * @param definition configured discovery rules
     * @return absolute downloadable URI
     * @throws com.towermarsh.opendata.exception.DownloadException
     */
    public URI resolve(
            URI landingPageUri,
            String html,
            LinkDiscoveryDefinition definition) throws DownloadException {

        Objects.requireNonNull(landingPageUri, "landingPageUri");
        Objects.requireNonNull(html, "html");
        Objects.requireNonNull(definition, "definition");

        var hrefPattern
                = Pattern.compile(definition.hrefPattern());
        var textPattern
                = definition.textPattern().isBlank()
                ? null
                : Pattern.compile(definition.textPattern());

        List<URI> matches = new ArrayList<>();
        var document
                = Jsoup.parse(html, landingPageUri.toString());

        document.select(definition.cssSelector()).forEach((var element) -> {
            var href = element.attr("href").trim();
            if (!(href.isEmpty()
                    || !hrefPattern.matcher(href).matches())) {
                var linkText = element.text().trim();
                if (!(textPattern != null
                        && !textPattern.matcher(linkText).matches())) {
                    var absolute = element.absUrl("href");
                    var resolved = absolute.isBlank()
                            ? landingPageUri.resolve(href)
                            : URI.create(absolute);

                    matches.add(resolved);
                }
            }
        });

        if (matches.isEmpty()) {
            throw new DownloadException(
                    "No downloadable link matched the configured HTML "
                    + "discovery rules at " + landingPageUri);
        }

        return definition.selectLastMatchingLink()
                ? matches.get(matches.size() - 1)
                : matches.get(0);
    }
}
