package com.towermarsh.opendata.config.model;

import java.util.Objects;

/**
 * Rules for finding a downloadable file link on an HTML landing page.
 *
 * @param cssSelector CSS selector used to select candidate anchors
 * @param hrefPattern regular expression applied to the href
 * @param textPattern optional regular expression applied to link text
 * @param selectLastMatchingLink whether the final matching link is selected
 */
public record LinkDiscoveryDefinition(
        String cssSelector,
        String hrefPattern,
        String textPattern,
        boolean selectLastMatchingLink) {

    public LinkDiscoveryDefinition {
        Objects.requireNonNull(cssSelector, "cssSelector");
        Objects.requireNonNull(hrefPattern, "hrefPattern");
        textPattern = textPattern == null ? "" : textPattern;
    }
}
