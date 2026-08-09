# OpenData GUI Specifcation 3.0

## Overview

- This is the design for a GUI for running the Opendata Processing Framework. 
- The GUI will be written using JavaFX rather thand swing, as this is more modern code.
- SceneBuilder is available for building the GUI
- Application will be built using Netbeans
- Minimum Java version is Java 17, although Java 25 JDK is currently installed
- The current command line interface is required

## Requirements

### SplashScreen

- current startup display a Splashscreen which has an image. This uses swing, so may need to be converted to work with JavaFX

## Main Screen

- Full Screen
- Menubar at top of screen displaying main menu items
- Menu items
	- File menu
	- Register menu
	- Enable menu
	- Execute menu
	- Details menu
	- Help/About menu
- Main Window

### Menubar

This is the list of top level items on the menubar

#### File

- Preferences
	- Allows the databse to be changed
	- Allows the database password to be reset
- Exit
	- Closes the application

#### Register

- Register 
	- Plugin using default file
- Register from File
	- Register Using a Selected File
	- File is selected usinng a file selection window
- Unregister All
- Unregister selected
	- unregisters selected plugin

#### Enable

- Enable All
- Enable Selected
	- pop up confirm box
	- OK Enables selected plugin
	- Cancel does nothing
- Disable All
- Disable Selected
	- pop up confirm box
	- OK Disables selected plugin
	- Cancel does nothing
	
#### Execute

- Execute All
- Execute Selected
	- pop up confirm box
	- OK runs plugin, and shows log in dialog window
	- Cancel does nothing
- Dryrun All
- Dryrun Selected
	- pop up confirm box
	- OK runs dryrun, and shows login in dialog window

#### Details

- shows details of selected plugin in dialog window

#### Help

Displays the following items

- Help
	- displays help just display s a brief description Of OpenData, needs to be displayed in a dialog box, can share the log window (See below) or can be defined
in its own window
- separator bar
- About
	- display the about box, currently defined using Swing to may need to be rewritten
	- shows the splashscreen image
	- shows specified text in a text box
	- has an OK button, clicking the ok button closes the about box

## Popup Menu

- this menu will popup when and item is selected in the main window and then the item is right clicked
- Options on the menu are as follwd
	- Details - pops up confrim box
	- Enable - pops up confirm box
	- Disable - pops up a confirm box
	- Unregister - pops up confrim box
	- Execute - pops up confirm box
	- Dry Run - pops up conform box
	
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