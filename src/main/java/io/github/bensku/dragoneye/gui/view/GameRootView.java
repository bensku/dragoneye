package io.github.bensku.dragoneye.gui.view;

import io.github.bensku.dragoneye.gui.controller.CreateEventController;
import javafx.scene.layout.BorderPane;

/**
 * Root view for anything that happens in game.
 *
 */
public class GameRootView extends BorderPane {

	public GameRootView(EventListView eventList, CreateEventController createController) {
		setCenter(eventList);
		setBottom(createController);
	}
}
