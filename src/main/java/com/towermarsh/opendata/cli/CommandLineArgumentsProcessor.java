/*
 * Filename: CommandLineArgumentsProcessor.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.cli;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
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

/** Parses and validates the OpenData command line. */
public final class CommandLineArgumentsProcessor {
    private static final String APPLICATION_NAME = "opendata";
    private final Options options = createOptions();

    public CommandLineArguments parse(final String[] arguments) {
        Objects.requireNonNull(arguments, "arguments");
        try {
            return toArguments(new DefaultParser().parse(options, arguments));
        } catch (ParseException | IllegalArgumentException exception) {
            throw new CommandLineProcessingException(exception.getMessage(), exception);
        }
    }

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

    private static Options createOptions() {
        final var result = new Options();
        result.addOption(Option.builder("p")
                .longOpt("plugin")
                .hasArg()
                .argName("id|all")
                .desc("Plugin id. Repeat the option, use comma-separated ids, or specify 'all'.")
                .build());
        result.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg()
                .argName("settings.properties")
                .desc("Optional application and plugin override properties file.")
                .build());
        result.addOption(Option.builder("j")
                .longOpt("parallelism")
                .hasArg()
                .argName("1-64")
                .desc("Maximum plugins executing concurrently; defaults to application configuration.")
                .build());
        result.addOption(Option.builder().longOpt("dry-run")
                .desc("Download and validate without database writes or run-audit rows.").build());
        result.addOption(Option.builder("v").longOpt("verbose")
                .desc("Enable FINE java.util.logging output.").build());
        result.addOption(Option.builder("h").longOpt("help").desc("Display help.").build());
        result.addOption(Option.builder().longOpt("version").desc("Display version.").build());
        result.addOption(Option.builder().longOpt("list-plugins").desc("List installed plugins.").build());
        return result;
    }

    private static CommandLineArguments toArguments(final CommandLine commandLine) {
        final boolean help = commandLine.hasOption("help");
        final boolean version = commandLine.hasOption("version");
        final boolean list = commandLine.hasOption("list-plugins");
        final boolean informational = help || version || list;
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
                version,
                list);
    }
}
