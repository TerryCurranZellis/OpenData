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

/**
 * Discovers, filters and reads local Octopus Energy statement PDFs.
 * 
 * @author Terry Curran
 * @version 2.0.0
 */
public final class OctopusExtract {

    private static final Logger LOGGER = Logger.getLogger(OctopusExtract.class.getName());
    
    /**
     * look for statements matching pattern
     */
    private static final Pattern FILE_PATTERN = Pattern.compile(
            "^octopus-energy-statement-(\\d{4}-\\d{2}-\\d{2})\\.pdf$",
            Pattern.CASE_INSENSITIVE);

    /**
     * Reads all new statement files as one extraction batch. A file is
     * considered already processed only when both its name and SHA-256 hash
     * match a completed row in {@code octopus.statement_file}.
     *
     * @param configuration plugin configuration
     * @param context current settings
     * @return list os statement records
     * @throws java.io.IOException
     */
    public List<ExtractedOctopusStatement> extract(
            final OctopusConfiguration configuration,
            final PluginExecutionContext context) throws IOException {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(context, "context");
        final var inputDirectory = configuration.inputDirectory();
        if (!Files.isDirectory(inputDirectory)) {
            throw new IOException("Octopus input directory does not exist or is not a directory: " + inputDirectory);
        }

        final var processed = context.dryRun()
                ? Set.of()
                : new OctopusProcessedFileRepository(context.database()).findProcessedFileKeys();
        final List<Path> candidates;
        try (var files = Files.list(inputDirectory)) {
            candidates = files.filter(Files::isRegularFile)
                    .filter(path -> FILE_PATTERN.matcher(fileName(path)).matches())
                    .sorted(Comparator.comparing(OctopusExtract::statementDate)
                            .thenComparing(OctopusExtract::fileName))
                    .toList();
        }

        final List<ExtractedOctopusStatement> extracted = new ArrayList<>();
        var skipped = 0;
        for (var path : candidates) {
            final var candidateFileName = fileName(path);
            final var hash = sha256(path);
            if (processed.contains(OctopusProcessedFileRepository.key(candidateFileName, hash))) {
                skipped++;
                continue;
            }
            extracted.add(new ExtractedOctopusStatement(
                    path,
                    candidateFileName,
                    statementDate(path),
                    hash,
                    Files.size(path),
                    PdfTextExtractor.extract(path)));
        }
        final var skippedCount = skipped;
        LOGGER.info(() -> "Octopus extract: discovered %d matching PDF(s), selected %d new/changed file(s), skipped %d completed file(s)"
                .formatted(candidates.size(), extracted.size(), skippedCount));
        return List.copyOf(extracted);
    }

    /**
     * Find the statement date
     * @param path path to file
     * @return  the statement date
     */
    static LocalDate statementDate(final Path path) {
        final var matcher = FILE_PATTERN.matcher(fileName(path));
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Octopus statement filename: " + fileName(path));
        }
        try {
            return LocalDate.parse(matcher.group(1));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid statement date in filename: " + fileName(path), exception);
        }
    }

    private static String fileName(final Path path) {
        final var fileName = Objects.requireNonNull(path, "path").getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("Path must include a file name: " + path);
        }
        return fileName.toString();
    }

    /**
     * calculate the file hash
     * @param path file to has
     * @return the file hash
     * @throws IOException 
     */
    private static String sha256(final Path path) throws IOException {
        try {
            final var digest = MessageDigest.getInstance("SHA-256");
            try (var input = Files.newInputStream(path)) {
                final var buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) >= 0) {
                    if (count > 0) {
                        digest.update(buffer, 0, count);
                    }
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
