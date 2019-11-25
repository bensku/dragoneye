package io.github.bensku.dragoneye.gui.controller;

import io.github.bensku.dragoneye.data.GameWorld;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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
	}
	
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
