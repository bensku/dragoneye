package io.github.bensku.dragoneye.gui.view;

import java.util.Optional;
import java.util.function.Consumer;

import io.github.bensku.dragoneye.data.Game;
import io.github.bensku.dragoneye.gui.model.GameListModel;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class GameSelectView extends BorderPane {
	
	public GameSelectView(GameListModel gameList, Consumer<Game> openHandler) {
		ListView<Game> listView = new ListView<>(gameList.getGames());
		listView.setCellFactory(view -> new GameCell());
		
		Button createGame = new Button("Start");
		createGame.setPrefWidth(225);
		Button deleteGame = new Button("Delete");
		deleteGame.setPrefWidth(225);
		HBox buttons = new HBox(createGame, deleteGame);
		
		setCenter(listView);
		setBottom(buttons);
		
		// Starting and deleting games
		createGame.setOnAction(e -> openHandler.accept(gameList.createGame()));
		deleteGame.setOnAction(e -> {
			int selected = listView.getSelectionModel().getSelectedIndex();
			if (selected == -1) {
				return; // Nothing selected?
			}
			
			// Ask for confirmation
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete world?");
			alert.setContentText("All data in the game will be permanently lost.");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isEmpty() || !result.get().equals(ButtonType.OK) ) {
				return; // User didn't confirm deletion
			}
			
			gameList.getGames().remove(selected);
		});
		
		// Opening past games
		listView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				Game game = listView.getSelectionModel().getSelectedItem();
				if (game != null) {
					openHandler.accept(game);
				}
			}
		});
	}
	
	/**
	 * Renders information about games in list.
	 *
	 */
	private static class GameCell extends ListCell<Game> {
		
		@Override
		public void updateItem(Game game, boolean empty) {
			super.updateItem(game, empty);
		     if (empty || game == null) {
		         setText(null);
		         setGraphic(null);
		     } else {
		    	 // TODO more readable format
		         setText(game.getCreationTime().toString());
		     }
		}
	}
}
