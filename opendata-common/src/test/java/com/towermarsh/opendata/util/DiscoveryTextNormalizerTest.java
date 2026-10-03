/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Tests shared discovery-text normalization helpers.
 *
 * @author Terry Curran
 * @version 3.3.0
 */
class DiscoveryTextNormalizerTest {

    @Test
    void normalizesExtensionsTermsAndFreeText() {
        assertEquals(Set.of("csv", "xlsx"),
                DiscoveryTextNormalizer.normalizeExtensions(Set.of(".CSV", " xlsx ", ".csv")));
        assertEquals(List.of("energy cap", "2026"),
                DiscoveryTextNormalizer.normalizeTerms(List.of(" Energy Cap ", "2026", "energy-cap")));
        assertEquals("octopus energy statement 2026",
                DiscoveryTextNormalizer.normalizeFreeText(" Octopus_Energy Statement: 2026 "));
    }
}
