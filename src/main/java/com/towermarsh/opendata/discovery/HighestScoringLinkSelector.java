/*
 * (c) Copyright 2026 Terry Curran
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
 * <p>Filename matches receive more weight than descriptive-text matches. A tie
 * is rejected by default so that a plugin cannot silently download an arbitrary
 * file after a publisher changes its page.</p>
 */
public final class HighestScoringLinkSelector implements DiscoveredLinkSelector {

    private final boolean failOnTie;

    public HighestScoringLinkSelector() {
        this(true);
    }

    public HighestScoringLinkSelector(boolean failOnTie) {
        this.failOnTie = failOnTie;
    }

    @Override
    public DiscoveredLink select(
            List<DiscoveredLink> candidates,
            List<String> preferredTerms) throws DiscoveryException {
        Objects.requireNonNull(candidates, "candidates");
        List<String> terms = normalize(preferredTerms);
        if (candidates.isEmpty()) {
            throw new DiscoveryException("No candidate data links were discovered");
        }

        List<ScoredLink> scored = candidates.stream()
                .map(link -> new ScoredLink(link, score(link, terms)))
                .sorted(Comparator.comparingInt(ScoredLink::score).reversed()
                        .thenComparing(item -> item.link().targetUri().toString()))
                .toList();

        ScoredLink best = scored.get(0);
        if (failOnTie && scored.size() > 1 && scored.get(1).score() == best.score()) {
            throw new DiscoveryException(
                    "More than one candidate link has the best score of "
                    + best.score() + ": " + best.link().targetUri()
                    + " and " + scored.get(1).link().targetUri());
        }
        return best.link();
    }

    private static int score(DiscoveredLink link, List<String> terms) {
        String fileName = link.fileName().toLowerCase(Locale.ROOT);
        String descriptive = (link.linkText() + " " + link.title())
                .toLowerCase(Locale.ROOT);
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

    private static List<String> normalize(List<String> terms) {
        if (terms == null) {
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

    private record ScoredLink(DiscoveredLink link, int score) {
    }
}
