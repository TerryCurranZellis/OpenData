/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Extracts plain text from a PDF file using Apache PDFBox 3.x.
 * <p>
 * The extractor reads the entire PDF and returns all textual content as a
 * single string with the original line endings. Because Octopus Energy PDF
 * bills use a two-column layout, the extracted text interleaves the left and
 * right columns; callers must join and normalise the lines before applying
 * pattern-matching (see {@link OctopusStatementParser} for this processing).
 *
 * <h2>Example</h2>
 * <pre>
 *   Path pdf = Path.of("samples/octopus-energy-statement-2026-01-05.pdf");
 *   String text = PdfTextExtractor.extract(pdf);
 *   System.out.println("Extracted " + text.length() + " characters");
 * </pre>
 *
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 15 Mar 2026
 */
public final class PdfTextExtractor {

    private PdfTextExtractor() {
        /* static utility class – not instantiated */ }

    /**
     * Extract all text from the PDF at the given path.
     *
     * <p>
     * The returned string preserves the original line endings from the PDF text
     * stream. Encrypted PDFs that require a password will throw an
     * {@link IOException}.
     *
     * @param pdfPath path to the PDF file to extract text from
     * @return the full text content of the PDF as returned by
     * {@link PDFTextStripper}; never {@code null}
     * @throws IOException if the file cannot be read, is not a valid PDF, or is
     * encrypted and cannot be opened without a password
     */
    public static String extract(Path pdfPath) throws IOException {
        try (var document = Loader.loadPDF(pdfPath.toFile())) {
            var stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
