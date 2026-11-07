/**
 * Stable OpenData plugin, database-resource and configuration contracts.
 * Other OpenData modules depend on this module, never the reverse.
 *
 * @since 4.0.0
 */
module com.towermarsh.opendata.api {
    requires transitive java.sql;
    exports com.towermarsh.opendata.config.model;
    exports com.towermarsh.opendata.database;
    exports com.towermarsh.opendata.plugin;
}
