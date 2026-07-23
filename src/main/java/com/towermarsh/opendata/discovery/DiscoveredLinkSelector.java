/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import com.towermarsh.opendata.exception.DiscoveryException;
import java.util.List;

/**
 * Selects one download link from a set of discovered candidates.
 */
public interface DiscoveredLinkSelector {

    /**
     * Selects one candidate.
     *
     * @param candidates discovered candidates
     * @param preferredTerms terms used to favour the intended dataset
     * @return selected candidate
     * @throws DiscoveryException if selection is unsafe or impossible
     */
    DiscoveredLink select(
            List<DiscoveredLink> candidates,
            List<String> preferredTerms) throws DiscoveryException;
}
