/*
 * Filename: JsoupHtmlLinkDiscovererTest.java
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

import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class JsoupHtmlLinkDiscovererTest {

    @Test
    void discoversRelativeLinksAndAppliesTermsAndExtensions() throws Exception {
        URI page = URI.create("https://example.test/data/index.html");
        String html = """
                <html><body>
                  <a href="files/price-cap-current.xlsx" title="Current cap">Price cap data</a>
                  <a href="files/price-cap-notes.pdf">Notes</a>
                  <a href="files/price-cap-archive.xlsx">Archived price cap</a>
                </body></html>
                """;
        LinkDiscoveryRequest request = new LinkDiscoveryRequest(
                page,
                Set.of("xlsx"),
                List.of("price cap"),
                List.of("archive"),
                null,
                null);

        List<DiscoveredLink> links = new JsoupHtmlLinkDiscoverer()
                .discoverHtml(page, html, request);

        assertEquals(1, links.size());
        assertEquals(
                URI.create("https://example.test/data/files/price-cap-current.xlsx"),
                links.get(0).targetUri());
    }

    @Test
    void removesDuplicateTargets() throws Exception {
        URI page = URI.create("https://example.test/");
        String html = """
                <a href="/file.csv">First</a>
                <a href="/file.csv">Second</a>
                """;

        List<DiscoveredLink> links = new JsoupHtmlLinkDiscoverer()
                .discoverHtml(page, html, LinkDiscoveryRequest.tabularFiles(page));

        assertEquals(1, links.size());
    }
}
