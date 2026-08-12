# OpenData GUI Specifcation 3.0

## Overview

- This is the design for a _GUI_ for running the **Opendata Processing Framework**. 
- The _GUI_ will be written using _JavaFX_ and _Java_ as this is more modern code.
- `SceneBuilder` is available for designing the _GUI_
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
	- Register from File
	- Unregister

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
		- OK runs dryrun, and shows login in dialog window

#### Details Menu

- Menu Items
	- Plug Detail
	- Logs
- Actions
	- Plugin Detail
	  - shows details of selected plugin in dialog window
	- Logs

#### Help Menu

- Menu Items
	- Help
	- separator bar
	- About

- Actions
	- Help
		- displays help just display s a brief description Of **OpenData**, needs to be displayed in a dialog box, can share the log window (See below) or can be defined in its own window
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
