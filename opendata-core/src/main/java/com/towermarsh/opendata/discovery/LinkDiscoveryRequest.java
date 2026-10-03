/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import com.towermarsh.opendata.util.DiscoveryTextNormalizer;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Immutable criteria used to discover candidate dataset links.
 *
 * @param pageUri HTML page to inspect
 * @param allowedExtensions accepted filename extensions
 * @param requiredTerms terms of which every entry must occur in link metadata
 * @param excludedTerms terms of which none may occur in link metadata
 * @param hrefPattern optional regular expression applied to the absolute URI
 * @param textPattern optional regular expression applied to link text and title
 *
 * @author Terry Curran
 * @version 3.3.0
 */
public record LinkDiscoveryRequest(
        URI pageUri,
        Set<String> allowedExtensions,
        List<String> requiredTerms,
        List<String> excludedTerms,
        Pattern hrefPattern,
        Pattern textPattern) {

    /**
     * Validates and normalises record components.
     *
     * @param pageUri HTML page to inspect
     * @param allowedExtensions accepted filename extensions
     * @param requiredTerms terms of which every entry must occur in link
     * metadata
     * @param excludedTerms terms of which none may occur in link metadata
     * @param hrefPattern optional regular expression applied to the absolute
     * URI
     * @param textPattern optional regular expression applied to link text and
     * title
     */
    public LinkDiscoveryRequest {
        Objects.requireNonNull(pageUri, "pageUri");
        allowedExtensions = DiscoveryTextNormalizer.normalizeExtensions(allowedExtensions);
        requiredTerms = DiscoveryTextNormalizer.normalizeTerms(requiredTerms);
        excludedTerms = DiscoveryTextNormalizer.normalizeTerms(excludedTerms);
    }

    @Override
    public Set<String> allowedExtensions() {
        return Set.copyOf(allowedExtensions);
    }

    @Override
    public List<String> requiredTerms() {
        return List.copyOf(requiredTerms);
    }

    @Override
    public List<String> excludedTerms() {
        return List.copyOf(excludedTerms);
    }

    /**
     * Creates a request for Excel and CSV files without text restrictions.
     *
     * @param pageUri source page
     * @return default request
     */
    public static LinkDiscoveryRequest tabularFiles(URI pageUri) {
        return new LinkDiscoveryRequest(
                pageUri,
                Set.of("csv", "xls", "xlsx"),
                List.of(),
                List.of(),
                null,
                null);
    }

    /**
     * Tests whether a candidate satisfies all configured criteria.
     *
     * @param link candidate link
     * @return {@code true} when the link is accepted
     */
    public boolean matches(DiscoveredLink link) {
        Objects.requireNonNull(link, "link");
        var searchable = link.searchableText();

        if (!allowedExtensions.isEmpty()
                && !allowedExtensions.contains(link.extension())) {
            return false;
        }
        if (requiredTerms.stream().anyMatch(term -> !searchable.contains(term))) {
            return false;
        }
        if (excludedTerms.stream().anyMatch(searchable::contains)) {
            return false;
        }
        if (hrefPattern != null
                && !hrefPattern.matcher(link.targetUri().toString()).find()) {
            return false;
        }
        var descriptiveText = (link.linkText() + " " + link.title()).trim();
        return textPattern == null || textPattern.matcher(descriptiveText).find();
    }
}
