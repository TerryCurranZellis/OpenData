/*
 * Filename: HtmlLinkResolverTest.java
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
package com.towermarsh.opendata.download.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.junit.jupiter.api.Test;

import com.towermarsh.opendata.config.model.LinkDiscoveryDefinition;
import com.towermarsh.opendata.exception.DownloadException;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class HtmlLinkResolverTest {

    @Test
    void resolvesRelativeWorkbookLinkUsingTextAndHrefPatterns() throws DownloadException {
        final String html = """
                <html><body>
                  <a href="/files/notes.pdf">Notes</a>
                  <a href="/files/cap-rates.xlsx">
                    Final levelised cap rates model
                  </a>
                </body></html>
                """;

        final var definition = new LinkDiscoveryDefinition(
                "a[href]",
                "(?i).*\\.xlsx$",
                "(?i).*final levelised cap rates model.*",
                false);

        final URI resolved = new HtmlLinkResolver().resolve(
                URI.create("https://example.org/publications/current"),
                html,
                definition);

        assertEquals(
                URI.create("https://example.org/files/cap-rates.xlsx"),
                resolved);
    }

    @Test
    void failsClearlyWhenNoLinkMatches() {
        final var definition = new LinkDiscoveryDefinition(
                "a[href]",
                "(?i).*\\.xlsx$",
                "",
                false);

        assertThrows(
                DownloadException.class,
                () -> new HtmlLinkResolver().resolve(
                        URI.create("https://example.org/"),
                        "<a href='file.csv'>CSV</a>",
                        definition));
    }
}
