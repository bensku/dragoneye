package io.github.bensku.dragoneye.gui.controller;

import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.data.PlayerCharacter;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class WorldEditController extends GridPane {

	/**
	 * Name of the world.
	 */
	private final StringProperty name;
		
	/**
	 * Creates a new world edit controller.
	 * @param world World where data is copied to this editor.
	 */
	public WorldEditController(GameWorld world) {
		TextField nameField = new TextField(world.getName());
		this.name = nameField.textProperty();
		addRow(0, new Label("Name"), nameField);
		GridPane.setHgrow(nameField, Priority.ALWAYS);
		
		// Player character editor
		ObservableList<PlayerCharacter> characters = FXCollections.observableArrayList();
		world.getCharacters().forEach(characters::add);
		
		// Show existing characters and allow editing them
		VBox charTiles = new VBox();
		ObservableList<Node> tiles = charTiles.getChildren();
		for (PlayerCharacter pc : characters) {
			tiles.add(new CharacterEditController(world, pc, ctrl -> tiles.remove(ctrl)));
		}
		
		Button createButton = new Button("Create character!");
		createButton.setOnAction(event -> {
			PlayerCharacter pc = world.createCharacter();
			tiles.add(new CharacterEditController(world, pc, ctrl -> tiles.remove(ctrl)));
		});
		
		VBox innerChars = new VBox(charTiles, createButton);
		TitledPane charactersPane = new TitledPane("Characters", innerChars);
		add(charactersPane, 0, 1, 2, 1);
		GridPane.setHgrow(charactersPane, Priority.ALWAYS);
	}
	
	/**
	 * Name of the world this is editing.
	 * @return World name.
	 */
	public StringProperty nameProperty() {
		return name;
	}
	
	/**
	 * Applies whatever changes have been made to given world.
	 * @param world World to apply changes made in editor.
	 */
	public void applyChanges(GameWorld world) {
		world.setName(name.get());
	}
}
