module com.towermarsh.opendata.plugin.octopus {
    requires com.towermarsh.opendata.api;
    requires com.towermarsh.opendata.common;
    requires org.apache.pdfbox;
    requires java.logging;
    requires java.sql;
    exports com.towermarsh.opendata.plugin.octopus;
    exports com.towermarsh.opendata.plugin.octopus.transform;
    exports com.towermarsh.opendata.plugin.octopus.transform.model;
}
