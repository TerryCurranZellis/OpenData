package com.towermarsh.opendata.cli;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable representation of the supported OpenData command-line arguments.
 */
public final class CommandLineArguments {

    private final String pluginId;
    private final Path overrideFile;
    private final boolean helpRequested;
    private final boolean versionRequested;
    private final boolean listPluginsRequested;
    private final boolean dryRun;
    private final boolean verbose;

    private CommandLineArguments(final Builder builder) {
        this.pluginId = builder.pluginId;
        this.overrideFile = builder.overrideFile;
        this.helpRequested = builder.helpRequested;
        this.versionRequested = builder.versionRequested;
        this.listPluginsRequested = builder.listPluginsRequested;
        this.dryRun = builder.dryRun;
        this.verbose = builder.verbose;
    }

    public Optional<String> pluginId() {
        return Optional.ofNullable(pluginId);
    }

    public Optional<Path> overrideFile() {
        return Optional.ofNullable(overrideFile);
    }

    public boolean helpRequested() {
        return helpRequested;
    }

    public boolean versionRequested() {
        return versionRequested;
    }

    public boolean listPluginsRequested() {
        return listPluginsRequested;
    }

    public boolean dryRun() {
        return dryRun;
    }

    public boolean verbose() {
        return verbose;
    }

    /**
     * Indicates whether the invocation is informational and therefore does not
     * require a plugin to be selected.
     *
     * @return true for help, version or plugin-list requests
     */
    public boolean isInformationalRequest() {
        return helpRequested || versionRequested || listPluginsRequested;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String pluginId;
        private Path overrideFile;
        private boolean helpRequested;
        private boolean versionRequested;
        private boolean listPluginsRequested;
        private boolean dryRun;
        private boolean verbose;

        private Builder() {
        }

        public Builder pluginId(final String value) {
            this.pluginId = normalisePluginId(value);
            return this;
        }

        public Builder overrideFile(final Path value) {
            this.overrideFile = value == null ? null : value.toAbsolutePath().normalize();
            return this;
        }

        public Builder helpRequested(final boolean value) {
            this.helpRequested = value;
            return this;
        }

        public Builder versionRequested(final boolean value) {
            this.versionRequested = value;
            return this;
        }

        public Builder listPluginsRequested(final boolean value) {
            this.listPluginsRequested = value;
            return this;
        }

        public Builder dryRun(final boolean value) {
            this.dryRun = value;
            return this;
        }

        public Builder verbose(final boolean value) {
            this.verbose = value;
            return this;
        }

        public CommandLineArguments build() {
            if (!helpRequested && !versionRequested && !listPluginsRequested
                    && (pluginId == null || pluginId.isBlank())) {
                throw new IllegalStateException(
                        "A plugin must be specified unless help, version or list-plugins was requested.");
            }
            return new CommandLineArguments(this);
        }

        private static String normalisePluginId(final String value) {
            if (value == null) {
                return null;
            }
            final String normalised = value.trim().toLowerCase(java.util.Locale.ROOT);
            return normalised.isEmpty() ? null : normalised;
        }
    }
}
