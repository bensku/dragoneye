# Requirements
The application helps game masters of tabletop roleplaying games to take notes
during games. It also allows reviewing notes from games played in past.

The application will initially support 5th edition of Dungeons and Dragons.

## The Problem
When running a tabletop roleplaying game, the game master *has* to improvise.
Even with careful planning, players will often do something unexpected. And
this is good; it is one of the advantages TTRPGs have over video games.

When improvising, the GM often needs to take notes. Unfortunately, they also
have many other things to attend to. As such, the notes written tend to be of
low quality. They may be hard to read, or be missing important information.

Specialized software created for in-game notes would reduce amount of
text the GM needs to type. It might also improve quality of the notes.

## User
The application is used locally by GM, who is the only user.

## User Interface
* Worlds view
  * Shows a list of all worlds
  * Allows creating, deleting and opening a world
* World view
  * Shows and allows editing characters in world
  * Shows a list of past games in the world
  * Allows creating, starting and deleting games
* Game view
  * Shows scrollable event log (game notes)
  * All actions that change notes support undo and redo
  * Allows creating, deleting, reordering and editing events
  * Shows overview of characters in world of the game

## Initial Functionality

### During Game
* GM may add *events* to notes
  * Events should contain timestamp when they were added
  * There should be many event types available (see "Event Types" below)
* GM may expect some events to be added to notes automatically
* GM may remove previously added events
* GM may edit events, if it makes sense with their event type
* GM may reorder events in notes
  * Timestamps should not change
* GM may undo and redo their edits to notes
* GM may stop the game
* GM may do most actions needed during game with keyboard only
  * Shortcuts should be intuitive

### Always
* GM may create and delete worlds
* GM may track player characters in worlds
  * Names, classes, proficiencies and XP (+levels)
* GM may create games in worlds, and delete games
* GM may start games and resume paused games
* GM may review notes from all games
* GM may search events, within one game or all games

## Event Types
The application needs to support following event types:
* Plain-text event
  * GM writes the full content of event, which is text
  * GM may input XP gained by event
* Short/long rest event
  * GM writes nothing
  * Resting in D&D replenishes used resources of characters
* Level up event
  * GM writes nothing
  * The event may be triggered automatically by characters receiving enough XP
  * Leveling up in D&D makes characters more powerful
* Combat event
  * GM writes overview of encounter
  * GM may input XP gained by event
  * Combat in D&D requires characters to spend resources

## Environment
* Application must work on Java 8 and 11
* Application must work on Linux
  * Java 8 or 11 may need to be installed
* Data will be stored locally on GM's computer
  * The application will not require Internet connection

## Further Development
* More event types could be added
* More character data could be tracked
* Requirement for Java installation could be dropped
  * Potential solutions: jlink, GraalVM native image, DIY solution
* Windows support should be reasonable easy to add; Java is portable