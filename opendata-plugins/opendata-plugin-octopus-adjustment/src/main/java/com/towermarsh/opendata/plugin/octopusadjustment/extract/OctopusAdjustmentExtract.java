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
public class OctopusAdjustmentExtract {

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
            OctopusAdjustmentConfiguration configuration,
            PluginExecutionContext context) throws IOException {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(context, "context");

        var inputDirectory = configuration.inputDirectory();
        if (!Files.isDirectory(inputDirectory)) {
            throw new IOException(
                    "Octopus adjustment input directory does not exist or is not a directory: "
                    + inputDirectory);
        }

        var candidatePattern = candidatePattern(configuration.accountNumber());
        var processed = context.dryRun()
                ? Set.<String>of()
                : new OctopusAdjustmentProcessedFileRepository(context.database())
                        .findProcessedFileKeys();

        List<Path> candidates;
        try (var paths = Files.list(inputDirectory)) {
            candidates = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> candidatePattern.matcher(PathDigestSupport.fileName(path)).matches())
                    .sorted(Comparator.comparing(
                            PathDigestSupport::fileName,
                            String.CASE_INSENSITIVE_ORDER))
                    .toList();
        }

        List<ExtractedOctopusAdjustment> extracted = new ArrayList<>();
        var skipped = 0;
        for (var path : candidates) {
            var name = PathDigestSupport.fileName(path);
            var hash = PathDigestSupport.sha256(path);
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

        int skippedCount = skipped;
        LOGGER.log(Level.INFO,
                "Octopus adjustment extract: discovered {0} matching PDF(s), selected {1} "
                + "new/changed file(s), skipped {2} completed file(s)",
                new Object[]{candidates.size(), extracted.size(), skippedCount});
        if (candidates.isEmpty()) {
            LOGGER.log(Level.WARNING,
                    "Octopus adjustment extract: no PDF in {0} matches {1}-*.pdf; {2} other file(s) were ignored",
                    new Object[]{inputDirectory, configuration.accountNumber(), countIgnored(inputDirectory)});
        }
        return List.copyOf(extracted);
    }

    /**
     * Count ignored.
     *
     * @param directory target directory
     * @return result of count ignored
     */
    private static long countIgnored(java.nio.file.Path directory) {
        try (var paths = Files.list(directory)) {
            return paths.filter(Files::isRegularFile).count();
        } catch (IOException exception) {
            return -1;
        }
    }

    /**
     * Matches candidate.
     *
     * @param fileName target file name
     * @param accountNumber input account number
     * @return result of matches candidate
     */
    static boolean matchesCandidate(String fileName, String accountNumber) {
        Objects.requireNonNull(fileName, "fileName");
        Objects.requireNonNull(accountNumber, "accountNumber");
        return candidatePattern(accountNumber).matcher(fileName).matches();
    }

    /**
     * Candidate pattern.
     *
     * @param accountNumber input account number
     * @return result of candidate pattern
     */
    private static Pattern candidatePattern(String accountNumber) {
        return Pattern.compile(
                "^" + Pattern.quote(accountNumber) + "-.+\\.pdf$",
                Pattern.CASE_INSENSITIVE);
    }
}
