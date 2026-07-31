/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import com.towermarsh.opendata.exception.DiscoveryException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Selects the candidate that best matches configured preferred terms.
 *
 * <p>
 * Filename matches receive more weight than descriptive-text matches. A tie is
 * rejected by default so that a plugin cannot silently download an arbitrary
 * file after a publisher changes its page.</p>
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class HighestScoringLinkSelector implements DiscoveredLinkSelector {

    private final boolean failOnTie;

    /**
     *
     * Creates a selector that fails when more than one candidate shares the
     * best score.
     *
     */
    public HighestScoringLinkSelector() {
        this(true);
    }

    /**
     * Creates a selector with configurable tie handling.
     *
     * @param failOnTie whether equal best scores should be rejected
     *
     */
    public HighestScoringLinkSelector(boolean failOnTie) {
        this.failOnTie = failOnTie;
    }

    @Override
    /**
     * Selects the highest-scoring discovered link.
     *
     * @param candidates discovered links to score
     * @param preferredTerms preferred terms used during scoring
     * @return selected discovered link
     * @throws DiscoveryException if no candidates are available or the best
     * score is tied
     */
    public DiscoveredLink select(
            List<DiscoveredLink> candidates,
            List<String> preferredTerms) throws DiscoveryException {
        Objects.requireNonNull(candidates, "candidates");
        List<String> terms = normalize(preferredTerms);
        if (candidates.isEmpty()) {
            throw new DiscoveryException("No candidate data links were discovered");
        }

        var scored = candidates.stream()
                .map(link -> new ScoredLink(link, score(link, terms)))
                .sorted(Comparator.comparingInt(ScoredLink::score).reversed()
                        .thenComparing(item -> item.link().targetUri().toString()))
                .toList();

        var best = scored.get(0);
        if (failOnTie && scored.size() > 1 && scored.get(1).score() == best.score()) {
            throw new DiscoveryException(
                    "More than one candidate link has the best score of "
                    + best.score() + ": " + best.link().targetUri()
                    + " and " + scored.get(1).link().targetUri());
        }
        return best.link();
    }

    /**
     * Computes a score for one discovered link.
     *
     * @param link discovered link to score
     * @param terms preferred search terms
     * @return computed score
     */
    private static int score(DiscoveredLink link, List<String> terms) {
        String fileName = normalizeText(link.fileName());
        String descriptive = normalizeText(link.linkText() + " " + link.title());
        int score = "https".equalsIgnoreCase(link.targetUri().getScheme()) ? 1 : 0;
        for (String term : terms) {
            if (fileName.contains(term)) {
                score += 5;
            }
            if (descriptive.contains(term)) {
                score += 2;
            }
        }
        return score;
    }

    /**
     * Normalises preferred search terms for case-insensitive matching.
     *
     * @param terms raw preferred terms
     * @return normalised terms
     */
    private static List<String> normalize(List<String> terms) {
        if (terms == null) {
            return List.of();
        }
        return terms.stream()
                .filter(Objects::nonNull)
                .map(HighestScoringLinkSelector::normalizeText)
                .map(String::trim)
                .filter(term -> !term.isEmpty())
                .distinct()
                .toList();
    }

    /**
     * Normalises link text for token-based matching.
     *
     * @param value text to normalise
     * @return normalised text
     */
    private static String normalizeText(final String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    private record ScoredLink(DiscoveredLink link, int score) {

    }
}
