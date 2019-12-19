package io.github.bensku.dragoneye.data;

import java.time.Instant;

import org.dizitart.no2.objects.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
     * When this game was created (ms since epoch).
     */
    private final long creationTime;
	
	/**
	 * World this game is happening on.
	 */
	transient GameWorld world;
	
	/**
	 * Events that happened during this game.
	 */
	transient EventLog events;
	
	@JsonCreator
	Game(@JsonProperty("worldId") int worldId, @JsonProperty("id") int id) {
		this.worldId = worldId;
		this.index = id;
		this.creationTime = System.currentTimeMillis();
	}
	
	public GameWorld getWorld() {
		return world;
	}
	
	public EventLog getEventLog() {
		return events;
	}
	
	public Instant getCreationTime() {
	    return Instant.ofEpochMilli(creationTime);
	}
}
