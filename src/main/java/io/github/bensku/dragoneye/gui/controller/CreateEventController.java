package io.github.bensku.dragoneye.gui.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.github.bensku.dragoneye.data.event.GameEvent;
import io.github.bensku.dragoneye.gui.controller.CreateEventController.Builder.TypeBuilder;
import io.github.bensku.dragoneye.gui.view.RootView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

/**
 * Provides user interface for creating new events.
 *
 */
public class CreateEventController extends GridPane {

	/**
	 * Creates a new builder that can be used to create event creation
	 * controller.
	 * @return A nnew builder.
	 */
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		public static class TypeBuilder {
			
			private final Builder parent;
			
			private RadioButton button;
			
			private boolean activateImmediately;
			
			private Set<KeyCombination> shortcuts;
			
			private BiFunction<String, Integer, ? extends GameEvent> constructor;
			
			private TypeBuilder(Builder parent) {
				this.parent = parent;
				this.shortcuts = new HashSet<>();
			}
			
			/**
			 * Sets the button users can click to choose this event type.
			 * Optional; some events types could only be activated with
			 * keyboard shortcuts.
			 * @param button A radio button.
			 * @return This builder.
			 */
			public TypeBuilder button(RadioButton button) {
				this.button = button;
				return this;
			}
			
			/**
			 * Log the event immediately when button or keybind is pressed,
			 * instead of waiting the user to submit it.
			 * @return This builder.
			 */
			public TypeBuilder activateImmediately() {
				this.activateImmediately = true;
				return this;
			}
			
			/**
			 * Adds a shortcut that causes this event type to be selected.
			 * @param shortcut Key combination.
			 * @return This builder.
			 */
			public TypeBuilder shortcut(KeyCombination shortcut) {
				shortcuts.add(shortcut);
				return this;
			}
			
			/**
			 * Sets the constructor used for creating game events. It receives
			 * current contents of the details and XP fields. It may return a valid
			 * event, or null in case no event creation is not needed
			 * @param constructor A constructor.
			 * @return This builder.
			 */
			public TypeBuilder constructor(BiFunction<String, Integer, ? extends GameEvent> constructor) {
				this.constructor = constructor;
				return this;
			}
			
			/**
			 * Finishes creation of this event type. The
			 * {@link #constructor(BiFunction) constructor} and at least one
			 * {@link #button(RadioButton) button} or
			 * {@link #shortcut(KeyCombination)} must have been added.
			 * @return Parent builder.
			 * @throws IllegalStateException When some required methods have
			 * not yet been called.
			 */
			public Builder finish() {
				if (constructor == null) {
					throw new IllegalArgumentException("constructor missing");
				} else if (button == null && shortcuts.isEmpty()) {
					throw new IllegalArgumentException("no button or shortcuts");
				}
				parent.finishedTypes.add(this); // Successfully finished
				return parent;
			}
		}
		
		/**
		 * Type builders that have been successfully
		 * {@link TypeBuilder#finish() finished}.
		 */
		private final List<TypeBuilder> finishedTypes;
		
		/**
		 * These are called when an event is created.
		 */
		private final List<Consumer<GameEvent>> eventListeners;
		
		/**
		 * Key bindings map.
		 */
		private ObservableMap<KeyCombination, Runnable> keyBindings;
		
		private Builder() {
			this.finishedTypes = new ArrayList<>();
			this.eventListeners = new ArrayList<>();
		}
		
		/**
		 * Begins adding a new event type.
		 * @return Event type builder.
		 */
		public TypeBuilder eventType() {
			return new TypeBuilder(this);
		}
		
		/**
		 * Adds a listener that receives all game events created here.
		 * @param consumer Consumer.
		 * @return This builder.
		 */
		public Builder eventListener(Consumer<GameEvent> consumer) {
			eventListeners.add(consumer);
			return this;
		}
		
		/**
		 * Sets where key bindings are registered.
		 * @param keyBindings Key bindings map.
		 * @return This builder.
		 */
		public Builder keyBindings(ObservableMap<KeyCombination, Runnable> keyBindings) {
			this.keyBindings = keyBindings;
			return this;
		}
		
		/**
		 * Builds a new controller based on data added to this builder.
		 * @return A new controller.
		 */
		public CreateEventController build() {
			return new CreateEventController(finishedTypes, eventListeners, keyBindings);
		}
	}
	
	/**
	 * User action, such as button press or keybind use info.
	 *
	 */
	private static class ActionInfo {
		
		/**
		 * Constructor that makes the event
		 */
		public final BiFunction<String, Integer, ? extends GameEvent> constructor;
		
		/**
		 * See {@link TypeBuilder#activateImmediately()}.
		 */
		public final boolean activateImmediately;

		private ActionInfo(BiFunction<String, Integer, ? extends GameEvent> constructor, boolean activateImmediately) {
			this.constructor = constructor;
			this.activateImmediately = activateImmediately;
		}
		
	}
	
	/**
	 * Details text field.
	 */
	private final TextArea detailsField;
	
	/**
	 * XP input field.
	 */
	private final TextField xpField;
	
	/**
	 * These are called when a new event has been created.
	 */
	private final List<Consumer<GameEvent>> listeners;
	
	/**
	 * Current game event information we need to fire it.
	 */
	private final ObjectProperty<ActionInfo> currentEvent;
	
	/**
	 * Map where we register key bindings.
	 */
	private final ObservableMap<KeyCombination, Runnable> keyBindings;

	private CreateEventController(List<TypeBuilder> types, List<Consumer<GameEvent>> listeners,
			ObservableMap<KeyCombination, Runnable> keyBindings) {
		this.listeners = listeners;
		this.currentEvent = new SimpleObjectProperty<>();
		this.keyBindings = keyBindings;
				
		// Details text area
		this.detailsField = new TextArea();
		detailsField.setPromptText("Event description...");
		// Capture tab in field to transfer control to next field (very hacky!)
		detailsField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.TAB && !event.isShiftDown() && !event.isControlDown()) {
				event.consume();
				Node source = (Node) event.getSource();
				source.fireEvent(new KeyEvent(source, event.getTarget(), event.getEventType(),
						event.getCharacter(), event.getText(), event.getCode(),
						event.isShiftDown(), true, event.isAltDown(), event.isMetaDown()));
			}
		});
		add(detailsField, 0, 0, 1, 2);
		GridPane.setHgrow(detailsField, Priority.ALWAYS);
		
		// XP input field
		this.xpField = new TextField();
		xpField.setPromptText("XP!");
		xpField.setPrefHeight(40);
		xpField.setPrefWidth(60);
		xpField.setFont(new Font(25));
		add(xpField, 1, 0, 1, 1);
		
		// Create button and keybind
		Button submitButton = new Button("New!");
		submitButton.setFont(new Font(30));
		submitButton.setPrefHeight(Double.MAX_VALUE);
		submitButton.setPrefWidth(Double.MAX_VALUE);
		submitButton.setOnAction(e -> createEvent(currentEvent.get()));
		add(submitButton, 1, 1, 1, 2);
		
		// Disable submit button if we don't have a type selected
		submitButton.disableProperty().bind(currentEvent.isNull());
		
		// Event type selection buttons
		ToggleGroup typeGroup = new ToggleGroup();
		HBox typeButtons = new HBox();
		for (TypeBuilder type : types) {
			ActionInfo info = new ActionInfo(type.constructor, type.activateImmediately);
			
			// Place button if one has been configured
			RadioButton button = type.button;
			if (button != null) {
				button.setPrefHeight(Double.MAX_VALUE);
				button.setPrefWidth(10_000);
				button.setToggleGroup(typeGroup);
				button.setUserData(info);
				typeButtons.getChildren().add(button);
			}
			
			// Register shortcuts
			for (KeyCombination shortcut : type.shortcuts) {
				keyBindings.put(shortcut, () -> {
					if (info.activateImmediately) {
						createEvent(info); // Immediately create an event
					} else { // Select appropriate button if possible
						typeGroup.selectToggle(button);
						currentEvent.set(info);
					}
				});
			}
			
		}
		add(typeButtons, 0, 2, 1, 1);
		
		// Finalize row layout of this controller
		getRowConstraints().addAll(new RowConstraints(50), new RowConstraints(20, 100, 100), new RowConstraints(50));
		getColumnConstraints().addAll(new ColumnConstraints(300, 500, Double.MAX_VALUE), new ColumnConstraints(130));
		
		// Change constructor based on changes in UI
		BooleanProperty nestedToggle = new SimpleBooleanProperty(false);
		typeGroup.selectedToggleProperty().addListener((group, oldValue, newValue) -> {
			if (nestedToggle.get()) {
				return;
			}
			if (newValue != null) {
				ActionInfo info = (ActionInfo) newValue.getUserData();
				if (info.activateImmediately) { // Fire this event immediately
					createEvent(info);
					nestedToggle.set(true); // Ensure we're not calling us over and over
					typeGroup.selectToggle(oldValue);
					nestedToggle.set(false);
				} else { // User can fire the event when they want to
					currentEvent.set(info);
				}
			} else {
				currentEvent.set(null);
			}
		});
		
		// Select first type that has button
		typeGroup.getToggles().get(0).setSelected(true);
		
		// TODO implement shortcuts
	}
	
	private void createEvent(ActionInfo info) {
		if (info == null) {
			return; // Nothing to do
		}
		GameEvent event = info.constructor.apply(detailsField.getText(), parseXp(xpField.getText()));
		if (event != null) {
			// Clear details; that data went to event already
			detailsField.clear();
			xpField.clear();
			
			// Call the listeners
			for (Consumer<GameEvent> listener : listeners) {
				listener.accept(event);
			}
		}
	}
	
	private int parseXp(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return 0; // By default, no XP is granted
		}
	}
}
