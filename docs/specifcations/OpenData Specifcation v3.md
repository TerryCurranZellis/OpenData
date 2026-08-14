# OpenData GUI Specifcation 3.1

## Overview

- This is the design for a _GUI_ for running the **Opendata Processing Framework**. 
- The _GUI_ will be written using _JavaFX_ and _Java_ as this is more modern code.
- as much of the JavaFX details will be loaded from the `.fxml` file
- `SceneBuilder` is available for designing the _GUI_
- Application will be built using _Netbeans_
- Minimum Java version is Java 24; Java 25 JDK is currently used for development
- The current command line interface is to remain as an option
- If no command line arguments are entered then the _GUI_ will run automatically
- An additional option `--gui` (short code `-g`) has been added, which will run the _GUI_ if used
- Code should be created in its own GUI package(s)
- Existing and future ui packages can be moved into the gui package(s)
- GUI code added or materially changed from Batch 2 onwards should use `@version 3.1.0`; Batch 1 source remains the 3.0.0 baseline
- Update all documentation, this will also require relevent ADR documents as its a new direction
- Screenshots of the _GUI_ will be required in the documentation, I will generate these as `.png` with 
the `docs\diagrams\source` as they will be required in the documentation, they will need to be copied into the `doc\diagrams\generated` folder to be included
in the documentation.
- Create a table of proposed screenshots and the locations within the documents, and I will generate the screenshots with corresponding names.

## Requirements

### SplashScreen

- Use a dedicated JavaFX splash `Stage` at graphical application startup.
- The splash screen must display for a minimum of 5 seconds and close automatically as the main window is shown.
- The minimum delay must not block the JavaFX application thread.
- The existing Swing splash implementation is deprecated from version 3.1.0 and replaced by the JavaFX startup path.
- Other Swing UI remnants are to be marked deprecated until their JavaFX replacements are implemented.

## Main Screen

This is the basic layout of the main screen, see image of the basic layout.

- Full Screen
- Menubar at top of screen displaying main menu items
- Menu items
  - File menu
  - Register menu
  - Enable menu
  - Execute menu
  - Details menu
  - Help menu
- Toobar below MenuBar
- Toolbar Icons

| Label | Icon |
|---|---|
| Exit | exit.png |
| Preferences | preferences.png |
| Save | Save | save.png |
| Register | register.png
| Unregister | unregister.png|   
| Enable | enable.png |
| Disable | disable.png |
| Execute | execute.png |
| Dryrun | dryrun.png |
| Logfile | 
| About | about.png |
| Help | help.png |

- Main Window
  - Table containing plugin details
  - Columns
    - Selected/Not selected box
    - plugin id
    - plugin description
    - enabled/disabled could be shown as a flag column to indicate
    - Status 
      - show the status of the last time the plugin was run only needs to show status or error status
      - leave blank if plugin has never been run
    - Date of last run
      - leave blank if plugin never ran
    
- Lower left
  - Status 
      - loading while plugin details being retrieved
    - 'ready' once everything has been loaded from the database

- Lower right
  - Number of Items selected
### Menubar

This is the list of top level items on the menubar

#### File Menu

- Menu Items
   - Settings
   - Exit
- Actiions

#### Register Menu

- Menu Items
  - Register 
  - Register from File
  - Unregister
- Actions
  - Register
    - scan the OpenData plugin configuration folder for new `.properties` files
    - validate discovered plugin definitions
    - ignore definitions whose `plugin.id` is already registered
    - show discovered plugins in an OK/Cancel confirmation box
    - OK registers all discovered new plugins
    - Cancel makes no changes
  - Register from File
    - open a JavaFX file chooser
    - select one plugin `.properties` file
    - validate and register the selected definition using its `plugin.id`
  - Unregister
    - requires one or more selected plugins
    - show an OK/Cancel confirmation box
    - OK unregisters selected plugins
    - Cancel makes no changes

#### Enable Menu

- Menu Items
  - Enable
  - Disable

- Actions
   - Enable
    - pop up confirm box
    - OK Enables selected plugin
    - Cancel does nothing
  - Disable
    - pop up confirm box
    - OK Disables selected plugin
    - Cancel does nothing  

#### Execute Menu

- Menu Items
  - Execute
  - Dryrun

- Actions
  - Execute
    - pop up confirm box
    - OK runs plugin, and shows log in dialog window
    - Cancel does nothing
  - Dryrun
    - pop up confirm box
    - OK runs dryrun, and shows log in dialog window

#### Details Menu

- Menu Items
  - Plugin Detail
  - Logs
- Actions
  - Plugin Detail
    - shows details of selected plugin in dialog window
      - two column output showing property and value
    - ok button at bottom of dialog window to close the dialog
  - Logs
      - loads the run log into a dialog window user can scroll around the window and review the current log
    - may need scroll bars for longer lined

#### Help Menu

- Menu Items
  - Help
  - separator bar
  - About

- Actions
  - Help
    - displays help just display s a brief description Of **OpenData**, needs to be displayed in a dialog box, can share the log window (See below) or 
    can be defined in its own window
    - I am working on a windows compiled help file, so it may be able to be displayed in its own dialog box
  - About
    - display the about box, currently defined using Swing to may need to be rewritten
    - shows the splashscreen image
    - shows specified text in a text box
    - has an OK button, clicking the ok button closes the about box

### Toolbar Buttons

Actions

### Popup Confirm Box

- Description
    - Requires item in the main window to be selected
  - Box has a message depending on the option selected
  - Has `OK` button to confirm action
  - has `Cancel' button to cancel action
- Actions
  - If no items selected then Display Warning message "No plugin selected"
  - `OK` button 
      - Unregister - unregister plugin
      - Enable - enable plugin
    - Disable - disable plugin
    - Execute - execute plugin, and disply log in log Window
    - Dryrun - dryrun plugin, and display log in log Window
    - Details - show selected plugin details in log Window
  - `Cancel` button
    - cancel select action and retur
  - once action is confirmed then run the appropriate action as a new task

## Log Window

This is a popup dialog box that displays log messages

### Execute or Dryrun

- Pops up when Execute or Dryrun.
- Selected plugin ids are snapshotted before the background task begins.
- Displays live JUL output in the Window as plugin(s) run; existing file/console logging remains active.
- Has a Close button centred at the bottom labelled `Close`.
- Close and window-close actions remain disabled while processing is active.
- Close becomes enabled once Execute or Dryrun completes or fails.
- Needs scrollbars to review the complete log display; long lines must remain horizontally scrollable.
- Normal Execute records the usual run audit and may write provider data.
- Dryrun uses the existing side-effect-free dry-run contract and must not write provider data or generic run-audit rows.

### Detail Popup

- dialog box
  - Pops up when Details is selected
  - Displays the Plugin details
  - two column output property and value
  - ok button is always available
