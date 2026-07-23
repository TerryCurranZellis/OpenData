/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import java.net.URI;
import java.util.Locale;
import java.util.Objects;

/**
 * A resolved hyperlink discovered on a source web page.
 *
 * @param pageUri page on which the link was found
 * @param targetUri absolute target URI
 * @param linkText visible anchor text
 * @param title anchor title attribute
 * @param fileName final path segment, when available
 * @param extension lower-case filename extension without a leading dot
 */
public record DiscoveredLink(
        URI pageUri,
        URI targetUri,
        String linkText,
        String title,
        String fileName,
        String extension) {

    public DiscoveredLink {
        Objects.requireNonNull(pageUri, "pageUri");
        Objects.requireNonNull(targetUri, "targetUri");
        linkText = Objects.requireNonNullElse(linkText, "").trim();
        title = Objects.requireNonNullElse(title, "").trim();
        fileName = Objects.requireNonNullElse(fileName, "").trim();
        extension = Objects.requireNonNullElse(extension, "")
                .replaceFirst("^\\.", "")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Returns searchable lower-case text assembled from the link metadata.
     *
     * @return combined searchable text
     */
    public String searchableText() {
        return String.join(" ", linkText, title, fileName, targetUri.toString())
                .toLowerCase(Locale.ROOT);
    }
}
