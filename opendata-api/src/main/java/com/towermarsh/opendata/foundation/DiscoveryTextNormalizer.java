/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.foundation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Normalises discovery text for case-insensitive matching and filtering.
 *
 * @author Terry Curran
 * @version 4.1.0
 * @since 4.1.0
 */
public final class DiscoveryTextNormalizer {

    private DiscoveryTextNormalizer() {
        // Utility class.
    }

    /**
     * Normalises free text for token-based matching.
     *
     * @param value text to normalise
     * @return normalised text, or an empty string when the value is null or
     * blank
     */
    public static String normalizeFreeText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    /**
     * Normalises preferred or excluded search terms.
     *
     * @param terms raw search terms
     * @return immutable distinct lower-case terms
     */
    public static List<String> normalizeTerms(List<String> terms) {
        if (terms == null || terms.isEmpty()) {
            return List.of();
        }
        return terms.stream()
                .filter(Objects::nonNull)
                .map(DiscoveryTextNormalizer::normalizeFreeText)
                .filter(term -> !term.isEmpty())
                .distinct()
                .toList();
    }

    /**
     * Normalises filename extensions used by HTML link discovery.
     *
     * @param extensions raw extension values
     * @return immutable distinct lower-case extensions without leading dots
     */
    public static Set<String> normalizeExtensions(Set<String> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return Set.of();
        }
        var normalized = new LinkedHashSet<String>();
        for (var extension : extensions) {
            if (extension != null && !extension.isBlank()) {
                normalized.add(extension.trim()
                        .replaceFirst("^\\.", "")
                        .toLowerCase(Locale.ROOT));
            }
        }
        return Set.copyOf(normalized);
    }
}
