/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

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
