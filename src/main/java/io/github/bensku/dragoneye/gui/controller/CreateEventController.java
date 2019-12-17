package io.github.bensku.dragoneye.gui.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.github.bensku.dragoneye.data.event.GameEvent;
import io.github.bensku.dragoneye.gui.controller.CreateEventController.Builder.TypeBuilder;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
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
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		public static class TypeBuilder {
			
			private final Builder parent;
			
			private RadioButton button;
			
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
		 * Builds a new controller based on data added to this builder.
		 * @return A new controller.
		 */
		public CreateEventController build() {
			return new CreateEventController(finishedTypes, eventListeners);
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
	 * Current game event constructor.
	 */
	private final ObjectProperty<BiFunction<String, Integer, GameEvent>> currentConstructor;

	private CreateEventController(List<TypeBuilder> types, List<Consumer<GameEvent>> listeners) {
		this.listeners = listeners;
		this.currentConstructor = new SimpleObjectProperty<>();
		
		int detailsHeight = 90;
		
		// Details text area
		this.detailsField = new TextArea();
		detailsField.setPromptText("Event description...");
		add(detailsField, 0, 0, 1, 2);
		//detailsField.setPrefSize(Double.MAX_VALUE, detailsHeight);
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
		submitButton.setOnAction(e -> createEvent());
		add(submitButton, 1, 1, 1, 2);
		
		// Disable submit button if we don't have a type selected
		submitButton.disableProperty().bind(currentConstructor.isNull());
		
		// Event type selection buttons
		ToggleGroup typeGroup = new ToggleGroup();
		HBox typeButtons = new HBox();
		for (TypeBuilder type : types) {
			RadioButton button = type.button;
			if (button != null) {
				button.setPrefHeight(Double.MAX_VALUE);
				button.setPrefWidth(10_000);
				button.setToggleGroup(typeGroup);
				button.setUserData(type.constructor);
				typeButtons.getChildren().add(button);
			}
		}
		add(typeButtons, 0, 2, 1, 1);
		
		// Finalize row layout of this controller
		getRowConstraints().addAll(new RowConstraints(50), new RowConstraints(20, 100, 100), new RowConstraints(50));
		getColumnConstraints().addAll(new ColumnConstraints(300, 500, Double.MAX_VALUE), new ColumnConstraints(130));
		
		// Change constructor based on changes in UI
		typeGroup.selectedToggleProperty().addListener((group, oldValue, newValue) -> {
			if (newValue != null) {
				currentConstructor.set((BiFunction<String, Integer, GameEvent>) newValue.getUserData());
			} else {
				currentConstructor.set(null);
			}
		});
		
		// Select first type that has button
		typeGroup.getToggles().get(0).setSelected(true);
		
		// TODO implement shortcuts
	}
	
	private void createEvent() {
		if (currentConstructor.get() == null) {
			return; // Nothing to do
		}
		GameEvent event = currentConstructor.get().apply(detailsField.getText(), parseXp(xpField.getText()));
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
