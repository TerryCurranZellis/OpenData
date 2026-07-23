package com.towermarsh.opendata.parser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.towermarsh.opendata.exception.ImportException;

/**
 * CSV parser implemented using Apache Commons CSV.
 *
 * <p>This implementation correctly handles quoted delimiters, escaped quotes,
 * embedded line breaks, empty trailing values, comments and configurable
 * header policies. It retains the existing {@link DataParser} result type so
 * the wider ETL layer does not need to change in this batch.</p>
 */
public final class CsvDataParser implements DataParser {

    private final CsvParserOptions options;

    /**
     * Creates a parser with conventional UTF-8 CSV defaults.
     */
    public CsvDataParser() {
        this(CsvParserOptions.defaults());
    }

    /**
     * Creates a configured CSV parser.
     *
     * @param options parser options
     */
    public CsvDataParser(final CsvParserOptions options) {
        this.options = Objects.requireNonNull(options, "options");
    }

    @Override
    public List<Map<String, String>> parse(final Path file)
            throws ImportException {

        Objects.requireNonNull(file, "file");

        final CSVFormat format = buildFormat(options);

        try (Reader reader = Files.newBufferedReader(
                    file,
                    options.charset());
             CSVParser parser = format.parse(reader)) {

            final List<Map<String, String>> rows = new ArrayList<>();

            if (options.firstRecordAsHeader()) {
                final List<String> headers =
                        List.copyOf(parser.getHeaderMap().keySet());

                for (CSVRecord record : parser) {
                    rows.add(toHeaderMap(record, headers));
                }
            } else {
                for (CSVRecord record : parser) {
                    rows.add(toOrdinalMap(record));
                }
            }

            return List.copyOf(rows);
        } catch (IOException | IllegalArgumentException exception) {
            throw new ImportException(
                    "Unable to parse CSV file: " + file,
                    exception);
        }
    }

    private static CSVFormat buildFormat(
            final CsvParserOptions options) {

        final CSVFormat.Builder builder = CSVFormat.RFC4180.builder()
                .setDelimiter(options.delimiter())
                .setQuote(options.quote().orElse(null))
                .setEscape(options.escape().orElse(null))
                .setCommentMarker(
                        options.commentMarker().orElse(null))
                .setTrim(options.trim())
                .setIgnoreSurroundingSpaces(
                        options.ignoreSurroundingSpaces())
                .setIgnoreEmptyLines(options.ignoreEmptyLines())
                .setAllowMissingColumnNames(
                        options.allowMissingColumnNames())
                .setDuplicateHeaderMode(
                        options.duplicateHeaderMode())
                .setNullString(options.nullString().orElse(null));

        if (options.firstRecordAsHeader()) {
            builder.setHeader();
            builder.setSkipHeaderRecord(true);
        }

        return builder.get();
    }

    private static Map<String, String> toHeaderMap(
            final CSVRecord record,
            final List<String> headers) {

        final Map<String, String> row = new LinkedHashMap<>();

        for (String header : headers) {
            final String value = record.isSet(header)
                    ? record.get(header)
                    : "";
            row.put(header, value);
        }

        return Map.copyOf(row);
    }

    private static Map<String, String> toOrdinalMap(
            final CSVRecord record) {

        final Map<String, String> row = new LinkedHashMap<>();

        for (int index = 0; index < record.size(); index++) {
            row.put("column_" + (index + 1), record.get(index));
        }

        return Map.copyOf(row);
    }
}
