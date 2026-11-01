# OpenData Plugin Configuration Folder

Version 3.0.0 of the JavaFX interface checks this directory when **Register** is
selected.

Place complete OpenData plugin definition files here using the `.properties`
extension. Each file must contain its own `plugin.id`, display name,
implementation class, dataset definition and the endpoint/property definitions
required by that plugin.

The GUI validates files before registration and offers only plugin ids that are
not already registered. `index.properties` is not required in this directory.

The source-tree `opendata-core/src/main/resources/config/plugins` directory is
the canonical development fallback. It holds every bundled plugin definition and
the single `index.properties` classpath catalogue used by the CLI.
Each plugin id in `index.properties` needs a matching `<id>.properties` file.
Use **Register from File** in the GUI to select a definition stored elsewhere.
