package io.github.bensku.dragoneye.gui.view;

import java.util.HashMap;
import java.util.Map;

import io.github.bensku.dragoneye.data.event.EventLog;
import io.github.bensku.dragoneye.data.event.GameEvent;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

/**
 * A view that shows event log contents in a list. After creating it, remember
 * to {@link #addType(Class, EventRenderer) add} all needed event types.
 * After that, you can {@link #initialize() initialize} it.
 *
 */
public class EventListView extends BorderPane {
	
	/**
	 * Event log this view presents data from.
	 */
	private final EventLog log;
	
	/**
	 * If {@link #initialize()} has been called before.
	 */
	private boolean initialized;
	
	/**
	 * List view with all events.
	 */
	private final ListView<GameEvent> eventsView;
	
	private class ViewUpdater implements EventLog.ChangeListener {

		@Override
		public void added(GameEvent event) {
			checkInitialize(true);
			eventsView.getItems().add(event);
		}

		@Override
		public void removed(GameEvent event) {
			checkInitialize(true);
			eventsView.getItems().remove(event); // TODO optimize, this is O(n) on list size
		}

		@Override
		public void updated(GameEvent event) {
			checkInitialize(true);
			eventsView.refresh();
		}
		
	}
	
	/**
	 * Delegates rendering of {@link GameEvent}s to their own renderers.
	 *
	 */
	private class EventCell extends ListCell<GameEvent> {
		
		@Override
		public void updateItem(GameEvent event, boolean empty) {
			super.updateItem(event, empty);
		     if (empty || event == null) {
		         setText(null);
		         setGraphic(null);
		     } else {
		    	 EventRenderer<GameEvent> renderer = renderers.get(event.getClass());
		    	 if (renderer == null) {
		    		 throw new IllegalStateException("renderer missing for " + event.getClass());
		    	 }
		         setGraphic(renderer.render(event));
		     }
		}
	}
	
	/**
	 * Renders events.
	 * @param <T> Type of events rendered.
	 *
	 */
	@FunctionalInterface
	public interface EventRenderer<T extends GameEvent> {
		
		/**
		 * Renders given event.
		 * @param event Event to render.
		 * @return Something that can be displayed in a list.
		 */
		Parent render(T event);
	}
	
	/**
	 * Event renderers by event types.
	 */
	private final Map<Class<? extends GameEvent>, EventRenderer<GameEvent>> renderers;

	public EventListView(EventLog log) {
		this.log = log;
		this.initialized = false;
		this.eventsView = new ListView<>();
		log.registerListener(new ViewUpdater());
		eventsView.setCellFactory(view -> new EventCell());
		this.renderers = new HashMap<>();
	}
	
	private void checkInitialize(boolean status) {
		if (status != initialized) {
			throw new IllegalStateException();
		}
	}
	
	@SuppressWarnings("unchecked") // Map key is event type, so this is mostly safe
	public <T extends GameEvent> void addType(Class<T> type, EventRenderer<T> renderer) {
		checkInitialize(false);
		renderers.put(type, (EventRenderer<GameEvent>) renderer);
	}
	
	public void initialize() {
		log.getAllEvents().forEach(eventsView.getItems()::add);
		initialized = true;
	}
}
