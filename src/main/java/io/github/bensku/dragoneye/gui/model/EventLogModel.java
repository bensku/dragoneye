package io.github.bensku.dragoneye.gui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.bensku.dragoneye.data.event.EventLog;
import io.github.bensku.dragoneye.data.event.GameEvent;

public class EventLogModel {

	public static class Change {
		
		public enum Kind {
			/**
			 * Added new event to end of event log (e.g. add, redo).
			 */
			ADD,
			
			/**
			 * Removed last event from log (e.g. undo).
			 */
			REMOVE,
			
			/**
			 * Unknown changes in event log. {@link Change#getEvent()} will
			 * probably return null for changes of this kind.
			 */
			OTHER
		}
		
		private final Kind kind;
		
		/**
		 * The event involved in change. May be null.
		 */
		private final GameEvent event;

		private Change(Kind kind, GameEvent event) {
			this.kind = kind;
			this.event = event;
		}

		public Kind getKind() {
			return kind;
		}

		public GameEvent getEvent() {
			return event;
		}
	}
	
	/**
	 * Backing event log.
	 */
	private final EventLog log;
	
	/**
	 * We'll call these when events change.
	 */
	private final List<Consumer<Change>> listeners;
	
	public EventLogModel(EventLog log) {
		this.log = log;
		this.listeners = new ArrayList<>();
	}
	
	private void eventsChanged(Change.Kind kind, GameEvent event) {
		Change change = new Change(kind, event);
		for (Consumer<Change> listener : listeners) {
			listener.accept(change);
		}
	}
	
	public void addEvent(GameEvent event) {
		eventsChanged(Change.Kind.ADD, event);
		log.addEvent(event);
	}
	
	public void undo() {
		// TODO have EventLog#undo() tell us what changed
	}
	
	public void redo() {
		
	}
	
	// TODO removing events, if we event want to support that
}
