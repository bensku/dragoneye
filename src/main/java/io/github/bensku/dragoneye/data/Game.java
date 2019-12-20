package io.github.bensku.dragoneye.data;

import java.time.Instant;
import java.util.Objects;

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
	final int index;
	
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

	@Override
	public int hashCode() {
		return Objects.hash(creationTime, index, worldId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Game other = (Game) obj;
		return creationTime == other.creationTime && index == other.index && worldId == other.worldId;
	}
	
}
