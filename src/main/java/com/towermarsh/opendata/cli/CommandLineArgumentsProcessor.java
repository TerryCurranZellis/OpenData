\
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
 * @author Terry Curran
 * @version 2.0.0
 */
public final class CommandLineArgumentsProcessor {

    /**
     * the application
     */
    private static final String APPLICATION_NAME = "OpenData";
    /**
     * command line options
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
            final var normalisedArguments = normaliseArguments(arguments);
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
     * @return normalised argument array
     */
    static String[] normaliseArguments(final String[] arguments) {
        if (arguments.length != 1) {
            return Arrays.copyOf(arguments, arguments.length);
        }
        final var commandLine = arguments[0];
        if (commandLine == null || commandLine.isBlank() || !containsWhitespace(commandLine)) {
            return Arrays.copyOf(arguments, arguments.length);
        }
        final List<String> tokens = new ArrayList<>();
        final var token = new StringBuilder();
        char quote = 0;
        for (var index = 0; index < commandLine.length(); index++) {
            final var current = commandLine.charAt(index);
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
     * check if a string contains white space
     *
     * @param value string to check
     * @return true or false
     */
    private static boolean containsWhitespace(final String value) {
        return value.chars().anyMatch(Character::isWhitespace);
    }

    /**
     * add token to the list
     *
     * @param tokens token list
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
     * @param writer output destination
     */
    public void printHelp(final PrintWriter writer) {
        Objects.requireNonNull(writer, "writer");
        final var formatter = new HelpFormatter();
        formatter.setWidth(124);
        formatter.printHelp(
                writer,
                124,
                APPLICATION_NAME + " --plugin <id|all> [--plugin <id>] (--Execute [run options] | --detail | [operation] [options])",
                System.lineSeparator()
                + "Runs or administers registered OpenData plugins. Named --plugin options may be repeated."
                + System.lineSeparator() + System.lineSeparator()
                + "Run examples:" + System.lineSeparator()
                + "  opendata --plugin openmeteo --Execute" + System.lineSeparator()
                + "  opendata --plugin openmeteo --plugin ofgem --Execute --parallelism 2" + System.lineSeparator()
                + "  opendata --plugin all --Execute --dry-run" + System.lineSeparator()
                + System.lineSeparator()
                + "Plugin information example:" + System.lineSeparator()
                + "  opendata --plugin ofgem --detail" + System.lineSeparator()
                + System.lineSeparator()
                + "Administration examples:" + System.lineSeparator()
                + "  opendata --plugin all --register" + System.lineSeparator()
                + "  opendata --plugin example --register --file C:\\OpenData\\example.properties" + System.lineSeparator()
                + "  opendata --plugin octopus --disable" + System.lineSeparator()
                + "  opendata --plugin octopus --enable" + System.lineSeparator()
                + "  opendata --plugin octopus --unregister" + System.lineSeparator()
                + "  opendata --list-plugins" + System.lineSeparator(),
                options,
                2,
                4,
                System.lineSeparator()
                + "--Execute (-x) is required for normal and dry-run plugin execution."
                + System.lineSeparator()
                + "--Execute cannot be combined with plugin information or administration operations."
                + System.lineSeparator()
                + "--detail displays stored configuration for exactly one named registered plugin."
                + System.lineSeparator()
                + "Exactly one of --register, --unregister/--remove, --enable, --disable, or --detail may be used."
                + System.lineSeparator()
                + "The -d short option means --disable. Use -n or --dry-run for dry-run execution."
                + System.lineSeparator()
                + "--file is accepted only with --register and one named plugin; it cannot be used with 'all'."
                + System.lineSeparator()
                + "--parallelism accepts 1-64 and affects only run and dry-run execution."
                + System.lineSeparator(),
                true);
        writer.flush();
    }

    /**
     * create all the command line options
     *
     * @return the available command line options
     */
    private static Options createOptions() {
        final var result = new Options();
        result.addOption(Option.builder("p")
                .longOpt("plugin")
                .hasArg().argName("id|all")
                .desc("Plugin id. Repeat the option, use comma-separated ids, or specify 'all'.")
                .get());
        result.addOption(Option.builder("x")
                .longOpt("Execute")
                .desc("Explicitly authorise plugin execution; required for normal and dry-run execution.")
                .get());
        result.addOption(Option.builder()
                .longOpt("detail")
                .desc("Display stored configuration for one named registered plugin.")
                .get());
        result.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg().argName("plugin.properties")
                .desc("Plugin definition file; requires --register and one named plugin.")
                .get());
        result.addOption(Option.builder("j")
                .longOpt("parallelism")
                .hasArg().argName("1-64")
                .desc("Maximum plugins executing concurrently; effective only for runs and dry-runs.")
                .get());
        result.addOption(Option.builder("r")
                .longOpt("register")
                .desc("Register or replace the selected plugin definitions.")
                .get());
        result.addOption(Option.builder("u")
                .longOpt("unregister")
                .desc("Remove the selected plugins and their stored configuration.")
                .get());
        result.addOption(Option.builder()
                .longOpt("remove")
                .desc("Alias for --unregister.")
                .get());
        result.addOption(Option.builder("e")
                .longOpt("enable")
                .desc("Enable the selected registered plugins.")
                .get());
        result.addOption(Option.builder("d")
                .longOpt("disable")
                .desc("Disable the selected registered plugins.")
                .get());
        result.addOption(Option.builder("n")
                .longOpt("dry-run")
                .desc("Run without plugin data writes or run-audit rows; requires --Execute.")
                .get());
        result.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("Enable FINE java.util.logging output.")
                .get());
        result.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Display help.")
                .get());
        result.addOption(Option.builder("a")
                .longOpt("about")
                .desc("Display the graphical About and version window.")
                .get());
        result.addOption(Option.builder("l")
                .longOpt("list-plugins")
                .desc("List registered plugins and enabled/disabled status.")
                .get());
        return result;
    }

    private static CommandLineArguments toArguments(final CommandLine commandLine) {
        final var help = commandLine.hasOption("help");
        final var about = commandLine.hasOption("about");
        final var list = commandLine.hasOption("list-plugins");
        final var informationalCount = booleanCount(help, about, list);
        if (informationalCount > 1) {
            throw new IllegalArgumentException("Use only one of --help, --about, or --list-plugins.");
        }

        final List<String> rawIds = parsePluginIds(commandLine);
        final var all = rawIds.stream().anyMatch("all"::equals);
        if (all && rawIds.size() > 1) {
            throw new IllegalArgumentException("--plugin all cannot be combined with another plugin id.");
        }
        final var uniqueIds = new LinkedHashSet<String>(rawIds);
        if (uniqueIds.size() != rawIds.size()) {
            throw new IllegalArgumentException("A plugin was selected more than once.");
        }

        final var register = commandLine.hasOption("register");
        final var unregister = commandLine.hasOption("unregister") || commandLine.hasOption("remove");
        if (commandLine.hasOption("unregister") && commandLine.hasOption("remove")) {
            throw new IllegalArgumentException("--unregister and --remove are aliases; specify only one.");
        }
        final var enable = commandLine.hasOption("enable");
        final var disable = commandLine.hasOption("disable");
        final var detail = commandLine.hasOption("detail");
        final var actionCount = booleanCount(register, unregister, enable, disable, detail);
        if (actionCount > 1) {
            throw new IllegalArgumentException(
                    "--register, --unregister/--remove, --enable, --disable, and --detail are mutually exclusive.");
        }
        final var command = register ? PluginCommand.REGISTER
                : unregister ? PluginCommand.UNREGISTER
                        : enable ? PluginCommand.ENABLE
                                : disable ? PluginCommand.DISABLE
                                        : detail ? PluginCommand.DETAIL
                                                : PluginCommand.RUN;

        final var execute = commandLine.hasOption("Execute");
        final var dryRun = commandLine.hasOption("dry-run");
        final var fileSpecified = commandLine.hasOption("file");
        final var parallelismSpecified = commandLine.hasOption("parallelism");
        final var pluginSpecified = !rawIds.isEmpty();
        final var informational = informationalCount == 1;

        if (informational
                && (pluginSpecified || actionCount > 0 || execute || dryRun || fileSpecified || parallelismSpecified)) {
            throw new IllegalArgumentException(
                    "Informational options cannot be combined with plugin selection or operational options.");
        }
        if (!informational && !pluginSpecified) {
            throw new IllegalArgumentException("Missing required option: --plugin <id|all>.");
        }
        if (detail && all) {
            throw new IllegalArgumentException(
                    "--detail requires exactly one named plugin; it cannot be used with --plugin all.");
        }
        if (detail && uniqueIds.size() != 1) {
            throw new IllegalArgumentException(
                    "--detail requires exactly one named plugin.");
        }
        if (execute && command != PluginCommand.RUN) {
            throw new IllegalArgumentException(
                    "--Execute cannot be combined with a plugin information or administration operation.");
        }
        if (!informational && command == PluginCommand.RUN && !execute) {
            throw new IllegalArgumentException(
                    "Missing required option for plugin execution: --Execute (-x).");
        }
        if (dryRun && command != PluginCommand.RUN) {
            throw new IllegalArgumentException(
                    "--dry-run cannot be combined with a plugin information or administration operation.");
        }
        if (fileSpecified && command != PluginCommand.REGISTER) {
            throw new IllegalArgumentException("--file requires --register.");
        }
        if (fileSpecified && all) {
            throw new IllegalArgumentException("--file cannot be used with --plugin all.");
        }
        if (fileSpecified && uniqueIds.size() != 1) {
            throw new IllegalArgumentException("--file requires exactly one named --plugin value.");
        }

        var parallelism = OptionalInt.empty();
        if (parallelismSpecified) {
            try {
                final var value = Integer.parseInt(commandLine.getOptionValue("parallelism"));
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
                dryRun,
                execute,
                commandLine.hasOption("verbose"),
                help,
                about,
                list,
                command);
    }

    /**
     * find the plugins on the command line
     *
     * @param commandLine the commandline
     * @return plugin ids
     */
    private static List<String> parsePluginIds(final CommandLine commandLine) {
        final List<String> rawIds = new ArrayList<>();
        final var optionValues = commandLine.getOptionValues("plugin");
        if (optionValues == null) {
            return rawIds;
        }
        for (var optionValue : optionValues) {
            for (var item : optionValue.split(",")) {
                if (!item.isBlank()) {
                    rawIds.add(item.trim().toLowerCase(Locale.ROOT));
                }
            }
        }
        return rawIds;
    }

    /**
     * count the number of information arguments
     *
     * @param values information arguments
     * @return number of information arguments
     */
    private static int booleanCount(final boolean... values) {
        var count = 0;
        for (var value : values) {
            if (value) {
                count++;
            }
        }
        return count;
    }
}
