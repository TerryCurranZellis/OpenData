# OpenData Plugin Configuration Folder

Version 3.0.0 of the JavaFX interface checks this directory when **Register** is
selected.

Place complete OpenData plugin definition files here using the `.properties`
extension. Each file must contain its own `plugin.id`, display name,
implementation class, dataset definition and the endpoint/property definitions
required by that plugin.

The GUI validates files before registration and offers only plugin ids that are
not already registered. `index.properties` is not required in this directory.

Each provider project under `opendata-plugins` owns its definition and an
`index.properties` classpath catalogue listing only that plugin. The GUI
development fallback scans those project resource folders. Provider projects
included in the application are added as dependencies of `opendata-core`.
Use **Register from File** in the GUI to select a definition stored elsewhere.
