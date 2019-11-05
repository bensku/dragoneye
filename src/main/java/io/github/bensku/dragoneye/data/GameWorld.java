package io.github.bensku.dragoneye.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.ObjectRepository;

import io.github.bensku.dragoneye.data.event.EventLog;
import io.github.bensku.dragoneye.data.event.GameEvent;

/**
 * A world that contains multiple {@link Game}s.
 *
 */
public class GameWorld {

	/**
	 * Index of this world in {@link Universe}.
	 */
	@Id
	final int index;
	
	/**
	 * Display name of the world.
	 */
	private String name;
	
	/**
	 * Player characters in this world.
	 */
	private final List<PlayerCharacter> characters;
	
	/**
	 * Backing database.
	 */
	transient Nitrite db;
	
	/**
	 * Games played in this world.
	 */
	transient ObjectRepository<Game> games;
	
	GameWorld(int id) {
		this.index = id;
		this.name = "";
		this.characters = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
	    Objects.requireNonNull(name);
		this.name = name;
	}

	/**
	 * Gets player characters in this game world. The returned list is mutable.
	 * If it is change, remember to {@link Universe#updateworld(GameWorld)}!
	 * @return Player characters.
	 */
	public List<PlayerCharacter> getCharacters() {
		return characters;
	}
	
	/**
	 * Injects transient data to a game.
	 * @param game Game to inject.
	 * @return The given game.
	 */
	private Game injectGame(Game game) {
		game.world = this; // Reference to this world
		// Create event log
		// TODO persistent action history?
		game.events = new EventLog(game, new ArrayList<>(), 0, db.getRepository("events-" + index, GameEvent.class));
		return game;
	}
	
	/**
	 * Gets all games in this world.
	 * @return A stream with all games.
	 */
	public Stream<Game> getGames() {
		return StreamSupport.stream(games.find().spliterator(), false)
				.map(this::injectGame);
	}
	
	/**
	 * Creates a new game.
	 * @return A new game.
	 */
	public Game createGame() {
		Game game = new Game((int) games.size());
		games.insert(game);
		return injectGame(game);
	}
	
	/**
	 * Updates changes to given game to database. Note that changes to events
	 * are not included here; the {@link EventLog} takes care of them.
	 * @param game Game to update.
	 */
	public void updateGame(Game game) {
		games.update(game);
	}
}
