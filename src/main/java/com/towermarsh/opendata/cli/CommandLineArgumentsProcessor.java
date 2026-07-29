/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.cli;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Parses and validates the OpenData command line.
 *
 * @author terry curran
 * @version 28 July 2026
 */
public final class CommandLineArgumentsProcessor {

    /**
     * Application Name
     */
    private static final String APPLICATION_NAME = "opendata";

    /**
     * Create command line options
     */
    private final Options options = createOptions();

    /**
     * Creates a command-line processor.
     */
    public CommandLineArgumentsProcessor() {
    }

    /**
     * Parses and validates the supplied command-line arguments.
     *
     * @param arguments raw command-line arguments
     * @return parsed arguments
     * @throws CommandLineProcessingException if the command line is invalid
     */
    public CommandLineArguments parse(final String[] arguments) {
        Objects.requireNonNull(arguments, "arguments");
        try {
            final String[] normalisedArguments = normaliseArguments(arguments);
            return toArguments(new DefaultParser().parse(options, normalisedArguments));
        } catch (ParseException | IllegalArgumentException exception) {
            throw new CommandLineProcessingException(exception.getMessage(), exception);
        }
    }

    /**
     * Normalises launcher input before Commons CLI parsing.
     *
     * <p>
     * Some IDE and wrapper configurations pass the complete command line as one
     * argument. This method expands only that single-element form.</p>
     *
     * @param arguments array of command line arguments
     */
    static String[] normaliseArguments(final String[] arguments) {
        if (arguments.length != 1) {
            return Arrays.copyOf(arguments, arguments.length);
        }
        final String commandLine = arguments[0];
        if (commandLine == null || commandLine.isBlank() || !containsWhitespace(commandLine)) {
            return Arrays.copyOf(arguments, arguments.length);
        }
        final List<String> tokens = new ArrayList<>();
        final StringBuilder token = new StringBuilder();
        char quote = 0;
        for (int index = 0; index < commandLine.length(); index++) {
            final char current = commandLine.charAt(index);
            if (current == '\'' || current == '"') {
                if (quote == 0) {
                    quote = current;
                } else if (quote == current) {
                    quote = 0;
                } else {
                    token.append(current);
                }
            } else if (Character.isWhitespace(current) && quote == 0) {
                addToken(tokens, token);
            } else {
                token.append(current);
            }
        }
        if (quote != 0) {
            throw new IllegalArgumentException("Unterminated quoted command-line value.");
        }
        addToken(tokens, token);
        return tokens.toArray(String[]::new);
    }

    /**
     * Check for whitespace
     *
     * @param value string to check
     */
    private static boolean containsWhitespace(final String value) {
        return value.chars().anyMatch(Character::isWhitespace);
    }

    /**
     * Tokenize the command line
     *
     * @param tokens exiting tokens
     * @param token token to add
     */
    private static void addToken(final List<String> tokens, final StringBuilder token) {
        if (!token.isEmpty()) {
            tokens.add(token.toString());
            token.setLength(0);
        }
    }

    /**
     * Prints command-line help to the supplied writer.
     *
     * @param writer output source
     */
    public void printHelp(final PrintWriter writer) {
        Objects.requireNonNull(writer, "writer");
        final var formatter = new HelpFormatter();
        formatter.setWidth(118);
        formatter.printHelp(
                writer,
                118,
                APPLICATION_NAME + " --plugin <id|all> [--plugin <id>] [--file <settings>] [options]",
                System.lineSeparator()
                + "Runs one or more OpenData plugins. Each selected plugin is submitted as an independent task."
                + System.lineSeparator() + System.lineSeparator()
                + "Examples:" + System.lineSeparator()
                + "  opendata --plugin openmeteo" + System.lineSeparator()
                + "  opendata --plugin openmeteo --plugin ofgem --parallelism 2" + System.lineSeparator()
                + "  opendata --plugin openmeteo,ofgem" + System.lineSeparator()
                + "  opendata --plugin all" + System.lineSeparator()
                + "  opendata --about" + System.lineSeparator()
                + "  opendata --plugin all --file C:\\OpenData\\run.properties" + System.lineSeparator(),
                options,
                2,
                4,
                System.lineSeparator()
                + "For a multi-plugin run, plugin overrides in --file must use plugin.<id>.<property>."
                + System.lineSeparator()
                + "Application overrides use application.<property>. Password values are never logged."
                + System.lineSeparator(),
                true);
        writer.flush();
    }

    /**
     * Build the command line options
     *
     * @return the command line options
     */
    private static Options createOptions() {
        final var result = new Options();
        result.addOption(Option
                .builder("p")
                .longOpt("plugin")
                .hasArg().argName("id|all")
                .desc("Plugin id. Repeat the option, use comma-separated ids, or specify 'all'.")
                .build());
        result.addOption(Option
                .builder("f")
                .longOpt("file")
                .hasArg()
                .argName("settings.properties")
                .desc("Optional application and plugin override properties file.")
                .build());
        result.addOption(Option
                .builder("j")
                .longOpt("parallelism")
                .hasArg()
                .argName("1-64")
                .desc("Maximum plugins executing concurrently; defaults to application configuration.")
                .build());
        result.addOption(Option
                .builder().longOpt("dry-run")
                .desc("Download and validate without database writes or run-audit rows.")
                .build());
        result.addOption(Option
                .builder("v")
                .longOpt("verbose")
                .desc("Enable FINE java.util.logging output.")
                .build());
        result.addOption(Option
                .builder("h")
                .longOpt("help")
                .desc("Display help.")
                .build());
        result.addOption(Option
                .builder()
                .longOpt("about")
                .desc("Display the graphical About and version window.")
                .build());
        result.addOption(Option
                .builder()
                .longOpt("list-plugins")
                .desc("List installed plugins.")
                .build());
        return result;
    }

    /**
     * Parse the command line
     *
     * @param commandLine the command line
     * @return the command line arguments record
     */
    private static CommandLineArguments toArguments(final CommandLine commandLine) {
        final boolean help = commandLine.hasOption("help");
        final boolean about = commandLine.hasOption("about");
        final boolean list = commandLine.hasOption("list-plugins");
        final boolean informational = help || about || list;
        final List<String> rawIds = new ArrayList<>();
        final String[] optionValues = commandLine.getOptionValues("plugin");
        if (optionValues != null) {
            for (String optionValue : optionValues) {
                for (String item : optionValue.split(",")) {
                    if (!item.isBlank()) {
                        rawIds.add(item.trim().toLowerCase(Locale.ROOT));
                    }
                }
            }
        }
        final boolean all = rawIds.stream().anyMatch("all"::equals);
        if (all && rawIds.size() > 1) {
            throw new IllegalArgumentException("--plugin all cannot be combined with another plugin id.");
        }
        final LinkedHashSet<String> uniqueIds = new LinkedHashSet<>(rawIds);
        if (uniqueIds.size() != rawIds.size()) {
            throw new IllegalArgumentException("A plugin was selected more than once.");
        }
        if (!informational && rawIds.isEmpty()) {
            throw new IllegalArgumentException("Missing required option: --plugin <id|all>.");
        }
        if (commandLine.hasOption("file") && rawIds.isEmpty()) {
            throw new IllegalArgumentException("--file requires --plugin.");
        }
        OptionalInt parallelism = OptionalInt.empty();
        if (commandLine.hasOption("parallelism")) {
            try {
                final int value = Integer.parseInt(commandLine.getOptionValue("parallelism"));
                if (value < 1 || value > 64) {
                    throw new IllegalArgumentException("--parallelism must be between 1 and 64.");
                }
                parallelism = OptionalInt.of(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("--parallelism must be an integer.", exception);
            }
        }
        return new CommandLineArguments(
                all ? List.of() : List.copyOf(uniqueIds),
                all,
                Optional.ofNullable(commandLine.getOptionValue("file")).map(Path::of),
                parallelism,
                commandLine.hasOption("dry-run"),
                commandLine.hasOption("verbose"),
                help,
                about,
                list);
    }
}
