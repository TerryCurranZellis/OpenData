# OpenData GUI Specifcation 3.0

## Overview

- This is the design for a _GUI_ for running the **Opendata Processing Framework**. 
- The _GUI_ will be written using _JavaFX_ rather than _Java_ _Swing_, as this is more modern code.
- `SceneBuilder` is available for building the _GUI_
- Application will be built using _Netbeans_
- Minimum Java version is Java 17, although Java 25 JDK is currently installed
- The current command line interface is required to remain as an option
- If no command line arguments are entered then the _GUI_ will run automatically
- An additional option `--gui` (short code `-g`) has been added, which will run the _GUI_ if used 

## Requirements

### SplashScreen

- JavaFX has its own `SplashScreen` that runs at application start and displays until the app starts
- the splashscreen be delayed to display for a minimum of 5 seconds, it will start end automatically once the main app starts,
so it may need a delay to be built into the startup to slow things down a little.
- Convert the existsing _Swing_ version of the splash screen

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

### Menubar

This is the list of top level items on the menubar

#### File Menu

1. Menu Items
- Settings
- Exit

2. Actiions

#### Register Menu

1. Menu Items
- Register 
- Register from File
- Unregister

2. Actions
- Register
- Register from File
- Unregister

#### Enable Menu

1. Menu Items
- Enable
- Disable

2. Actions
 - Enable
	- pop up confirm box
	- OK Enables selected plugin
	- Cancel does nothing
- Disable
	- pop up confirm box
	- OK Disables selected plugin
	- Cancel does nothing	
#### Execute Menu

1. Menu Items
- Execute
- Dryrun

2. Actions
- Execute
	- pop up confirm box
	- OK runs plugin, and shows log in dialog window
	- Cancel does nothing
- Dryrun
	- pop up confirm box
	- OK runs dryrun, and shows login in dialog window

#### Details Menu

1. Menu Items
- Plug Detail
	- shows details of selected plugin in dialog window
- Logs

2. Actions
- Plugin Detail
  - shows details of selected plugin in dialog window
- Logs

#### Help Menu

1. Menu Items
- Help
- separator bar
- About

2. Actions
- Help
	- displays help just display s a brief description Of **OpenData**, needs to be displayed in a dialog box, can share the log window (See below) or can be defined in its own window
- About
	- display the about box, currently defined using Swing to may need to be rewritten
	- shows the splashscreen image
	- shows specified text in a text box
	- has an OK button, clicking the ok button closes the about box
	
### Popup Confirm Box
- Box has a message depending on the option selected
- Has ok button to confirm
- has cancel button to cancel option
- Action on clicking OK button
	- Details - show selected plugin details in log Window
	- Execute - execute plugin, and disply log in log Window
	- Dryrun - dryrun plugin, and display log in log Window
	- Enable - enable plugin
	- Disable - disable plugin
	- Unregister - unregister plugin
- Action on clicking Cancel button
	- cancel select action and retur

## Log Window

This is a popup dialog box that displays log messages

### Execute or Dryrun

- Pops up when Execute or Dryrun, 
- Displays the log file in the Window as plugin(s) runs
- has a close button at centred at the bottom labelled "Close"
- Close button is enabled once the Execute or Dryrun completes
- Needs scrollbars, to scroll up and down the log display
- Pops up when Details is seleected
- Disaplays the Plugin details
