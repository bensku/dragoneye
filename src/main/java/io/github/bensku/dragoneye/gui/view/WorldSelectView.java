package io.github.bensku.dragoneye.gui.view;

import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.data.Universe;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class WorldSelectView extends BorderPane {

	public WorldSelectView(Universe universe) {
		ListView<GameWorld> worldList = new ListView<>();
		universe.getWorlds().forEach(world -> worldList.getItems().add(world));
		
		Button createWorld = new Button("Create");
		createWorld.setPrefWidth(200);
		Button deleteWorld = new Button("Delete");
		deleteWorld.setPrefWidth(200);
		HBox buttons = new HBox(createWorld, deleteWorld);
		
		setCenter(worldList);
		setBottom(buttons);
	}
}
