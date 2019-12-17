package io.github.bensku.dragoneye.gui.controller;

import java.util.function.Consumer;

import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.data.PlayerCharacter;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * A small controller that allows editing player character data.
 *
 */
public class CharacterEditController extends GridPane {

	private final GameWorld world;
	private final PlayerCharacter pc;
	
	/**
	 * Creates a new character edit controller.
	 * @param world World the character is in.
	 * @param pc The character.
	 * @param deleteCallback Callback that receives this if the character is deleted.
	 */
	public CharacterEditController(GameWorld world, PlayerCharacter pc, Consumer<CharacterEditController> deleteCallback) {
		this.world = world;
		this.pc = pc;
		
		TextField nameField = new TextField(pc.getName());
		nameField.setPromptText("Name...");
		saveAfterModified(nameField, pc::setName);
		add(nameField, 0, 0, 2, 1);
		
		Label levelLabel = new Label("Level: " + pc.getLevel());
		
		TextField xpField = new TextField("" + pc.getXp());
		xpField.setPromptText("XP!");
		saveAfterModified(xpField, text -> {
			try {
				pc.setXp(Integer.parseInt(text));
				levelLabel.setText("Level: " + pc.getLevel()); // Update level in UI, too
			} catch (NumberFormatException e) {
				new Alert(AlertType.ERROR, "Invalid level provided.");
			}
		});
		add(xpField, 2, 0);
		
		add(levelLabel, 3, 0);
		
		TextField classField = new TextField(pc.getCharClass());
		classField.setPromptText("Class...");
		saveAfterModified(classField, pc::setCharClass);
		add(new Label("Class:"), 0, 1);
		add(classField, 1, 1, 2, 1);
		
		Button deleteButton = new Button("Del");
		deleteButton.setOnAction(event -> {
			deleteCallback.accept(this);
			world.removeCharacter(pc);
		});
		add(deleteButton, 4, 1);
		
		getColumnConstraints().addAll(new ColumnConstraints(45), new ColumnConstraints(170), new ColumnConstraints(110), new ColumnConstraints(70));
		
		setPadding(new Insets(3));
	}
	
	private void saveAfterModified(TextField field, Consumer<String> consumer) {
		field.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				consumer.accept(field.getText());
				world.updateCharacter(pc);
			}
		});
	}
}
