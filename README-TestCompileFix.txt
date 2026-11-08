OpenData 4.0.0 - JPMS testCompile correction

Fixes:
1. Package conflict: moves AllPluginsRegistrationTest out of the API-owned
   com.towermarsh.opendata.plugin package into Core's own
   com.towermarsh.opendata.core.plugin package.
2. Adds `requires static jdk.httpserver;` to the Core module descriptor so
   HttpDataDownloaderTest can compile its lightweight local HTTP server.

Apply to your feature/init-wizard-v4.0.0 branch:
1. Extract OpenData-4.0.0-testCompile-fix.zip into the OpenData project
   root directory (the folder containing the parent pom.xml).
2. In PowerShell from the OpenData project root, run:
       .\Apply-TestCompileFix.ps1
   This removes only the obsolete test path, after checking that the new
   test and module change are installed.
3. Run:
       mvn -pl opendata-core -am test
       git status

If script execution is blocked, remove this one obsolete file manually:
   opendata-core/src/test/java/com/towermarsh/opendata/plugin/AllPluginsRegistrationTest.java

No production Java classes or database setup logic are changed.
The full Maven test run has not been performed in this environment
(JDK 21 is available, but the project requires JDK 24+).
