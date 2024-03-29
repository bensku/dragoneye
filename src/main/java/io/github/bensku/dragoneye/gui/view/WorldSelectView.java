package io.github.bensku.dragoneye.gui.view;

import java.util.Optional;
import java.util.function.Consumer;

import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.gui.controller.WorldEditController;
import io.github.bensku.dragoneye.gui.model.WorldListModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WorldSelectView extends BorderPane {

	/**
	 * list view of all worlds.
	 */
	private final ListView<GameWorld> listView;

	/**
	 * Model of all worlds.
	 */
	private final WorldListModel worldList;

	/**
	 * Creates a new world select view.
	 * @param worldList Model that lists all worlds.
	 * @param openHandler Called when user wants to open a world.
	 */
	public WorldSelectView(WorldListModel worldList, Consumer<GameWorld> openHandler) {
		this.worldList = worldList;

		this.listView = new ListView<>(worldList.getWorlds());
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

		// Creating, editing (name, characters) and deleting worlds
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
			alert.setContentText("All data in the world will be permanently lost.");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isEmpty() || !result.get().equals(ButtonType.OK)) {
				return; // User didn't confirm deletion
			}

			worldList.getWorlds().remove(selected);
		});

		// Opening worlds (list of games in them)
		listView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				GameWorld world = listView.getSelectionModel().getSelectedItem();
				if (world != null) {
					openHandler.accept(world);
				}
			}
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
			if (empty || world == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(world.getName());
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
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		GridPane pane = new GridPane();
		stage.setScene(new Scene(pane, 500, 500));

		// World editor
		WorldEditController editor = new WorldEditController(world);
		ScrollPane editorContainer = new ScrollPane(editor);
		editor.prefWidthProperty().bind(editorContainer.widthProperty().subtract(18));
		pane.add(editorContainer, 0, 0, 3, 1);
		GridPane.setHgrow(editorContainer, Priority.ALWAYS);

		// Buttons to cancel/save changes
		Button cancelButton = new Button("Cancel");
		cancelButton.setPrefWidth(Double.MAX_VALUE);
		cancelButton.setOnAction(e -> stage.close());
		pane.add(cancelButton, 1, 2);

		Button saveButton = new Button("Save");
		saveButton.setPrefWidth(Double.MAX_VALUE);
		BooleanProperty saving = new SimpleBooleanProperty();
		saveButton.setOnAction(e -> {
			saving.set(true);
			stage.close();
		});
		pane.add(saveButton, 2, 2);

		pane.getColumnConstraints().addAll(new ColumnConstraints(0, 360, Double.MAX_VALUE), new ColumnConstraints(70), new ColumnConstraints(70));

		stage.showAndWait();

		if (saving.get()) { // Save only if that button was clicked
			editor.applyChanges(world);
			worldList.updateWorld(world);
			listView.refresh(); // Update shown list too
		}
	}

}
