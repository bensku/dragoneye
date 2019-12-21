package io.github.bensku.dragoneye.gui.view;

import io.github.bensku.dragoneye.gui.controller.CreateEventController;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;

/**
 * Root view for anything that happens in game.
 *
 */
public class GameRootView extends BorderPane implements RootView.NavigationListener {

	/**
	 * Map where key bindings can be registered to.
	 */
	private final ObservableMap<KeyCombination, Runnable> keyBindings;
	
	/**
	 * Functions we call on undo/redo.
	 */
	private final Runnable undoHandler, redoHandler;
	
	/**
	 * Constructs a new game root view.
	 * @param eventList View with all added events.
	 * @param createController Controller for creating more events.
	 * @param keyBindings Where we register key bindings.
	 * @param undoHandler Called when user wants to undo last change.
	 * @param redoHandler Called when user wants to redo undone change.
	 */
	public GameRootView(EventListView eventList, CreateEventController createController,
			ObservableMap<KeyCombination, Runnable> keyBindings,
			Runnable undoHandler, Runnable redoHandler) {
		this.keyBindings = keyBindings;
		this.undoHandler = undoHandler;
		this.redoHandler = redoHandler;
		setCenter(eventList);
		setBottom(createController);
	}

	@Override
	public void opened() {
		keyBindings.put(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN), undoHandler);
		keyBindings.put(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN), redoHandler);
	}

	@Override
	public void closed() {
		keyBindings.clear();
	}
}
