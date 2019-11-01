package io.github.bensku.dragoneye.data.event;

import java.util.function.Consumer;

public class GameEvent {

	/**
	 * Function that undoes this event an event log.
	 */
	private final Consumer<EventLog> undoer;
	
	/**
	 * Function that redoes this event an event log.
	 */
	private final Consumer<EventLog> redoer;
	
	/**
	 * If this event has been {@link #undo()}ne. {@link #redo()} to remove
	 * this.
	 */
	private boolean undone;
	
	protected GameEvent(Consumer<EventLog> undoer, Consumer<EventLog> redoer) {
		this.undoer = undoer;
		this.redoer = redoer;
	}
	
	public void undo(EventLog log) {
		if (undone) {
			throw new IllegalArgumentException("already undone");
		}
		undoer.accept(log);
	}
	
	public void redo(EventLog log) {
		if (!undone) {
			throw new IllegalArgumentException("not undone");
		}
		redoer.accept(log);
	}
}
