package io.github.bensku.dragoneye.gui.view;

import javafx.scene.layout.BorderPane;

/**
 * Root view for anything that happens in game.
 *
 */
public class GameRootView extends BorderPane {

	public GameRootView(EventListView eventList) {
		setCenter(eventList);
	}
}
