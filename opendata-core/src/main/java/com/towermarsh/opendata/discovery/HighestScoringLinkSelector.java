/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import com.towermarsh.opendata.util.DiscoveryTextNormalizer;
import com.towermarsh.opendata.exception.DiscoveryException;
import java.util.Comparator;
import java.util.List;
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
 * @version 3.3.0
 */
public class HighestScoringLinkSelector implements DiscoveredLinkSelector {

    private final boolean failOnTie;

    /**
     * Creates a selector that fails when more than one candidate shares the
     * best score.
     */
    public HighestScoringLinkSelector() {
        this(true);
    }

    /**
     * Creates a selector with configurable tie handling.
     *
     * @param failOnTie whether equal best scores should be rejected
     */
    public HighestScoringLinkSelector(final boolean failOnTie) {
        this.failOnTie = failOnTie;
    }

    /**
     * Selects the highest-scoring discovered link.
     *
     * @param candidates discovered links to score
     * @param preferredTerms preferred terms used during scoring
     * @return selected discovered link
     * @throws DiscoveryException if no candidates are available or the best
     * score is tied
     */
    @Override
    public DiscoveredLink select(
            final List<DiscoveredLink> candidates,
            final List<String> preferredTerms) throws DiscoveryException {
        Objects.requireNonNull(candidates, "candidates");
        var terms = DiscoveryTextNormalizer.normalizeTerms(preferredTerms);
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
    private static int score(final DiscoveredLink link, final List<String> terms) {
        var fileName = DiscoveryTextNormalizer.normalizeFreeText(link.fileName());
        var descriptive = DiscoveryTextNormalizer.normalizeFreeText(link.linkText() + " " + link.title());
        var score = "https".equalsIgnoreCase(link.targetUri().getScheme()) ? 1 : 0;
        for (var term : terms) {
            if (fileName.contains(term)) {
                score += 5;
            }
            if (descriptive.contains(term)) {
                score += 2;
            }
        }
        return score;
    }

    private record ScoredLink(DiscoveredLink link, int score) {

    }
}
