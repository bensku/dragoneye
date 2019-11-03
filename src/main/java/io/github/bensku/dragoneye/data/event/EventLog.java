package io.github.bensku.dragoneye.data.event;

import java.util.List;

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
		
		/**
		 * Adds a game event to log.
		 * @param event Event to add.
		 */
		public void addEvent(GameEvent event) {
			event.setLogIndexInternal(nextEvent++);
			events.insert(event);
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
			events.remove(event);
		}
		
		/**
		 * Updates a game event that is already in log.
		 * @param event Event to update.
		 */
		public void updateEvent(GameEvent event) {
			events.update(event);
		}
		
		// TODO reordering events?
	}
	
	/**
	 * Mutator that allows modifying this event log. Passed to
	 * {@link LogAction}s; no one else should be able to change this log.
	 */
	private final Mutator mutator;

	/**
	 * Actions that have been done to this event log in past
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
	
	public EventLog(Game game, List<LogAction> actions, int lastAction, ObjectRepository<GameEvent> events) {
		this.game = game;
		this.mutator = new Mutator();
		this.actions = actions;
		this.lastAction = lastAction;
		this.events = events;
		this.nextEvent = (int) events.size(); // Won't ever have more than 2^31-1 events
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
		action.apply(mutator, game);
	}
	
	/**
	 * Return a cursor with all events.
	 * @return All events.
	 */
	public Cursor<GameEvent> getAllEvents() {
		return events.find();
	}
}
