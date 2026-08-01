/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.transform;

import java.io.IOException;
import com.towermarsh.opendata.plugin.octopus.extract.PdfTextExtractor;
import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Extract energy data from Octopus energy statements.
 *
 * <p>
 * Parses Octopus Energy statement PDF files (after text extraction) and
 * produces {@link ElectricityRecord} and {@link GasRecord} lists that can
 * be written to CSV files or merged directly into the database layer for
 * subsequent analysis.
 *
 * <p>
 * The PDF bills use a two-column page layout. When text is extracted by PDFBox
 * the two columns are interleaved on the same lines. This parser handles that
 * by joining every line into one long string and collapsing consecutive
 * white space before applying regex patterns — exactly as the PowerShell script
 * does.
 * <p>
 * <b>Example</b>
 * <pre>
 *   Path pdfDir = Path.of("samples");
 *   Path outDir = Path.of("output");
 *   OctopusStatementParser parser = new OctopusStatementParser(pdfDir);
 *   List&lt;ElectricityRecord&gt; elec = parser.parseElectricity();
 *   List&lt;GasRecord&gt; gas = parser.parseGas();
 *   OctopusStatementParser.writeElectricityCsv(elec, outDir.resolve("electric_data.csv"));
 *   OctopusStatementParser.writeGasCsv(gas, outDir.resolve("gas_data.csv"));
 * </pre>
 *
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 1.0.0
 *
 */
public final class OctopusStatementParser {

    // ── Date patterns ────────────────────────────────────────────────────────
    /**
     * Ordinal-suffix pattern fragment, e.g. "4th", "22nd".
     */
    private static final String ORDINAL = "\\d{1,2}(?:st|nd|rd|th)";
    /**
     * Word with optional trailing dot, e.g. "Dec.", "December".
     */
    private static final String MONTH_WORD = "\\w+\\.?";
    /**
     * Four-digit year.
     */
    private static final String YEAR = "\\d{4}";
    /**
     * Full ordinal date fragment, e.g. "4th Dec. 2021".
     */
    private static final String DATE_FRAG
            = "(" + ORDINAL + "\\s+" + MONTH_WORD + "\\s+" + YEAR + ")";

    // ── Section delimiters ───────────────────────────────────────────────────
    private static final Pattern ELEC_START = Pattern.compile("Electricity Supply number");
    private static final Pattern ELEC_END = Pattern.compile("Total Electricity Charges");
    private static final Pattern GAS_START = Pattern.compile("Gas Meter Point Reference:");
    private static final Pattern GAS_END = Pattern.compile("Total Gas Charges");

    // ── Field patterns ───────────────────────────────────────────────────────
    private static final Pattern BILL_PERIOD
            = Pattern.compile(DATE_FRAG + "\\s*-\\s*" + DATE_FRAG);

    private static final Pattern TARIFF_PERIOD_EXACT
            = Pattern.compile("\\(" + DATE_FRAG + "\\s*-\\s*" + DATE_FRAG + "\\)");

    private static final Pattern TARIFF_PERIOD_RELAXED
            = Pattern.compile("\\(" + DATE_FRAG + "\\s*-\\s*(" + ORDINAL + "\\s+" + MONTH_WORD + ")\\s[^)]+?(\\d{4})\\)");

    private static final Pattern TARIFF_NAME
            = Pattern.compile("((?:Loyal\\s+|Flexible\\s+|Go\\s+)?Octopus(?:\\s+(?!Octopus|Flexible|Loyal|Go)\\w+){0,5}?)\\s*$");

    private static final Pattern METER_READING
            = Pattern.compile("(" + ORDINAL + "\\s+" + MONTH_WORD + "\\s+" + YEAR + ")\\s+([\\d.]+)\\s+"
                    + "(Smart meter reading|Estimated reading|Data collector reading|"
                    + "Actual reading|Customer reading)",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern MPAN
            = Pattern.compile("Electricity Supply number\\s+\\S+(?:\\s+\\S+)*?\\s+(\\d{13})\\b");
    private static final Pattern MPAN_FALLBACK
            = Pattern.compile("\\b(\\d{13})\\b");
    private static final Pattern MPRN
            = Pattern.compile("Gas Meter Point Reference:\\s+(\\d+)");
    private static final Pattern METER_ID
            = Pattern.compile("Energy Charges for Meter\\s+(\\S+)");

    private static final Pattern UNIT_RATE
            = Pattern.compile("Unit Rate\\s+([\\d.]+)p/kWh");
    private static final Pattern ENERGY_USED
            = Pattern.compile("Energy Used\\s+(-?[\\d.]+)\\s+kWh\\s+@\\s+([\\d.]+)p/kWh");
    private static final Pattern ENERGY_USED_GAS
            = Pattern.compile("Energy Used\\*?\\s+(-?[\\d.]+)\\s+kWh\\s+@\\s+([\\d.]+)p/kWh");
    private static final Pattern CONSUMPTION_M3
            = Pattern.compile("Consumption\\s+([\\d.]+)\\s+Units\\s+\\(m3\\)");

    private static final Pattern SC_FULL
            = Pattern.compile("Standing Charge\\s+\\d+\\s+days\\s+@\\s+([\\d.]+)p/day\\s+\u00A3([\\d.]+)");
    private static final Pattern SC_RATE_ONLY
            = Pattern.compile("Standing Charge\\s+([\\d.]+)p/day");

    private static final Pattern TOTAL_ELEC
            = Pattern.compile("Total Electricity Charges\\s+(-?)\u00A3([\\d.]+)");
    private static final Pattern TOTAL_GAS
            = Pattern.compile("Total Gas Charges\\s+(-?)\u00A3([\\d.]+)");

    /**
     * Filename date pattern for PDF files:
     * octopus-energy-statement-YYYY-MM-DD.pdf
     */
    private static final Pattern PDF_DATE_PATTERN
            = Pattern.compile("octopus-energy-statement-(\\d{4}-\\d{2}-\\d{2})\\.pdf",
                    Pattern.CASE_INSENSITIVE);

    // ── Instance state ───────────────────────────────────────────────────────
    private static final Logger logger = Logger.getLogger(OctopusStatementParser.class.getName());
    private final Path inputDirectory;

    /**
     * Construct a parser that will search {@code inputDirectory} for Octopus
     * Energy PDF files.
     *
     * @param inputDirectory directory containing
     * {@code octopus-energy-statement-YYYY-MM-DD.pdf} files; must exist
     */
    public OctopusStatementParser(Path inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Parse electricity records from a single PDF file that does not follow
     * the {@code octopus-energy-statement-YYYY-MM-DD.pdf} naming convention.
     *
     * <p>
     * This method is intended for "catch-up" or summary billing PDFs whose
     * filenames carry account or bill-reference identifiers rather than a
     * statement date. The bill date is derived from the bill-period end date
     * found in the PDF text.
     *
     * @param pdfPath path to the PDF file to parse
     * @return list of extracted electricity records; never {@code null}
     * @throws IOException if the PDF cannot be read
     */
    public static List<ElectricityRecord> parseElectricityFromFile(Path pdfPath)
            throws IOException {
        var rawText = PdfTextExtractor.extract(pdfPath);
        if (rawText.isBlank()) {
            logger.log(Level.INFO, "  Skipping empty file: {0}", pdfPath.getFileName());
            return List.of();
        }
        var fullText = toJoinedText(rawText);

        // Derive bill date from the bill-period end (or start when end is absent)
        var period = getBillPeriod(fullText);
        var billDate = !period[1].isEmpty() ? period[1] : period[0];

        logger.log(Level.INFO, "  Parsing {0}  bill period {1} to {2}",
                new Object[]{pdfPath.getFileName(), period[0], period[1]});

        List<ElectricityRecord> records = new ArrayList<>();
        for (var section : getElectricitySections(fullText)) {
            records.add(newElectricityRecord(section, billDate, period[0], period[1]));
        }
        logger.log(Level.INFO, "  Found {0} electricity section(s) in {1}",
                new Object[]{records.size(), pdfPath.getFileName()});
        return records;
    }

    /**
     * Parse gas records from a single PDF file that does not follow the
     * {@code octopus-energy-statement-YYYY-MM-DD.pdf} naming convention.
     *
     * <p>
     * This is the gas counterpart to {@link #parseElectricityFromFile(Path)}.
     * It is intended for catch-up or adjustment billing PDFs whose filenames
     * carry account or bill-reference identifiers rather than a statement date.
     * The bill date is derived from the bill-period end date found in the PDF
     * text.
     *
     * @param pdfPath path to the PDF file to parse
     * @return list of extracted gas records; never {@code null}; will be empty
     *         for files that contain no gas section (e.g. electricity-only
     *         adjustment bills)
     * @throws IOException if the PDF cannot be read
     */
    public static List<GasRecord> parseGasFromFile(Path pdfPath)
            throws IOException {
        var rawText = PdfTextExtractor.extract(pdfPath);
        if (rawText.isBlank()) {
            logger.log(Level.INFO, "  Skipping empty file: {0}", pdfPath.getFileName());
            return List.of();
        }
        var fullText = toJoinedText(rawText);

        var period = getBillPeriod(fullText);
        var billDate = !period[1].isEmpty() ? period[1] : period[0];

        logger.log(Level.INFO, "  Parsing gas from {0}  bill period {1} to {2}",
                new Object[]{pdfPath.getFileName(), period[0], period[1]});

        List<GasRecord> records = new ArrayList<>();
        for (var section : getGasSections(fullText)) {
            records.add(newGasRecord(section, billDate, period[0], period[1]));
        }
        logger.log(Level.INFO, "  Found {0} gas section(s) in {1}",
                new Object[]{records.size(), pdfPath.getFileName()});
        return records;
    }

    /**
     * Parse both electricity and gas records from a single PDF file that does
     * not follow the {@code octopus-energy-statement-YYYY-MM-DD.pdf} naming
     * convention.
     *
     * <p>
     * This is a single-pass convenience method that combines
     * {@link #parseElectricityFromFile(Path)} and
     * {@link #parseGasFromFile(Path)}: the PDF text is extracted only once,
     * making it more efficient than calling the two methods separately.
     *
     * <p>
     * <b>Example</b>
     * <pre>
     *   Path adj = Path.of("C:/Attachments/octopus/A-5F191685-419015087-1.pdf");
     *   Object[] both = OctopusStatementParser.parseBothFromFile(adj);
     *   List&lt;ElectricityRecord&gt; elec = (List&lt;ElectricityRecord&gt;) both[0];
     *   List&lt;GasRecord&gt;         gas  = (List&lt;GasRecord&gt;)         both[1];
     * </pre>
     *
     * @param pdfPath path to the PDF file to parse
     * @return two-element array where index&nbsp;0 holds a
     *         {@code List<ElectricityRecord>} and index&nbsp;1 holds a
     *         {@code List<GasRecord>}; neither element is {@code null}
     * @throws IOException if the PDF cannot be read
     */
    public static Object[] parseBothFromFile(Path pdfPath) throws IOException {
        var rawText = PdfTextExtractor.extract(pdfPath);
        if (rawText.isBlank()) {
            logger.log(Level.INFO, "  Skipping empty file: {0}", pdfPath.getFileName());
            return new Object[]{List.of(), List.of()};
        }
        var fullText = toJoinedText(rawText);

        var period = getBillPeriod(fullText);
        var billDate = !period[1].isEmpty() ? period[1] : period[0];

        logger.log(Level.INFO, "  Parsing {0}  bill period {1} to {2}",
                new Object[]{pdfPath.getFileName(), period[0], period[1]});

        List<ElectricityRecord> elecRecords = new ArrayList<>();
        for (var section : getElectricitySections(fullText)) {
            elecRecords.add(newElectricityRecord(section, billDate, period[0], period[1]));
        }

        List<GasRecord> gasRecords = new ArrayList<>();
        for (var section : getGasSections(fullText)) {
            gasRecords.add(newGasRecord(section, billDate, period[0], period[1]));
        }

        logger.log(Level.INFO, "  {0}  [elec: {1}, gas: {2}]",
                new Object[]{pdfPath.getFileName(), elecRecords.size(), gasRecords.size()});

        return new Object[]{elecRecords, gasRecords};
    }

    /**
     * Parse all PDF files in the input directory and return electricity
     * records.
     *
     * <p>
     * Files are processed in chronological order (oldest bill first) so the
     * resulting list is also chronologically ordered. Files that do not match
     * the expected {@code octopus-energy-statement-YYYY-MM-DD.pdf} naming
     * convention are silently ignored.
     *
     * @return list of extracted electricity records; never {@code null}
     * @throws IOException if any PDF file cannot be read or its text cannot be
     * extracted
     */
    public List<ElectricityRecord> parseElectricity() throws IOException {
        List<ElectricityRecord> records = new ArrayList<>();
        for (var entry : findPdfFiles()) {
            var billDate = entry.billDate();
            var fullText = toJoinedText(PdfTextExtractor.extract(entry.path()));
            if (fullText.isBlank()) {
                logger.log(Level.INFO, "  Skipping empty file: {0}", entry.path().getFileName());
                continue;
            }
            var period = getBillPeriod(fullText);
            for (var section : getElectricitySections(fullText)) {
                records.add(newElectricityRecord(section, billDate, period[0], period[1]));
            }
        }
        return records;
    }

    /**
     * Parse all PDF files in the input directory and return gas records.
     *
     * <p>
     * Files are processed in chronological order (oldest bill first).
     *
     * @return list of extracted gas records; never {@code null}
     * @throws IOException if any PDF file cannot be read or its text cannot be
     * extracted
     */
    public List<GasRecord> parseGas() throws IOException {
        List<GasRecord> records = new ArrayList<>();
        for (var entry : findPdfFiles()) {
            var billDate = entry.billDate();
            var fullText = toJoinedText(PdfTextExtractor.extract(entry.path()));
            if (fullText.isBlank()) {
                continue;
            }
            var period = getBillPeriod(fullText);
            for (var section : getGasSections(fullText)) {
                records.add(newGasRecord(section, billDate, period[0], period[1]));
            }
        }
        return records;
    }

    /**
     * Parse all PDF files and report the bill count per file to standard
     * output.
     *
     * <p>
     * This method extracts both electricity and gas data in a single pass (one
     * PDF read per file) and is therefore more efficient than calling
     * {@link #parseElectricity()} and {@link #parseGas()} separately. The
     * returned pair contains the complete electricity and gas record lists.
     *
     * @return a two-element array where index 0 holds the electricity records
     * and index 1 holds the gas records
     * @throws IOException if any PDF file cannot be read or its text cannot be
     * extracted
     */
    public Object[] parseBoth() throws IOException {
        List<ElectricityRecord> elecRecords = new ArrayList<>();
        List<GasRecord> gasRecords = new ArrayList<>();

        for (var entry : findPdfFiles()) {
            var billDate = entry.billDate();
            var fullText = toJoinedText(PdfTextExtractor.extract(entry.path()));
            if (fullText.isBlank()) {
                logger.log(Level.INFO, "  Skipping empty file: {0}", entry.path().getFileName());
                continue;
            }
            var period = getBillPeriod(fullText);
            var elecSections = getElectricitySections(fullText);
            var gasSections = getGasSections(fullText);

            elecSections.forEach(section -> {
                elecRecords.add(newElectricityRecord(section, billDate, period[0], period[1]));
            });
            gasSections.forEach(section -> {
                gasRecords.add(newGasRecord(section, billDate, period[0], period[1]));
            });

            logger.log(Level.INFO, "  {0}  bill period {1} to {2}  [elec: {3}, gas: {4}]",
                    new Object[]{
                        entry.path().getFileName(),
                        period[0].isEmpty() ? "?" : period[0],
                        period[1].isEmpty() ? "?" : period[1],
                        elecSections.size(), gasSections.size()});
        }
        return new Object[]{elecRecords, gasRecords};
    }

    
    // ── File discovery ───────────────────────────────────────────────────────
    /**
     * Return an ordered list of PDF entries whose filenames match the pattern
     * {@code octopus-energy-statement-YYYY-MM-DD.pdf}, sorted chronologically
     * (oldest first).
     *
     * @return sorted list of {@link PdfEntry} objects; never {@code null}
     * @throws IOException if the input directory cannot be listed
     */
    private List<PdfEntry> findPdfFiles() throws IOException {
        List<PdfEntry> entries = new ArrayList<>();
        try (var stream = Files.list(inputDirectory)) {
            stream.filter((var p) -> {
                var m = PDF_DATE_PATTERN.matcher(p.getFileName().toString());
                return m.matches();
            }).forEach((var p) -> {
                var m = PDF_DATE_PATTERN.matcher(p.getFileName().toString());
                if (m.matches()) {
                    entries.add(new PdfEntry(p, m.group(1)));
                }
            });
        }
        entries.sort(Comparator.comparing(PdfEntry::billDate));
        return entries;
    }

    /**
     * Return the number of PDF files found in the input directory that match
     * the expected Octopus Energy statement filename pattern.
     *
     * @return count of matching PDF files
     * @throws IOException if the input directory cannot be listed
     */
    public int countPdfFiles() throws IOException {
        return findPdfFiles().size();
    }

    // ── Text normalisation ───────────────────────────────────────────────────
    /**
     * Join every line of the extracted PDF text into a single string and
     * collapse runs of three or more whitespace characters to two spaces.
     *
     * <p>
     * This replicates the PowerShell line:
     * {@code ($rawLines -join ' ') -replace '\s{3,}', ' '} and is necessary
     * because the two-column PDF layout causes text from both columns to appear
     * on the same extracted line.
     *
     * @param rawText text as returned by {@link PdfTextExtractor#extract}
     * @return normalised single-line string
     */
    private static String toJoinedText(String rawText) {
        var joined = rawText.replaceAll("\\r?\\n", " ");
        return joined.replaceAll("\\s{3,}", "  ");
    }

    // ── Date conversion ──────────────────────────────────────────────────────
    /**
     * Convert an Octopus Energy date string (e.g. {@code "4th Dec. 2021"}) to
     * ISO format {@code yyyy-MM-dd}.
     *
     * <p>
     * Handles the following variations found in the bills:
     * <ul>
     * <li>Ordinal suffixes: 1st, 2nd, 3rd, 4th, …, 22nd, …</li>
     * <li>Abbreviated month names with optional trailing dot: Jan., Feb.,
     * …</li>
     * <li>"Sept." normalised to "Sep." before parsing</li>
     * <li>Full month names: January, February, …</li>
     * </ul>
     *
     * @param dateStr raw date string from the bill text
     * @return the date in {@code yyyy-MM-dd} format, or an empty string if
     * {@code dateStr} is blank or cannot be parsed
     */
    static String convertOctopusDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return "";
        }
        try {
            // Remove ordinal suffixes: 1st -> 1, 2nd -> 2, 3rd -> 3, 4th -> 4 ...
            var clean = dateStr.trim()
                    .replaceAll("\\b(\\d{1,2})(st|nd|rd|th)\\b", "$1");
            // Normalise "Sept." -> "Sep." and "Sept" -> "Sep" for uniform handling
            // (Java Locale.ENGLISH uses "Sep" as the 3-letter abbreviation)
            clean = clean.replaceAll("\\bSept\\.", "Sep.");
            clean = clean.replaceAll("\\bSept\\b", "Sep");
            // Remove trailing dots from abbreviated month names
            clean = clean.replaceAll(
                    "\\b(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\.", "$1");

            // Try parsing with multiple format patterns; use Locale.ENGLISH because
            // Java Locale.UK (en_GB) uses "Sept" for September while bills use "Sep"
            String[] patterns = {"d MMMM yyyy", "d MMM yyyy"};
            for (var fmt : patterns) {
                try {
                    var sdf = new SimpleDateFormat(fmt, Locale.ENGLISH);
                    sdf.setLenient(false);
                    var dt = sdf.parse(clean);
                    SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    return out.format(dt);
                } catch (ParseException ignored) {
                    // try next pattern
                }
            }
            logger.log(Level.WARNING, "  Could not parse date string: ''{0}''", dateStr);
            return "";
        } catch (Exception e) {
            logger.log(Level.WARNING, "  Could not parse date string: ''{0}''", dateStr);
            return "";
        }
    }

    // ── Bill period ──────────────────────────────────────────────────────────
    /**
     * Extract the overall bill period (start and end dates) from the full bill
     * text.
     *
     * <p>
     * The bill period appears near the top of every statement as a date range,
     * e.g. {@code "7th Dec. 2021 - 5th Jan. 2022"}.
     *
     * @param fullText the normalised, joined bill text
     * @return two-element array {@code {startDate, endDate}} in
     * {@code yyyy-MM-dd} format; both elements are empty strings if the pattern
     * is not found
     */
    static String[] getBillPeriod(String fullText) {
        var m = BILL_PERIOD.matcher(fullText);
        if (m.find()) {
            return new String[]{
                convertOctopusDate(m.group(1)),
                convertOctopusDate(m.group(2))
            };
        }
        return new String[]{"", ""};
    }

    // ── Tariff period ────────────────────────────────────────────────────────
    /**
     * Extract the tariff name and charging period from a section of bill text.
     *
     * <p>
     * The bill PDF uses a two-column layout; after text extraction and
     * line-joining, values from the right column are interspersed in the text.
     * This method uses two patterns to locate the tariff period:
     * <ol>
     * <li>An exact {@code "(start_date - end_date)"} match.</li>
     * <li>A relaxed pattern that tolerates intervening words between the end
     * month and the end year (caused by two-column interleaving).</li>
     * </ol>
     * The tariff name is extracted from the text immediately before the opening
     * {@code "("} of the date range.
     *
     * @param sectionText text of one tariff-period section
     * @return three-element array {@code {name, startDate, endDate}} where each
     * element is a string (or empty if not found)
     */
    static String[] getTariffPeriod(String sectionText) {
        var startDate = "";
        var endDate = "";
        var parenPos = -1;

        // Pattern 1: exact "(start - end)" date range
        var exact = TARIFF_PERIOD_EXACT.matcher(sectionText);
        if (exact.find()) {
            startDate = convertOctopusDate(exact.group(1));
            endDate = convertOctopusDate(exact.group(2));
            parenPos = exact.start();
        } else {
            // Pattern 2: relaxed – year of end date separated by intervening words
            var relaxed = TARIFF_PERIOD_RELAXED.matcher(sectionText);
            if (relaxed.find()) {
                startDate = convertOctopusDate(relaxed.group(1));
                endDate = convertOctopusDate(relaxed.group(2) + " " + relaxed.group(3));
                parenPos = relaxed.start();
            }
        }

        if (parenPos < 0) {
            return new String[]{"", "", ""};
        }

        // Extract tariff name from text immediately before the "("
        var prefixLen = Math.min(120, parenPos);
        var prefix = sectionText.substring(parenPos - prefixLen, parenPos).stripTrailing();
        var nameMatcher = TARIFF_NAME.matcher(prefix);
        var tariffName = nameMatcher.find() ? nameMatcher.group(1).trim() : "";

        return new String[]{tariffName, startDate, endDate};
    }

    // ── Meter readings ───────────────────────────────────────────────────────
    /**
     * Return all meter readings found in the given section text, sorted
     * chronologically (oldest first).
     *
     * <p>
     * Each meter reading line contains a date with an ordinal suffix, a decimal
     * value, and a recognised reading-type keyword.
     *
     * @param sectionText text of one tariff-period section
     * @return list of {@link MeterReading} objects sorted by date; may be empty
     */
    static List<MeterReading> getMeterReadings(String sectionText) {
        List<MeterReading> readings = new ArrayList<>();
        var m = METER_READING.matcher(sectionText);
        while (m.find()) {
            var date = convertOctopusDate(m.group(1));
            if (!date.isEmpty()) {
                readings.add(new MeterReading(date, m.group(2), m.group(3)));
            }
        }
        readings.sort(Comparator.comparing(MeterReading::date));
        return readings;
    }

    // ── Section extraction ───────────────────────────────────────────────────
    /**
     * Extract all electricity tariff-period section texts from the full bill
     * text.
     *
     * <p>
     * Each electricity section starts at {@code "Electricity Supply number"}
     * and ends just past {@code "Total Electricity Charges"}. A bill may
     * contain more than one section when the tariff rate changed during the
     * billing period.
     *
     * @param fullText the normalised, joined bill text
     * @return list of section text strings; may be empty if no electricity
     * sections are found
     */
    static List<String> getElectricitySections(String fullText) {
        return extractSections(fullText, ELEC_START, ELEC_END);
    }

    /**
     * Extract all gas tariff-period section texts from the full bill text.
     *
     * <p>
     * Each gas section starts at {@code "Gas Meter Point Reference:"} and ends
     * just past {@code "Total Gas Charges"}.
     *
     * @param fullText the normalised, joined bill text
     * @return list of section text strings; may be empty if no gas sections are
     * found
     */
    static List<String> getGasSections(String fullText) {
        return extractSections(fullText, GAS_START, GAS_END);
    }

    /**
     * Generic helper that extracts text sections delimited by a start and end
     * pattern from a joined bill string.
     *
     * @param fullText the full bill text
     * @param start pattern that marks the beginning of each section
     * @param end pattern that marks the end of each section
     * @return list of section strings (each includes a 30-character tail past
     * the end marker to capture the total charge amount)
     */
    private static List<String> extractSections(
            String fullText, Pattern start, Pattern end) {
        List<String> sections = new ArrayList<>();
        var startM = start.matcher(fullText);
        while (startM.find()) {
            var startPos = startM.start();
            var remainder = fullText.substring(startPos);
            var endM = end.matcher(remainder);
            if (endM.find()) {
                var sectionEnd = startPos + endM.start() + endM.group().length();
                var extraLen = Math.min(30, fullText.length() - sectionEnd);
                sections.add(fullText.substring(startPos, sectionEnd + extraLen));
            }
        }
        return sections;
    }

    // ── Record builders ──────────────────────────────────────────────────────
    /**
     * Parse one electricity tariff-period section and return a
     * {@link ElectricityRecord}.
     *
     * @param sectionText raw text of one electricity section (from
     * {@code "Electricity Supply number"} to just past
     * {@code "Total Electricity Charges £…"})
     * @param billDate date extracted from the bill filename
     * ({@code yyyy-MM-dd})
     * @param billPeriodStart overall bill period start ({@code yyyy-MM-dd});
     * may be empty
     * @param billPeriodEnd overall bill period end ({@code yyyy-MM-dd}); may be
     * empty
     * @return a fully populated {@link ElectricityRecord}; fields that
     * cannot be parsed are empty strings
     */
    static ElectricityRecord newElectricityRecord(
            String sectionText, String billDate,
            String billPeriodStart, String billPeriodEnd) {

        // MPAN (13-digit supply point identifier)
        var mpan = "";
        var mpanM = MPAN.matcher(sectionText);
        if (mpanM.find()) {
            mpan = mpanM.group(1);
        } else {
            var fb = MPAN_FALLBACK.matcher(sectionText);
            if (fb.find()) {
                mpan = fb.group(1);
            }
        }

        // Physical meter serial number
        var meterId = "";
        var meterM = METER_ID.matcher(sectionText);
        if (meterM.find()) {
            meterId = meterM.group(1);
        }

        // Tariff name and charging period
        var tariff = getTariffPeriod(sectionText);

        // Unit rate (p/kWh)
        // Primary: explicit "Unit Rate Xp/kWh" line (from "About Your Tariff" column).
        // Fallback: rate embedded in "Energy Used N kWh @ Xp/kWh" when the primary
        // is not available (PDFBox may place the "About Your Tariff" column outside
        // the section boundaries).
        var unitRate = "";
        var unitM = UNIT_RATE.matcher(sectionText);
        if (unitM.find()) {
            unitRate = unitM.group(1);
        } else {
            var energyM2 = ENERGY_USED.matcher(sectionText);
            if (energyM2.find()) {
                unitRate = energyM2.group(2);
            }
        }

        // Meter readings – take first (start) and last (end) chronologically
        var readings = getMeterReadings(sectionText);
        var startReadDate = readings.size() >= 1 ? readings.get(0).date() : "";
        var startReadValue = readings.size() >= 1 ? readings.get(0).value() : "";
        var startReadType = readings.size() >= 1 ? readings.get(0).type() : "";
        var endReadDate = readings.size() >= 2 ? readings.get(readings.size() - 1).date() : "";
        var endReadValue = readings.size() >= 2 ? readings.get(readings.size() - 1).value() : "";
        var endReadType = readings.size() >= 2 ? readings.get(readings.size() - 1).type() : "";

        // Energy consumed (kWh)
        var energyKwh = "";
        var energyM = ENERGY_USED.matcher(sectionText);
        if (energyM.find()) {
            energyKwh = energyM.group(1);
        }

        // Standing charge rate and total
        var scRate = "";
        var scTotal = "";
        var scFull = SC_FULL.matcher(sectionText);
        if (scFull.find()) {
            scRate = scFull.group(1);
            scTotal = scFull.group(2);
        } else {
            var scRateOnly = SC_RATE_ONLY.matcher(sectionText);
            if (scRateOnly.find()) {
                scRate = scRateOnly.group(1);
            }
        }

        // Total electricity charge (inc. VAT)
        var totalCost = "";
        var totalM = TOTAL_ELEC.matcher(sectionText);
        if (totalM.find()) {
            totalCost = totalM.group(1) + totalM.group(2);
        }

        return new ElectricityRecord(
                billDate, billPeriodStart, billPeriodEnd,
                tariff[0], tariff[1], tariff[2],
                mpan, meterId,
                startReadDate, startReadValue, startReadType,
                endReadDate, endReadValue, endReadType,
                energyKwh, unitRate, scRate, scTotal, totalCost);
    }

    /**
     * Parse one gas tariff-period section and return a {@link GasRecord}.
     *
     * @param sectionText raw text of one gas section (from
     * {@code "Gas Meter Point Reference:"} to just past
     * {@code "Total Gas Charges £…"})
     * @param billDate date extracted from the bill filename
     * ({@code yyyy-MM-dd})
     * @param billPeriodStart overall bill period start ({@code yyyy-MM-dd});
     * may be empty
     * @param billPeriodEnd overall bill period end ({@code yyyy-MM-dd}); may be
     * empty
     * @return a fully populated {@link GasRecord}; fields that cannot be
     * parsed are empty strings
     */
    static GasRecord newGasRecord(
            String sectionText, String billDate,
            String billPeriodStart, String billPeriodEnd) {

        // MPRN (gas supply point identifier)
        var mprn = "";
        var mprnM = MPRN.matcher(sectionText);
        if (mprnM.find()) {
            mprn = mprnM.group(1);
        }

        // Physical meter serial number
        var meterId = "";
        var meterM = METER_ID.matcher(sectionText);
        if (meterM.find()) {
            meterId = meterM.group(1);
        }

        // Tariff name and charging period
        var tariff = getTariffPeriod(sectionText);

        // Unit rate (p/kWh) – same two-pattern approach as electricity
        var unitRate = "";
        var unitM = UNIT_RATE.matcher(sectionText);
        if (unitM.find()) {
            unitRate = unitM.group(1);
        } else {
            var energyM2 = ENERGY_USED_GAS.matcher(sectionText);
            if (energyM2.find()) {
                unitRate = energyM2.group(2);
            }
        }

        // Meter readings – take first (start) and last (end) chronologically
        var readings = getMeterReadings(sectionText);
        var startReadDate = readings.size() >= 1 ? readings.get(0).date() : "";
        var startReadValue = readings.size() >= 1 ? readings.get(0).value() : "";
        var startReadType = readings.size() >= 1 ? readings.get(0).type() : "";
        var endReadDate = readings.size() >= 2 ? readings.get(readings.size() - 1).date() : "";
        var endReadValue = readings.size() >= 2 ? readings.get(readings.size() - 1).value() : "";
        var endReadType = readings.size() >= 2 ? readings.get(readings.size() - 1).type() : "";

        // Gas consumption in cubic metres
        var consumptionM3 = "";
        var consM = CONSUMPTION_M3.matcher(sectionText);
        if (consM.find()) {
            consumptionM3 = consM.group(1);
        }

        // Energy consumed in kWh (calorific-value-corrected)
        var energyKwh = "";
        var energyM = ENERGY_USED_GAS.matcher(sectionText);
        if (energyM.find()) {
            energyKwh = energyM.group(1);
        }

        // Standing charge rate and total
        var scRate = "";
        var scTotal = "";
        var scFull = SC_FULL.matcher(sectionText);
        if (scFull.find()) {
            scRate = scFull.group(1);
            scTotal = scFull.group(2);
        } else {
            var scRateOnly = SC_RATE_ONLY.matcher(sectionText);
            if (scRateOnly.find()) {
                scRate = scRateOnly.group(1);
            }
        }

        // Total gas charge (inc. VAT)
        var totalCost = "";
        var totalM = TOTAL_GAS.matcher(sectionText);
        if (totalM.find()) {
            totalCost = totalM.group(1) + totalM.group(2);
        }

        return new GasRecord(
                billDate, billPeriodStart, billPeriodEnd,
                tariff[0], tariff[1], tariff[2],
                mprn, meterId,
                startReadDate, startReadValue, startReadType,
                endReadDate, endReadValue, endReadType,
                consumptionM3, energyKwh, unitRate, scRate, scTotal, totalCost);
    }

    // ── Inner helper types ───────────────────────────────────────────────────
    /**
     * Lightweight holder pairing a PDF {@link Path} with the bill date string
     * extracted from its filename.
     *
     * @param path path to the PDF file
     * @param billDate bill date in {@code yyyy-MM-dd} format
     */
    record PdfEntry(Path path, String billDate) {

    }

    /**
     * One meter reading extracted from a tariff-period section.
     *
     * @param date reading date in {@code yyyy-MM-dd} format
     * @param value reading value as a decimal string (kWh or m³)
     * @param type reading type, e.g. {@code "Smart meter reading"}
     */
    record MeterReading(String date, String value, String type) {

    }
}
