/*
 * Filename: HtmlLinkResolver.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
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

/**
 * Resolves a downloadable link from an already-downloaded HTML document.
 *
 * <p>Network access is deliberately separate from HTML parsing so link
 * matching can be tested deterministically.</p>
  *
 * @author Terry Curran
 * @version 17 July 2026
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

        final Pattern hrefPattern =
                Pattern.compile(definition.hrefPattern());
        final Pattern textPattern =
                definition.textPattern().isBlank()
                        ? null
                        : Pattern.compile(definition.textPattern());

        final List<URI> matches = new ArrayList<>();
        final var document =
                Jsoup.parse(html, landingPageUri.toString());

        for (Element element :
                document.select(definition.cssSelector())) {

            final String href = element.attr("href").trim();
            if (href.isEmpty()
                    || !hrefPattern.matcher(href).matches()) {
                continue;
            }

            final String linkText = element.text().trim();
            if (textPattern != null
                    && !textPattern.matcher(linkText).matches()) {
                continue;
            }

            final String absolute = element.absUrl("href");
            final URI resolved = absolute.isBlank()
                    ? landingPageUri.resolve(href)
                    : URI.create(absolute);

            matches.add(resolved);
        }

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
