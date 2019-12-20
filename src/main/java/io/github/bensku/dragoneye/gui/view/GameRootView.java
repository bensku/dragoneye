package io.github.bensku.dragoneye.gui.view;

import io.github.bensku.dragoneye.gui.controller.CreateEventController;
import javafx.scene.layout.BorderPane;

/**
 * Root view for anything that happens in game.
 *
 */
public class GameRootView extends BorderPane {

	/**
	 * Constructs a new game root view.
	 * @param eventList View with all added events.
	 * @param createController Controller for creating more events.
	 */
	public GameRootView(EventListView eventList, CreateEventController createController) {
		setCenter(eventList);
		setBottom(createController);
	}
}
