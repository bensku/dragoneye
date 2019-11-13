package io.github.bensku.dragoneye.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.Cursor;
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
	 * The universe in which this world is in.
	 */
	transient Universe universe;
	
	/**
	 * Backing database.
	 */
	transient Nitrite db;
	
	/**
	 * Player characters in this world.
	 */
	transient ObjectRepository<PlayerCharacter> characters;
	
	/**
	 * Games played in this world.
	 */
	transient ObjectRepository<Game> games;
	
	GameWorld(int index) {
		this.index = index;
		this.name = "";
	}
	
	public Universe getUniverse() {
		return universe;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
	    Objects.requireNonNull(name);
		this.name = name;
	}

	/**
	 * Gets player characters in this game world.
	 * @return Player characters.
	 */
	public Cursor<PlayerCharacter> getCharacters() {
		return characters.find();
	}
	
	public PlayerCharacter getCharacter(int id) {
		return characters.getById(NitriteId.createId((long) id));
	}
	
	/**
	 * Creates a new character.
	 * @return A new character.
	 */
	public PlayerCharacter createCharacter() {
		PlayerCharacter pc = new PlayerCharacter((int) characters.size());
		characters.insert(pc);
		return pc;
	}
	
	/**
	 * Updates an existing character.
	 * @param pc Existing character.
	 */
	public void updateCharacter(PlayerCharacter pc) {
		characters.update(pc);
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
