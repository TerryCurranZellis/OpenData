/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.exception.DiscoveryException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;

class HighestScoringLinkSelectorTest {

    @Test
    void favoursPreferredTermInFilename() throws Exception {
        DiscoveredLink general = link("general-data.xlsx", "Price cap table");
        DiscoveredLink annex = link("price-cap-annex.xlsx", "Download");

        DiscoveredLink selected = new HighestScoringLinkSelector()
                .select(List.of(general, annex), List.of("price cap"));

        assertEquals(annex, selected);
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsEqualBestScores() {
        DiscoveredLink first = link("one.xlsx", "Dataset");
        DiscoveredLink second = link("two.xlsx", "Dataset");

        assertThrows(DiscoveryException.class,
                () -> new HighestScoringLinkSelector()
                        .select(List.of(first, second), List.of("missing")));
    }

    private static DiscoveredLink link(String filename, String text) {
        URI page = URI.create("https://example.test/");
        URI target = page.resolve(filename);
        return new DiscoveredLink(page, target, text, "", filename, "xlsx");
    }
}
