package com.towermarsh.opendata.download.strategy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import com.towermarsh.opendata.exception.DownloadException;

/**
 * Streams a remote HTTP resource to a local file.
 *
 * <p>The response is written to a temporary part file and moved into place only
 * after a successful 2xx response. Interrupted requests preserve the thread's
 * interrupt status.</p>
 */
public final class DirectHttpDownloadStrategy {

    private final HttpClient httpClient;

    /**
     * Creates a downloader that follows normal redirects.
     *
     * @param connectTimeout connection timeout
     */
    public DirectHttpDownloadStrategy(
            final Duration connectTimeout) {

        Objects.requireNonNull(connectTimeout, "connectTimeout");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Downloads a URI.
     *
     * @param requestedUri remote URI
     * @param destination final destination file
     * @param headers non-secret HTTP headers
     * @param requestTimeout request timeout
     * @return download result
     * @throws com.towermarsh.opendata.exception.DownloadException
     */
    public ResolvedDownload download(
            final URI requestedUri,
            final Path destination,
            final Map<String, String> headers,
            final Duration requestTimeout) throws DownloadException {

        Objects.requireNonNull(requestedUri, "requestedUri");
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(headers, "headers");
        Objects.requireNonNull(requestTimeout, "requestTimeout");

        final Path absoluteDestination =
                destination.toAbsolutePath().normalize();
        final Path parent = absoluteDestination.getParent();
        final Path partFile =
                absoluteDestination.resolveSibling(
                        absoluteDestination.getFileName() + ".part");

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.deleteIfExists(partFile);

            final HttpRequest.Builder requestBuilder =
                    HttpRequest.newBuilder(requestedUri)
                            .GET()
                            .timeout(requestTimeout);

            headers.forEach(requestBuilder::header);

            final HttpResponse<Path> response = httpClient.send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofFile(partFile));

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                Files.deleteIfExists(partFile);
                throw new DownloadException(
                        "HTTP request returned status %d for %s."
                                .formatted(
                                        response.statusCode(),
                                        requestedUri));
            }

            moveCompletedFile(partFile, absoluteDestination);

            return new ResolvedDownload(
                    requestedUri,
                    response.uri(),
                    absoluteDestination,
                    Files.size(absoluteDestination),
                    response.headers()
                            .firstValue("Content-Type"),
                    Instant.now());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            deleteQuietly(partFile);
            throw new DownloadException(
                    "HTTP download was interrupted: " + requestedUri,
                    exception);
        } catch (IOException exception) {
            deleteQuietly(partFile);
            throw new DownloadException(
                    "Unable to download: " + requestedUri,
                    exception);
        }
    }

    private static void moveCompletedFile(
            final Path partFile,
            final Path destination) throws IOException {

        try {
            Files.move(
                    partFile,
                    destination,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    partFile,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteQuietly(final Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
            // Preserve the original failure.
        }
    }
}
