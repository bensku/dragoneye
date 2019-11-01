package io.github.bensku.dragoneye.data;

import io.github.bensku.dragoneye.data.event.EventLog;

/**
 * A game session with event occurring in a {@link World}.
 *
 */
public class Game {

	/**
	 * Index of this game in world.
	 */
	private final int index;
	
	/**
	 * Events that happened during this game.
	 */
	private final EventLog events;
}
