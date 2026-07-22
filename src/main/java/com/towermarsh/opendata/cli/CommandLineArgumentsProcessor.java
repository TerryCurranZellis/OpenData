package com.towermarsh.opendata.cli;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Parses and validates the OpenData Framework command line using
 * Apache Commons CLI.
 */
public final class CommandLineArgumentsProcessor {

    private static final String APPLICATION_NAME = "opendata";

    private final Options options;

    public CommandLineArgumentsProcessor() {
        this.options = createOptions();
    }

    /**
     * Parses command-line arguments.
     *
     * @param arguments arguments passed to the Java application
     * @return immutable parsed arguments
     * @throws CommandLineProcessingException if parsing or validation fails
     */
    public CommandLineArguments parse(final String[] arguments) {
        Objects.requireNonNull(arguments, "arguments");

        try {
            final CommandLine commandLine = new DefaultParser().parse(options, arguments);
            return toArguments(commandLine);
        } catch (ParseException | IllegalStateException exception) {
            throw new CommandLineProcessingException(exception.getMessage(), exception);
        }
    }

    /**
     * Prints command-line help.
     *
     * @param writer destination writer
     */
    public void printHelp(final PrintWriter writer) {
        Objects.requireNonNull(writer, "writer");

        final HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(110);
        formatter.printHelp(
                writer,
                110,
                APPLICATION_NAME + " --plugin <id> [--file <settings.properties>] [options]",
                System.lineSeparator()
                        + "Processes an OpenData dataset plugin. Plugin defaults are loaded first; "
                        + "the optional file overrides those values."
                        + System.lineSeparator()
                        + System.lineSeparator()
                        + "Examples:"
                        + System.lineSeparator()
                        + "  opendata --plugin ofgem"
                        + System.lineSeparator()
                        + "  opendata --plugin ofgem --file C:\\OpenData\\ofgem-local.properties"
                        + System.lineSeparator(),
                options,
                2,
                4,
                System.lineSeparator()
                        + "Configuration precedence:"
                        + System.lineSeparator()
                        + "  1. framework built-in defaults"
                        + System.lineSeparator()
                        + "  2. classpath config/application.properties"
                        + System.lineSeparator()
                        + "  3. classpath config/plugins/<plugin>.properties"
                        + System.lineSeparator()
                        + "  4. --file override properties"
                        + System.lineSeparator(),
                true);
        writer.flush();
    }

    private static Options createOptions() {
        final Options result = new Options();

        result.addOption(Option.builder("p")
                .longOpt("plugin")
                .hasArg()
                .argName("id")
                .desc("Dataset plugin to execute, for example 'ofgem'.")
                .build());

        result.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg()
                .argName("settings.properties")
                .desc("Optional properties file overriding the selected plugin defaults.")
                .build());

        result.addOption(Option.builder()
                .longOpt("dry-run")
                .desc("Validate and prepare processing without writing data.")
                .build());

        result.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("Enable more detailed application logging.")
                .build());

        result.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Display command-line help.")
                .build());

        result.addOption(Option.builder()
                .longOpt("version")
                .desc("Display application version.")
                .build());

        result.addOption(Option.builder()
                .longOpt("list-plugins")
                .desc("List installed dataset plugins.")
                .build());

        return result;
    }

    private static CommandLineArguments toArguments(final CommandLine commandLine) {
        final boolean informational = commandLine.hasOption("help")
                || commandLine.hasOption("version")
                || commandLine.hasOption("list-plugins");

        if (!informational && !commandLine.hasOption("plugin")) {
            throw new IllegalStateException("Missing required option: --plugin <id>");
        }

        if (commandLine.hasOption("file") && !commandLine.hasOption("plugin")) {
            throw new IllegalStateException("--file can only be used with --plugin.");
        }

        final CommandLineArguments.Builder builder = CommandLineArguments.builder()
                .helpRequested(commandLine.hasOption("help"))
                .versionRequested(commandLine.hasOption("version"))
                .listPluginsRequested(commandLine.hasOption("list-plugins"))
                .dryRun(commandLine.hasOption("dry-run"))
                .verbose(commandLine.hasOption("verbose"));

        if (commandLine.hasOption("plugin")) {
            builder.pluginId(commandLine.getOptionValue("plugin"));
        }

        if (commandLine.hasOption("file")) {
            builder.overrideFile(Path.of(commandLine.getOptionValue("file")));
        }

        return builder.build();
    }
}
