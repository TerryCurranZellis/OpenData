# OpenData Plugin Configuration Folder

Version 3.0.0 of the JavaFX interface checks this directory when **Register** is
selected.

Place complete OpenData plugin definition files here using the `.properties`
extension. Each file must contain its own `plugin.id`, display name,
implementation class, dataset definition and the endpoint/property definitions
required by that plugin.

The GUI validates files before registration and offers only plugin ids that are
not already registered. `index.properties` is not required in this directory.

The source-tree `opendata-plugins/opendata-plugin-*/src/main/resources/config/plugins`
directories are the canonical development fallbacks. Each bundled plugin module
owns its own packaged definition resources, and the CLI catalogue merges the
classpath indexes contributed by those modules.
Use **Register from File** in the GUI to select a definition stored elsewhere.
