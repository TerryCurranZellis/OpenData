package com.towermarsh.opendata.parser;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.csv.DuplicateHeaderMode;

/**
 * Immutable Apache Commons CSV parser options.
 *
 * @param charset source character set
 * @param delimiter field delimiter
 * @param quote quote character, empty to disable quoting
 * @param escape escape character, empty to disable explicit escaping
 * @param commentMarker comment marker, empty to disable comments
 * @param firstRecordAsHeader whether the first record supplies column names
 * @param trim whether values are trimmed
 * @param ignoreSurroundingSpaces whether delimiter-adjacent spaces are ignored
 * @param ignoreEmptyLines whether empty lines are ignored
 * @param allowMissingColumnNames whether blank header names are permitted
 * @param duplicateHeaderMode duplicate-header policy
 * @param nullString source token interpreted as null
 */
public record CsvParserOptions(
        Charset charset,
        char delimiter,
        Optional<Character> quote,
        Optional<Character> escape,
        Optional<Character> commentMarker,
        boolean firstRecordAsHeader,
        boolean trim,
        boolean ignoreSurroundingSpaces,
        boolean ignoreEmptyLines,
        boolean allowMissingColumnNames,
        DuplicateHeaderMode duplicateHeaderMode,
        Optional<String> nullString) {

    public CsvParserOptions {
        Objects.requireNonNull(charset, "charset");
        quote = Objects.requireNonNull(quote, "quote");
        escape = Objects.requireNonNull(escape, "escape");
        commentMarker =
                Objects.requireNonNull(commentMarker, "commentMarker");
        Objects.requireNonNull(
                duplicateHeaderMode,
                "duplicateHeaderMode");
        nullString = Objects.requireNonNull(nullString, "nullString");
    }

    /**
     * Returns safe defaults for conventional UTF-8 CSV files.
     *
     * @return default options
     */
    public static CsvParserOptions defaults() {
        return new CsvParserOptions(
                StandardCharsets.UTF_8,
                ',',
                Optional.of('"'),
                Optional.empty(),
                Optional.empty(),
                true,
                false,
                true,
                true,
                false,
                DuplicateHeaderMode.DISALLOW,
                Optional.empty());
    }
}
