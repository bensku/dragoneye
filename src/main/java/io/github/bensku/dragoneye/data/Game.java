package io.github.bensku.dragoneye.data;

import org.dizitart.no2.objects.Id;

import io.github.bensku.dragoneye.data.event.EventLog;

/**
 * A game session with event occurring in a {@link GameWorld}.
 *
 */
public class Game {
	
	/**
	 * Index of this game in the world.
	 */
	@Id
	private final int index;
	
	/**
	 * World this game is happening on.
	 */
	transient GameWorld world;
	
	/**
	 * Events that happened during this game.
	 */
	transient EventLog events;
	
	Game(int index) {
		this.index = index;
	}
	
	public GameWorld getWorld() {
		return world;
	}
	
	public EventLog getEventLog() {
		return events;
	}
}
