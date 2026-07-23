/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
 */
public record LinkDiscoveryRequest(
        URI pageUri,
        Set<String> allowedExtensions,
        List<String> requiredTerms,
        List<String> excludedTerms,
        Pattern hrefPattern,
        Pattern textPattern) {

    public LinkDiscoveryRequest {
        Objects.requireNonNull(pageUri, "pageUri");
        allowedExtensions = normalizeExtensions(allowedExtensions);
        requiredTerms = normalizeTerms(requiredTerms);
        excludedTerms = normalizeTerms(excludedTerms);
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
        String searchable = link.searchableText();

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
        String descriptiveText = (link.linkText() + " " + link.title()).trim();
        return textPattern == null || textPattern.matcher(descriptiveText).find();
    }

    private static Set<String> normalizeExtensions(Set<String> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return Set.of();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String extension : extensions) {
            if (extension != null && !extension.isBlank()) {
                normalized.add(extension.trim()
                        .replaceFirst("^\\.", "")
                        .toLowerCase(Locale.ROOT));
            }
        }
        return Set.copyOf(normalized);
    }

    private static List<String> normalizeTerms(List<String> terms) {
        if (terms == null || terms.isEmpty()) {
            return List.of();
        }
        return terms.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(term -> !term.isEmpty())
                .map(term -> term.toLowerCase(Locale.ROOT))
                .distinct()
                .toList();
    }
}
