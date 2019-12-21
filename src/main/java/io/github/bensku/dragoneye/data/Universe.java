package io.github.bensku.dragoneye.data;

import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.mapper.JacksonMapper;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.bensku.dragoneye.data.event.EventTypes;
import io.github.bensku.dragoneye.data.event.GameEvent;

/**
 * An universe consists of multiple {@link GameWorld}s.
 * It is backed by one {@link Nitrite} database.
 *
 */
public class Universe {
	
	/**
	 * Creates an universe that stores data in given file.
	 * @param file Database file.
	 * @return A new universe.
	 */
	public static Universe openOrCreate(Path file) {
		return new Universe(Nitrite.builder().filePath(file.toFile()));
	}
	
	/**
	 * Creates an universe that is stored in memory.
	 * @return A new universe.
	 */
	public static Universe create() {
		return new Universe(Nitrite.builder());
	}

	/**
	 * Database backing this universe.
	 */
	private final Nitrite db;
	
	/**
	 * All worlds in this universe.
	 */
	private final ObjectRepository<GameWorld> worlds;
	
	/**
	 * All games in all worlds.
	 */
	final ObjectRepository<Game> games;
	
	/**
	 * All characters in all worlds.
	 */
	final ObjectRepository<PlayerCharacter> characters;
	
	/**
	 * Creates a new universe.
	 * @param builder Builder we'll use to create the database.
	 */
	public Universe(NitriteBuilder builder) {
		JacksonMapper mapper = new JacksonMapper();
		builder.nitriteMapper(mapper);
		
		// Register event types to mapper we have
		ObjectMapper objMapper = mapper.getObjectMapper();
		for (Class<? extends GameEvent> type : EventTypes.getEventTypes()) {
			objMapper.registerSubtypes(new NamedType(type, type.getSimpleName()));
		}
		
		this.db = builder.openOrCreate();
		this.worlds = db.getRepository(GameWorld.class);
		this.games = db.getRepository(Game.class);
		this.characters = db.getRepository(PlayerCharacter.class);
	}
	
	/**
	 * Injects transient data to given world.
	 * @param world World to inject data to.
	 * @return The given world.
	 */
	private GameWorld injectWorld(GameWorld world) {
		world.universe = this;
		world.db = db;
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
	
	/**
	 * Removes a world from the database.
	 * @param world World to delete.
	 * @param world
	 */
	public void removeWorld(GameWorld world) {
		worlds.remove(world);
		
		// Clean up data under the world
		world.getGames().forEach(world::removeGame); // Games
		characters.find(ObjectFilters.eq("worldId", world.id)); // Characters
	}
}
