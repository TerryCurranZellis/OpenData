/*
 * Filename: HighestScoringLinkSelectorTest.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.exception.DiscoveryException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
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
