package io.github.bensku.dragoneye.data.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;

import io.github.bensku.dragoneye.data.Game;

/**
 * Log of {@link GameEvent}s. Has full undo/redo support with
 * {@link LogAction}s.
 *
 */
public class EventLog {
	
	/**
	 * Game this event log is for.
	 */
	private final Game game;
	
	/**
	 * Allows mutation of event log. This should only be used inside
	 * {@link LogAction}s to preserve undo/redo support.
	 *
	 */
	public class Mutator {
		
		private void notifyListeners(GameEvent event, BiConsumer<ChangeListener, GameEvent> func) {
			for (ChangeListener listener : changeListeners) {
				func.accept(listener, event);
			}
		}
		
		/**
		 * Adds a game event to log.
		 * @param event Event to add.
		 */
		public void addEvent(GameEvent event) {
			event.setLogIndexInternal(nextEvent++);
			event.added(this, game); // Let event to mutate log
			events.insert(event);
			notifyListeners(event, ChangeListener::added);
		}
		
		/**
		 * Removes a game event from log.
		 * @param event Event to remove.
		 */
		public void removeEvent(GameEvent event) {
			if (event.getLogIndex() == nextEvent - 1) {
				nextEvent--; // Removed last in log; we can put something in its place
			} // else: leaving a hole in event ids for DB to handle
			event.setLogIndexInternal(-1); // Not in log anymore
			event.removed(this, game); // Let event to mutate log
			events.remove(event);
			notifyListeners(event, ChangeListener::removed);
		}
		
		/**
		 * Updates a game event that is already in log.
		 * @param event Event to update.
		 */
		public void updateEvent(GameEvent event) {
			events.update(event);
			notifyListeners(event, ChangeListener::updated);
			// With updates, we do not allow events to mutate log
			// Unless UI allows updates to XP, this shouldn't be a big deal
			// (GM retroactively changing XP is seldom a good idea, anyway)
		}
		
		// TODO reordering events?
	}
	
	/**
	 * Mutator that allows modifying this event log. Passed to
	 * {@link LogAction}s; no one else should be able to change this log.
	 */
	private final Mutator mutator;

	/**
	 * Actions that have been done to this event log in past.
	 */
	private final List<LogAction> actions;
	
	/**
	 * Index of last action in {@link #actions} that currently affects this
	 * event log. There may be actions that have been undone after it.
	 * 
	 * <p>When there are no actions in effect, this is -1.
	 */
	private int lastAction;
	
	/**
	 * Game events in this log.
	 */
	private final ObjectRepository<GameEvent> events;
	
	/**
	 * Log index of next event that is added. We're not using
	 * {@link ObjectRepository#size()} on {@link #events}, because its time
	 * complexity is not known.
	 */
	private int nextEvent;
	
	/**
	 * A listener for changes in event log.
	 *
	 */
	public interface ChangeListener {
		
		/**
		 * Called after an event has been added.
		 * @param event Event that was just added to log.
		 */
		void added(GameEvent event);
		
		/**
		 * Called after an event has been removed.
		 * @param event Event that was just removed from log.
		 */
		void removed(GameEvent event);
		
		/**
		 * Called when an event is updated.
		 * @param event Event that was updated and is in log.
		 */
		void updated(GameEvent event);
	}
	
	/**
	 * Change listeners.
	 */
	private final List<ChangeListener> changeListeners;
	
	public EventLog(Game game, List<LogAction> actions, int lastAction, ObjectRepository<GameEvent> events) {
		this.game = game;
		this.mutator = new Mutator();
		this.actions = actions;
		this.lastAction = lastAction;
		this.events = events;
		this.nextEvent = (int) events.size(); // Won't ever have more than 2^31-1 events
		this.changeListeners = new ArrayList<>();
	}
	
	/**
	 * Attempts to undo last action in this event log. Does nothing if there
	 * are no actions that can be undone.
	 */
	public void undo() {
		if (lastAction == -1) {
			return; // Nothing to undo
		}
		actions.get(lastAction--).undo(mutator, game);
	}
	
	/**
	 * Attempts to redo last undone action in this event log. Does nothing if
	 * there are currently no undone actions.
	 */
	public void redo() {
		if (lastAction == actions.size() - 1) {
			return; // Nothing to redo
		}
		actions.get(lastAction++).apply(mutator, game);
	}
	
	/**
	 * Executes an action in this event log. This should never be called more
	 * than once with any given action.
	 * 
	 * <p>This method destroys current undone actions. They cannot be redone in
	 * future.
	 * @param action The action to execute.
	 * @throws IllegalArgumentException If same action is done more than once.
	 */
	public void doAction(LogAction action) {
		if (action.executed) {
			throw new IllegalArgumentException("action already executed");
		}
		// Remove undone actions; new action overwrites undo history
		for (int i = actions.size() - 1; i > lastAction; i--) {
			actions.remove(i);
		}
		lastAction++; // Initially not undone
		actions.add(action); // Store action for undo/redo
		action.apply(mutator, game); // Apply the action
	}
	
	/**
	 * Adds an event to this log. Supports undo/redo.
	 * @param event Event to add.
	 */
	public void addEvent(GameEvent event) {
	    doAction(new LogAction((mut, game) -> mut.addEvent(event),
	            (mut, game) -> mut.removeEvent(event)));
	}
	
	/**
	 * Removes an event from this log. Supports undo/redo.
	 * @param event Event to remove.
	 */
	public void removeEvent(GameEvent event) {
	    doAction(new LogAction((mut, game) -> mut.removeEvent(event),
	            (mut, game) -> mut.addEvent(event)));
	}
	
	/**
	 * Updates data of an event.
	 * @param event Event to update.
	 */
	public void updateEvent(GameEvent event) {
	    GameEvent original = events.getById(NitriteId.createId((long) event.getLogIndex()));
	    doAction(new LogAction((mut, game) -> mut.updateEvent(event),
	            (mut, game) -> mut.updateEvent(original)));
	}
	
	/**
	 * Return an ordered stream of all game events.
	 * @return All events.
	 */
	public Stream<GameEvent> getAllEvents() {
		Cursor<GameEvent> cursor = events.find();
		Spliterator<GameEvent> spliterator = Spliterators.spliterator(cursor.iterator(), cursor.size(),
				Spliterator.ORDERED | Spliterator.SIZED);
		return StreamSupport.stream(spliterator, false).map(e -> {
			e.inject(game);
			return e;
		});
	}
	
	/**
	 * Registers a new change listener.
	 * @param listener Change listener.
	 */
	public void registerListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
}
