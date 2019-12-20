package io.github.bensku.dragoneye.gui.model;

import java.util.Comparator;

import io.github.bensku.dragoneye.data.Game;
import io.github.bensku.dragoneye.data.GameWorld;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A model of all games in one {@link GameWorld}.
 *
 */
public class GameListModel {

	/**
	 * World we're operating on.
	 */
	private final GameWorld world;

	/**
	 * All games in a world.
	 */
	private final ObservableList<Game> games;

	/**
	 * Creates a model of all games in a world.
	 * @param world World to get games from.
	 */
	public GameListModel(GameWorld world) {
		this.world = world;
		this.games = FXCollections.observableArrayList();

		world.getGames().forEach(games::add);
		games.sort(Comparator.comparing(Game::getCreationTime).reversed()); // Newest games first
		games.addListener(this::gamesChanged);
	}

	private void gamesChanged(ListChangeListener.Change<? extends Game> c) {
		while (c.next()) {
			c.getRemoved().stream().forEach(world::removeGame);
		}
	}

	public ObservableList<Game> getGames() {
		return games;
	}

	/**
	 * Creates a new game.
	 * @return A new game.
	 */
	public Game createGame() {
		Game game = world.createGame();
		games.add(0, game);
		return game;
	}

	/**
	 * Updates data of given game to storage. This applies to everything except
	 * event log content.
	 * @param game Game to update.
	 */
	public void updateGame(Game game) {
		world.updateGame(game);
	}
}
