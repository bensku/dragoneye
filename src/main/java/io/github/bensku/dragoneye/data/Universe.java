package io.github.bensku.dragoneye.data;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;

/**
 * An universe consists of multiple {@link GameWorld}s.
 * It is backed by one {@link Nitrite} database.
 *
 */
public class Universe {

	/**
	 * Database backing this universe.
	 */
	private final Nitrite db;
	
	/**
	 * All worlds in this universe.
	 */
	private final ObjectRepository<GameWorld> worlds;
	
	/**
	 * Creates a new universe.
	 * @param db Database to load it from.
	 */
	public Universe(Nitrite db) {
		this.db = db;
		this.worlds = db.getRepository("worlds", GameWorld.class);
	}
	
	/**
	 * Injects transient data to given world.
	 * @param world World to inject data to.
	 * @return The given world.
	 */
	private GameWorld injectWorld(GameWorld world) {
		world.universe = this;
		world.db = db;
		world.characters = db.getRepository("characters-" + world.index, PlayerCharacter.class);
		world.games = db.getRepository("games-" + world.index, Game.class);
		return world;
	}
	
	/**
	 * Gets all worlds in this universe.
	 * @return A stream with all worlds.
	 */
	public Stream<GameWorld> getWorlds() {
		return StreamSupport.stream(worlds.find().spliterator(), false)
				.map(this::injectWorld);
	}
	
	/**
	 * Creates a new world with an empty name.
	 * @return A new world.
	 */
	public GameWorld createWorld() {
		GameWorld world = new GameWorld((int) worlds.size());
		worlds.insert(world); // Store to DB
		return injectWorld(world); // Inject and return
	}
	
	/**
	 * Updates changes to given world in the database.
	 * @param world World to update.
	 */
	public void updateWorld(GameWorld world) {
		worlds.update(world);
	}
}
