package com.towermarsh.opendata.download.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.junit.jupiter.api.Test;

import com.towermarsh.opendata.config.model.LinkDiscoveryDefinition;
import com.towermarsh.opendata.exception.DownloadException;

class HtmlLinkResolverTest {

    @Test
    void resolvesRelativeWorkbookLinkUsingTextAndHrefPatterns() {
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
