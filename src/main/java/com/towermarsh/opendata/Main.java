package com.towermarsh.opendata;

import java.time.Duration;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.towermarsh.opendata.app.ApplicationRunStatus;
import com.towermarsh.opendata.cli.CommandLineArgumentsProcessor;
import com.towermarsh.opendata.cli.CommandLineProcessingException;
import com.towermarsh.opendata.config.ConfigurationService;
import com.towermarsh.opendata.exception.ConfigurationException;

/**
 * Minimal integration example for command-line and configuration processing.
 */
public final class Main {

    // set up a logger
    protected static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Main entry point
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final var startTime = System.nanoTime();
        var runStatus = ApplicationRunStatus.SUCCESS;
        final var processor = new CommandLineArgumentsProcessor();
        try {
            final var arguments = processor.parse(args);
            if (arguments.helpRequested()) {
                processor.printHelp(new PrintWriter(System.out, true));
                return;
            }
            if (arguments.versionRequested()) {
                System.out.println("OpenData Framework 0.1.0-SNAPSHOT");
                return;
            }
            if (arguments.listPluginsRequested()) {
                System.out.println("Installed plugins: ofgem");
                return;
            }
            final var configuration = new ConfigurationService().resolve(arguments);
            LOGGER.info(() -> "Selected plugin: " + configuration.pluginId());
            LOGGER.info(() -> "Dry run: " + configuration.dryRun());
            LOGGER.info(() -> "Resolved properties: " + configuration.runtimeOverrides().size());
            // Hand configuration to the plugin registry / pipeline engine here.
        } catch (CommandLineProcessingException exception) {
            System.err.println("Command-line error: " + exception.getMessage());
            processor.printHelp(new PrintWriter(System.err, true));
            runStatus = ApplicationRunStatus.COMMAND_LINE_ERROR;
        } catch (ConfigurationException exception) {
            LOGGER.log(Level.SEVERE, "Configuration error: {0}", exception.getMessage());
            runStatus = ApplicationRunStatus.CONFIGURATION_ERROR;
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unexpected application failure.", exception);
            runStatus = ApplicationRunStatus.APPLICATION_ERROR;
        } finally {
            final var elapsedNanoseconds = System.nanoTime() - startTime;
            final var duration = Duration.ofNanos(elapsedNanoseconds);
            LOGGER.log(Level.INFO, "Application finished. Status: {0}. Duration: {1}.",
                    new Object[]{
                        runStatus.displayName(),
                        formatDuration(duration)
                    });
        }
    }

    /**
     * format duration into hours, mins, sec, etc
     *
     * @param duration elapsed time
     * @return formatted time
     */
    private static String formatDuration(
            final Duration duration) {
        var hours = duration.toHours();
        var minutes = duration.toMinutesPart();
        var seconds = duration.toSecondsPart();
        var milliseconds = duration.toMillisPart();
        return "%02d:%02d:%02d.%03d".formatted(hours, minutes, seconds, milliseconds);
    }
}
