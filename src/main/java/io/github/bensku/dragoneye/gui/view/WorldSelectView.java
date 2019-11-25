package io.github.bensku.dragoneye.gui.view;

import java.util.Optional;

import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.gui.model.WorldListModel;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class WorldSelectView extends BorderPane {
	
	/**
	 * Model of all worlds.
	 */
	private final WorldListModel worldList;
	
	public WorldSelectView(WorldListModel worldList) {
		this.worldList = worldList;
		
		ListView<GameWorld> listView = new ListView<>(worldList.getWorlds());
		listView.setCellFactory(view -> new GameWorldCell());
		
		Button createWorld = new Button("Create");
		createWorld.setPrefWidth(150);
		Button editWorld = new Button("Edit");
		editWorld.setPrefWidth(150);
		Button deleteWorld = new Button("Delete");
		deleteWorld.setPrefWidth(150);
		HBox buttons = new HBox(createWorld, editWorld, deleteWorld);
		
		setCenter(listView);
		setBottom(buttons);
		
		createWorld.setOnAction(e -> createWorld());
		editWorld.setOnAction(e -> {
			GameWorld selected = listView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				editWorld(selected);
			}
		});
		deleteWorld.setOnAction(e -> {
			int selected = listView.getSelectionModel().getSelectedIndex();
			if (selected == -1) {
				return; // Nothing selected?
			}
			
			// Ask for confirmation
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete world?");
			alert.setContentText("All data in the world be permanently lost.");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isEmpty() || !result.get().equals(ButtonType.OK) ) {
				return; // User didn't confirm deletion
			}
			
			worldList.getWorlds().remove(selected);
		});
	}
	
	/**
	 * Renders {@link GameWorld}s in a list.
	 *
	 */
	private static class GameWorldCell extends ListCell<GameWorld> {
		
		@Override
		public void updateItem(GameWorld world, boolean empty) {
			super.updateItem(world, empty);
			if (world != null) {
				BorderPane pane = new BorderPane();
				pane.setCenter(new Label(world.getName()));
			} else {
				setGraphic(null);
			}
		}
	}
	
	/**
	 * Creates a new world, then immediately opens editor for it.
	 */
	private void createWorld() {
		GameWorld world = worldList.createWorld();
		world.setName("Unnamed World"); // editWorld will save
		editWorld(world);
	}
	
	/**
	 * Opens an editor window for given world.
	 */
	private void editWorld(GameWorld world) {
		// TODO implement editing worlds
		
		worldList.updateWorld(world); // Whatever happened, save changes
	}
}
