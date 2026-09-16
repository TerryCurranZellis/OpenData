/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.download;

import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.model.DataFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Streaming HTTP implementation of {@link DataDownloader}.
 *
 * <p>
 * The response is written to a temporary sibling file and moved into place only
 * after the transfer has completed. This avoids leaving a partial dataset at
 * the final path.</p>
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class HttpDataDownloader implements DataDownloader {

    private static final Logger LOGGER = Logger.getLogger(
            HttpDataDownloader.class.getName());

    private final HttpClient client;
    private final HttpDownloadOptions options;

    /**
     *
     * Creates a downloader using default HTTP download options.
     *
     */
    public HttpDataDownloader() {
        this(HttpDownloadOptions.defaults());
    }

    /**
     *
     * Creates a downloader using the supplied HTTP options.
     *
     * @param options HTTP download options
     *
     */
    public HttpDataDownloader(HttpDownloadOptions options) {
        this.options = Objects.requireNonNull(options, "options");
        this.client = HttpClient.newBuilder()
                .connectTimeout(options.connectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public DataFile download(URI sourceUri, Path destination)
            throws DownloadException {
        Objects.requireNonNull(sourceUri, "sourceUri");
        Objects.requireNonNull(destination, "destination");

        var absoluteDestination = destination.toAbsolutePath().normalize();
        var parent = absoluteDestination.getParent();
        var temporary = absoluteDestination.resolveSibling(
                absoluteDestination.getFileName() + ".part");

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.exists(absoluteDestination) && !options.overwrite()) {
                throw new DownloadException(
                        "Destination already exists and overwrite is disabled: "
                        + absoluteDestination);
            }

            var request = HttpRequest.newBuilder(sourceUri)
                    .timeout(options.requestTimeout())
                    .header("Accept", "*/*")
                    .header("User-Agent", options.userAgent())
                    .GET()
                    .build();

            LOGGER.log(Level.INFO, "Downloading {0} to {1}",
                    new Object[]{sourceUri, absoluteDestination});
            HttpResponse<InputStream> response = client.send(
                    request, HttpResponse.BodyHandlers.ofInputStream());

            var status = response.statusCode();
            if (status < 200 || status >= 300) {
                try (var ignored = response.body()) {
                    // Close response body before reporting the error.
                }
                throw new DownloadException(
                        "HTTP download failed with status " + status
                        + " for " + sourceUri);
            }

            var declaredLength = response.headers()
                    .firstValueAsLong("Content-Length")
                    .orElse(-1L);
            enforceMaximum(declaredLength, sourceUri);

            long bytes;
            try (var input = response.body(); var output = Files.newOutputStream(
                    temporary,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                bytes = copyWithLimit(input, output, options.maximumBytes());
            }

            moveIntoPlace(temporary, absoluteDestination);
            LOGGER.log(Level.INFO, "Downloaded {0} byte(s) from {1}",
                    new Object[]{bytes, sourceUri});
            return new DataFile(absoluteDestination, bytes, LocalDateTime.now());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            deleteQuietly(temporary);
            throw new DownloadException("Download interrupted: " + sourceUri, ex);
        } catch (IOException ex) {
            deleteQuietly(temporary);
            throw new DownloadException("Unable to download file: " + sourceUri, ex);
        } catch (DownloadException ex) {
            deleteQuietly(temporary);
            throw ex;
        }
    }

    /**
     * Validates the declared remote content length against the configured
     * maximum.
     *
     * @param contentLength declared remote content length
     * @param sourceUri source URI being downloaded
     * @throws DownloadException if the declared length exceeds the configured
     * maximum
     */
    private void enforceMaximum(long contentLength, URI sourceUri)
            throws DownloadException {
        if (options.maximumBytes() > 0
                && contentLength > options.maximumBytes()) {
            throw new DownloadException(
                    "Remote file is larger than the configured maximum of "
                    + options.maximumBytes() + " bytes: " + sourceUri);
        }
    }

    /**
     * Copies a response stream while enforcing the configured maximum size.
     *
     * @param input source input stream
     * @param output destination output stream
     * @param maximumBytes maximum permitted size in bytes, or {@code 0} for no
     * limit
     * @return copied byte count
     * @throws IOException if stream copying fails
     * @throws DownloadException if the download exceeds the configured maximum
     */
    private static long copyWithLimit(
            InputStream input,
            OutputStream output,
            long maximumBytes) throws IOException, DownloadException {
        var buffer = new byte[64 * 1024];
        var total = 0L;
        int read;
        while ((read = input.read(buffer)) >= 0) {
            total += read;
            if (maximumBytes > 0 && total > maximumBytes) {
                throw new DownloadException(
                        "Downloaded content exceeded the configured maximum of "
                        + maximumBytes + " bytes");
            }
            output.write(buffer, 0, read);
        }
        return total;
    }

    /**
     * Moves a completed temporary file into its final destination.
     *
     * @param source temporary source file
     * @param destination final destination file
     * @throws IOException if the move fails
     */
    private static void moveIntoPlace(Path source, Path destination)
            throws IOException {
        try {
            Files.move(source, destination,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Deletes a temporary file without masking the primary failure.
     *
     * @param path temporary file path
     */
    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, "Unable to remove temporary file " + path, ex);
        }
    }
}
