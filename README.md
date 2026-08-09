# OpenData JavaFX FXML sample

This sample separates the JavaFX GUI from the program that starts it.

## Startup path

```text
com.towermarsh.opendata.OpenDataGuiLauncher.main()
        |
        v
com.towermarsh.opendata.gui.OpenDataGuiApplication.launchGui()
        |
        v
JavaFX Application.start()
        |
        v
OpenDataMainView.fxml
        |
        v
OpenDataMainController
```

`OpenDataGuiLauncher` is deliberately outside the `gui` package. It is only a
sample launcher and does not modify or replace the existing OpenData main class.

Later, the existing OpenData main program can start the GUI by calling:

```java
OpenDataGuiApplication.launchGui(args);
```

That integration is intentionally not made in this sample.

## Scene Builder

Open this file in Scene Builder:

```text
src/main/resources/com/towermarsh/opendata/gui/OpenDataMainView.fxml
```

The FXML declares:

```text
fx:controller="com.towermarsh.opendata.gui.OpenDataMainController"
```

If Scene Builder is configured with this Maven project, it can resolve the
controller. The FXML remains ordinary Scene Builder-editable FXML.

## Run

```powershell
mvn clean javafx:run
```

The Maven JavaFX plugin runs `com.towermarsh.opendata.OpenDataGuiLauncher`, not
the JavaFX application class directly.

## Current behaviour

The table is populated with sample Ofgem, Open-Meteo and Octopus rows. Menu and
toolbar actions only update the status bar. They are placeholders for future
OpenData application services and do not yet execute plugin operations.
