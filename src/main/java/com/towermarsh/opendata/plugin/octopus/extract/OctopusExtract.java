/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** Discovers, filters and reads local Octopus Energy statement PDFs. */
public final class OctopusExtract {
    private static final Logger LOGGER = Logger.getLogger(OctopusExtract.class.getName());
    private static final Pattern FILE_PATTERN = Pattern.compile(
            "^octopus-energy-statement-(\\d{4}-\\d{2}-\\d{2})\\.pdf$",
            Pattern.CASE_INSENSITIVE);

    /**
     * Reads all new statement files as one extraction batch.
     * A file is considered already processed only when both its name and SHA-256
     * hash match a completed row in {@code octopus.statement_file}.
     */
    public List<ExtractedOctopusStatement> extract(
            final OctopusConfiguration configuration,
            final PluginExecutionContext context) throws IOException {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(context, "context");
        final Path inputDirectory = configuration.inputDirectory();
        if (!Files.isDirectory(inputDirectory)) {
            throw new IOException("Octopus input directory does not exist or is not a directory: " + inputDirectory);
        }

        final Set<String> processed = context.dryRun()
                ? new OctopusProcessedFileRepository(context.database()).findProcessedFileKeys()
                : new OctopusProcessedFileRepository(context.database()).findProcessedFileKeys();
        final List<Path> candidates;
        try (Stream<Path> files = Files.list(inputDirectory)) {
            candidates = files.filter(Files::isRegularFile)
                    .filter(path -> FILE_PATTERN.matcher(path.getFileName().toString()).matches())
                    .sorted(Comparator.comparing(OctopusExtract::statementDate)
                            .thenComparing(path -> path.getFileName().toString()))
                    .toList();
        }

        final List<ExtractedOctopusStatement> extracted = new ArrayList<>();
        int skipped = 0;
        for (Path path : candidates) {
            final String fileName = path.getFileName().toString();
            final String hash = sha256(path);
            if (processed.contains(OctopusProcessedFileRepository.key(fileName, hash))) {
                skipped++;
                continue;
            }
            extracted.add(new ExtractedOctopusStatement(
                    path,
                    fileName,
                    statementDate(path),
                    hash,
                    Files.size(path),
                    PdfTextExtractor.extract(path)));
        }
        final int skippedCount = skipped;
        LOGGER.info(() -> "Octopus extract: discovered %d matching PDF(s), selected %d new/changed file(s), skipped %d completed file(s)"
                .formatted(candidates.size(), extracted.size(), skippedCount));
        return List.copyOf(extracted);
    }

    static LocalDate statementDate(final Path path) {
        final Matcher matcher = FILE_PATTERN.matcher(path.getFileName().toString());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Octopus statement filename: " + path.getFileName());
        }
        try {
            return LocalDate.parse(matcher.group(1));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid statement date in filename: " + path.getFileName(), exception);
        }
    }

    private static String sha256(final Path path) throws IOException {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = Files.newInputStream(path)) {
                final byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) >= 0) {
                    if (count > 0) digest.update(buffer, 0, count);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
