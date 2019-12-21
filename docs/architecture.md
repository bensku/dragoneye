# Dragoneye Architecture
You should read usage.md before this document; in particular, some terminology
used here may be confusing unless you have been game master before.

## Application logic
Dragoneye's "backend" is completely separate from the GUI. It consists
of classes that provide high-level access to underlying data storage.

### Concept implementations
> io.github.bensku.dragoneye.data

The concepts detailed in usage.md are implemented in this package. Their
hierarchy is best described as a tree, like this:

* Universe: root of everything in a single Dragoneye installation
  * GameWorld: a D&D campaign setting
    * PlayerCharacter: a character played by a player
    * Game: a single game session

<code>Universe</code> maps roughly one-to-one to a single Nitrite
database file, and allows CRUD operations on <code>GameWorld</code>s.
It also provides package-private access to database "tables" to
other concept implementations. The universe injects itself and a reference
to Nitrite database to <code>GameWorld</code>s that are retrieved from it.

<code>GameWorld</code> handles characters and games, as described in
usage.md. Each world has unique id that is used as a foreign key for
characters and games in it. A game world injects itself and relevant event logs
to <code>Game</code>s retrieved from it.

<code>PlayerCharacter</code> is, for most part, a POJO that stores data
about characters. It has a high-level API for experience and level tracking.

<code>Game</code> is also a POJO. However, it provides access to
<code>EventLog</code> (more about that below) that is injected into it by
<code>GameWorld</code>.

### Event system
> io.github.bensku.dragoneye.data.event

This package contains entirety of Dragoneye's event system. In particular,
there are implementations of

* Event log of game, as mentioned in usage.md (<code>EventLog</code>)
* Event types
  * Subclasses of <code>GameEvent</code>
  * Registered in <code>EventTypes</code> for serialization purposes
* Log actions (<code>LogAction</code>)
  * All modifications to event log are represented as actions
  * Primary purpose is support for undo and redo

The most important class is <code>EventLog</code>. It provides a list-like
data structure that is persisted using Nitrite database and has unlimited
(aside of application restart) undo and redo support. Registering listeners
for changes in the log is also supported.

Direct access to the event log data is granted only to log actions by using
an inner class of <code>EventLog</code>. In general, all modifications to the
event log go through log actions; this is critical for undo/redo support.

Log actions are generally implemented by providing two lambdas to 
<code>LogAction</code> constructor. <code>EventLog</code> implements
creation of some most common actions, but also allows usage of any actions.

Currently existing event types are
* <code>TextEvent</code>: just user-written text
* <code>CombatEvent</code>: just user-written text, too
* <code>LevelUpEvent</code>: who leveled up, to what level
* <code>RestEvent</code>: short or long rest (see D&D 5e SRD)

They all extend the core <code>GameEvent</code>, and due to Nitrite database
security requirements, are registered in <code>EventTypes</code>.

<code>GameEvent</code> contains creation time of an event, experience gained
from it and the *dependent events*. Dependent events are events that Dragoneye
automatically creates based on some trigger. <code>GameEvent</code> will,
for example, add its XP to characters in world and create a
<code>LevelUpEvent</code> if they leveled up due to that.

## User interface
Dragoneye's current "frontend" is written in JavaFX, and heavily uses the
application logic.

Dragoneye mostly follows the MVC model, separating models, controllers and
views. Because MVC does not work well in all situations, it is not strictly
followed, though.

### Main package
> io.github.bensku.dragoneye

Base package of Dragoneye contains its main class that launches the JavaFX
application. Before launching JavaFX, parts of application logic will be loaded
and initialized.

Note that packages under this are *not* all part of the user interface. Main
class of application was just placed as high in package hierarchy as possible,
for clarity reasons.

### GUI core
> io.github.bensku.dragoneye.gui

This package contains the JavaFX application (<code>DragoneyeApp</code>). It
ties the other GUI parts and application logic together, often by using models.

In this package are also <code>EventRenderers</code> that provide visual appearance
to game events.

### Controllers
> io.github.bensku.dragoneye.gui.controller

In package has all custom controllers of Dragoneye. <code>CharacterEditController</code>
is used in world edit screen (see usage.md) for editing characters.
<code>CreateEventController</code> is used in game screen for creating new game events.
<code>WorldEditController</code> provides world editing component, and uses
<code>CharacterEditController</code>s for editing characters.

### Models
> io.github.bensku.dragoneye.gui.model

Models convert data retrieved from application logic into a format that is closer
to how it looks in GUI. They commonly provide JavaFX properties and observable
collections for views and controllers to use.

<code>GameListModel</code> provides observable data of all games in a world.
<code>WorldListModel</code> provides a list of *all* worlds, instead.

### Views
> io.github.bensku.dragoneye.gui.view

Dragoneye has many custom views that present data to user. They contain
controls - custom, or from JavaFX - that allow editing it, too.

<code>EventListView</code> is used when user is in game view. It renders
a list of all events from an <code>EventLog</code>. This view is one of the
cases where MVC is not strictly followed; events are not accessed through
a model. <code>EventLog</code> directly provides a similar event listener
system that model could provide.

<code>GameRootView</code> places <code>EventListView</code> above
<code>CreateEventController</code>. It also configures shortcuts for
undoing/redoing event addition when receiving a notification from
<code>RootView</code>.

<code>GameSelectView</code> and <code>WorldSelectView</code> allow
selecting, creating and editing games and world, respectively.
They heavily delegate to other views and controllers.

<code>RootView</code> provides the global back button described in
usage.md and shows title of current view at top of window. It also
notifies the views when they become visible or are removed due to
user going back.

## Architectural problems
Dragoneye's application logic is too tightly coupled with Nitrite database
and Jackson serializer it uses. Abstracting these away beyond very simple
DAOs would have been ideal. Unfortunately, it would probably take a lot
of time, because Dragoneye has requires polymorphic (de)serialization
that is not widely supported.