package io.github.bensku.dragoneye.data;

import org.dizitart.no2.objects.Id;

import io.github.bensku.dragoneye.data.event.EventLog;

/**
 * A game session with event occurring in a {@link GameWorld}.
 *
 */
public class Game {
	
	/**
	 * Unique id of this game.
	 */
	@Id
	private final int index;
	
	/**
	 * Id of our game world.
	 */
	private final int worldId;
	
	/**
	 * World this game is happening on.
	 */
	transient GameWorld world;
	
	/**
	 * Events that happened during this game.
	 */
	transient EventLog events;
	
	Game(int worldId, int id) {
		this.worldId = worldId;
		this.index = id;
	}
	
	public GameWorld getWorld() {
		return world;
	}
	
	public EventLog getEventLog() {
		return events;
	}
}
