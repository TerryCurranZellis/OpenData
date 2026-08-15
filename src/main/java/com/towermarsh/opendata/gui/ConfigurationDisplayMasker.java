/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Converts configuration maps into safe read-only GUI values.
 *
 * <p>Plugin property definitions can explicitly mark a value as sensitive with
 * a companion {@code .sensitive=true} property. Conventional secret-bearing
 * property names are also masked defensively.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
final class ConfigurationDisplayMasker {

    static final String MASKED_VALUE = "********";

    private ConfigurationDisplayMasker() {
    }

    static List<ConfigurationDisplayEntry> entries(final Map<String, String> values) {
        final var source = Map.copyOf(Objects.requireNonNull(values, "values"));
        return source.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .map(entry -> new ConfigurationDisplayEntry(
                entry.getKey(),
                shouldMask(entry.getKey(), source)
                        ? MASKED_VALUE
                        : Objects.toString(entry.getValue(), "")))
                .toList();
    }

    static boolean shouldMask(final String key, final Map<String, String> values) {
        final var normalised = Objects.requireNonNull(key, "key")
                .trim().toLowerCase(Locale.ROOT);

        if (normalised.contains("password")
                || normalised.contains("passwd")
                || normalised.contains("secret")
                || normalised.contains("token")
                || normalised.contains("credential")
                || normalised.contains("api-key")
                || normalised.contains("api_key")
                || normalised.contains("apikey")) {
            return true;
        }

        if (normalised.endsWith(".value")) {
            final var sensitiveKey = normalised.substring(0,
                    normalised.length() - ".value".length()) + ".sensitive";
            return values.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(sensitiveKey))
                    .map(Map.Entry::getValue)
                    .filter(Objects::nonNull)
                    .anyMatch(value -> Boolean.parseBoolean(value.trim()));
        }
        return false;
    }
}
