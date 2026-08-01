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
import org.jsoup.nodes.Element;

import com.towermarsh.opendata.config.model.LinkDiscoveryDefinition;
import com.towermarsh.opendata.exception.DownloadException;
import java.util.function.Consumer;

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
public final class HtmlLinkResolver {

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
            final URI landingPageUri,
            final String html,
            final LinkDiscoveryDefinition definition) throws DownloadException {

        Objects.requireNonNull(landingPageUri, "landingPageUri");
        Objects.requireNonNull(html, "html");
        Objects.requireNonNull(definition, "definition");

        final var hrefPattern
                = Pattern.compile(definition.hrefPattern());
        final var textPattern
                = definition.textPattern().isBlank()
                ? null
                : Pattern.compile(definition.textPattern());

        final List<URI> matches = new ArrayList<>();
        final var document
                = Jsoup.parse(html, landingPageUri.toString());

        document.select(definition.cssSelector()).forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                final var href = element.attr("href").trim();
                if (!(href.isEmpty()
                        || !hrefPattern.matcher(href).matches())) {
                    final String linkText = element.text().trim();
                    if (!(textPattern != null
                            && !textPattern.matcher(linkText).matches())) {
                        final var absolute = element.absUrl("href");
                        final URI resolved = absolute.isBlank()
                                ? landingPageUri.resolve(href)
                                : URI.create(absolute);

                        matches.add(resolved);
                    }
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
