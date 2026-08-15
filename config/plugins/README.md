# OpenData Plugin Configuration Folder

Version 3.0.0 of the JavaFX interface checks this directory when **Register** is
selected.

Place complete OpenData plugin definition files here using the `.properties`
extension. Each file must contain its own `plugin.id`, display name,
implementation class, dataset definition and the endpoint/property definitions
required by that plugin.

The GUI validates files before registration and offers only plugin ids that are
not already registered. `index.properties` is not required in this directory.

The source-tree `src/main/resources/config/plugins` directory remains a fallback
for development and contains packaged definitions used by the CLI catalogue.
Use **Register from File** in the GUI to select a definition stored elsewhere.
