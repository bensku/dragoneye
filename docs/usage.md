# Usage

## Concepts
A **game world** contains a set of **player characters** and **games**.
It represents what most RPG campaigns are; many games, played over a long time
with characters changing only rarely.

Dragoneye keeps track of **player characters** to tell you when they have
accumulated enough XP to level up. Full character sheets are not stored.

**Games** store notes taken during *games* you were GMing. The notes are
tracked in form of **events** that you input during the game. This allows
Dragoneye to automatically track experience gained and saves you some typing
during the game.

## Global navigation
You can go back to your previous view by clicking the back button at top
of Dragoneye window. From game view, it brings you to game list; from
game list, it brings you to the world list.

## Managing Worlds
When you open Dragoneye, you'll be greeted with a list of worlds. The buttoms
below this list allow creation of new worlds, editing of existing ones or
deletion of them.

To edit or delete a world, click on it on the list once, then select the
relevant button on bottom bar. To create a world, just click them button.
Dragoneye will prompt you to edit worlds after they have been created.

To 'enter' a world, double-click it in the list.

## Editing Worlds
Editing worlds allows setting their names and, more importantly, adding
characters in them.

At top of editor window, you may enter a new name for the world. Choose
anything you'd like; it is only to help you distinguish between worlds.

Below this there is a list of characters you have added to this world,
as well as buttons to create more or delete existing characters. Three
pieces of information are tracked about all characters:

* Name
  * Shown in the game view when the character levels up
* XP
  * Incremented by Dragoneye when you add events with XP
  * Causes level up events to be produced according to 5e SRD
* Class
  * This one is just for your convenience

## Managing Games
After you have entered a world, you can start games in it. At bottom of the
window, click <code>Start</code> to do this. You may also open a past game
from the list by double-clicking it. The games are identified by timestamps
from when they were originally started.

Deleting a game is done by clicking it once, then pressing <code>Delete</code>.

## During the Game
Most of the in-game view consists of event log; a list of things that have
happened in the game. They are meant to be read by you *after* the game,
with relevant details being moved to e.g. your campaign notes. Each event
is color-coded based on its type. Some events may contain notes written by you
or XP that it gave characters in the world.

Below the event log are controls for adding more events. At very bottom of
the window, you may select event types "Text" and "Combat". Both allow
you to input details and XP gained in the text fields above, but the combat
event is color coded to be easy to spot.

There are also buttons for logging short and long rests. They are immediately
logged, and do not contain any written notes or XP.

### Shortcuts
The buttons for event types have keyboard shortcuts:
* Ctrl+1: Text
* Ctrl+2: Combat
* Ctrl+3: Short rest
* Ctrl+4: Long rest

In addition to these, undoing addition of events can be done with Ctrl+Z.
Redoing undone actions is done with Shift+Ctrl+Z. Note that undo/redo history
is lost if Dragoneye is closed; the events themself are persistent.