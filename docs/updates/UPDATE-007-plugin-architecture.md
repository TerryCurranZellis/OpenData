# Update Instructions: `docs/architecture/007-plugin-architecture.md`

Add a section titled **Phase 1 Classpath Registry**.

The registry reads an explicit classpath index and returns metadata from each
plugin properties file. The registry is not hard-coded in `Main`.

The explicit index is portable between IDE execution and packaged JAR files.
A later database implementation will replace `ClasspathPluginRegistry` while
retaining the `PluginRegistry` interface.
