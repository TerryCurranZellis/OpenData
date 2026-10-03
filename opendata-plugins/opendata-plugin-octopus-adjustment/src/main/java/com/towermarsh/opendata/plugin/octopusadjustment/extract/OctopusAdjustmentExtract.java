/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.extract;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.octopusadjustment.initialise.OctopusAdjustmentConfiguration;
import com.towermarsh.opendata.util.PathDigestSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Discovers and fingerprints local Octopus Energy adjustment PDFs.
 *
 * <p>
 * Filenames must start with the configured account number followed by a hyphen
 * and must end with {@code .pdf}, case-insensitively. The suffix is treated as
 * opaque source identity and no date is parsed from it.</p>
 *
 * @author Terry Curran
 * @version 3.3.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentExtract {

    private static final Logger LOGGER = Logger.getLogger(OctopusAdjustmentExtract.class.getName());

    /**
     * Discovers adjustment PDFs that have not already completed with the same
     * filename and SHA-256 content hash.
     *
     * @param configuration typed adjustment configuration
     * @param context execution context
     * @return immutable source list
     * @throws IOException if the input directory or source files cannot be read
     * @since 3.1.0
     */
    public List<ExtractedOctopusAdjustment> extract(
            final OctopusAdjustmentConfiguration configuration,
            final PluginExecutionContext context) throws IOException {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(context, "context");

        final var inputDirectory = configuration.inputDirectory();
        if (!Files.isDirectory(inputDirectory)) {
            throw new IOException(
                    "Octopus adjustment input directory does not exist or is not a directory: "
                    + inputDirectory);
        }

        final var candidatePattern = candidatePattern(configuration.accountNumber());
        final var processed = context.dryRun()
                ? Set.<String>of()
                : new OctopusAdjustmentProcessedFileRepository(context.database())
                        .findProcessedFileKeys();

        final List<Path> candidates;
        try (var paths = Files.list(inputDirectory)) {
            candidates = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> candidatePattern.matcher(PathDigestSupport.fileName(path)).matches())
                    .sorted(Comparator.comparing(
                            PathDigestSupport::fileName,
                            String.CASE_INSENSITIVE_ORDER))
                    .toList();
        }

        final List<ExtractedOctopusAdjustment> extracted = new ArrayList<>();
        var skipped = 0;
        for (var path : candidates) {
            final var name = PathDigestSupport.fileName(path);
            final var hash = PathDigestSupport.sha256(path);
            if (processed.contains(OctopusAdjustmentProcessedFileRepository.key(name, hash))) {
                skipped++;
                continue;
            }
            extracted.add(new ExtractedOctopusAdjustment(
                    path,
                    name,
                    hash,
                    Files.size(path)));
        }

        final int skippedCount = skipped;
        LOGGER.log(Level.INFO,
                "Octopus adjustment extract: discovered {0} matching PDF(s), selected {1} "
                + "new/changed file(s), skipped {2} completed file(s)",
                new Object[]{candidates.size() + extracted.size() + skippedCount});
        return List.copyOf(extracted);
    }

    static boolean matchesCandidate(final String fileName, final String accountNumber) {
        Objects.requireNonNull(fileName, "fileName");
        Objects.requireNonNull(accountNumber, "accountNumber");
        return candidatePattern(accountNumber).matcher(fileName).matches();
    }

    private static Pattern candidatePattern(final String accountNumber) {
        return Pattern.compile(
                "^" + Pattern.quote(accountNumber) + "-.+\\.pdf$",
                Pattern.CASE_INSENSITIVE);
    }
}
