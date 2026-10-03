/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import com.towermarsh.opendata.util.PathDigestSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Discovers, filters and reads local Octopus Energy statement PDFs.
 *
 * @author Terry Curran
 * @version 3.3.0
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
                    .filter(path -> FILE_PATTERN.matcher(PathDigestSupport.fileName(path)).matches())
                    .sorted(Comparator.comparing(OctopusExtract::statementDate)
                            .thenComparing(PathDigestSupport::fileName))
                    .toList();
        }

        final List<ExtractedOctopusStatement> extracted = new ArrayList<>();
        var skipped = 0;
        for (var path : candidates) {
            final var candidateFileName = PathDigestSupport.fileName(path);
            final var hash = PathDigestSupport.sha256(path);
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
       final var fileName = PathDigestSupport.fileName(path);
       final var matcher = FILE_PATTERN.matcher(fileName);
       if (!matcher.matches()) {
           throw new IllegalArgumentException("Invalid Octopus statement filename: " + fileName);
       }
       try {
           return LocalDate.parse(matcher.group(1));
       } catch (DateTimeParseException exception) {
           throw new IllegalArgumentException("Invalid statement date in filename: " + fileName, exception);
       }
    }
}
