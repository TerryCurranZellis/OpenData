package com.towermarsh.opendata.config;

/**
 * Records where a resolved property value originated.
 */
public enum ConfigurationSource {
    BUILT_IN_DEFAULT,
    APPLICATION_CLASSPATH,
    PLUGIN_CLASSPATH,
    OVERRIDE_FILE
}
