package io.github.bensku.dragoneye.data.event;

import java.time.Instant;

import org.dizitart.no2.objects.Id;

/**
 * An event in {@link EventLog}.
 *
 */
public class GameEvent {
	
	/**
	 * Index of this event in log. -1 when this event is not in a log.
	 */
	@Id
	private int logIndex;

	/**
	 * When the event was created. This will NEVER be modified.
	 * This is in UTC time.
	 */
	private final Instant creationTime;
	
	/**
	 * XP gained by characters due to this event.
	 */
	private int xp;
	
	protected GameEvent() {
		this.logIndex = -1;
		this.creationTime = Instant.now();
		this.xp = 0;
	}
	
	/**
	 * Gets index of this event in the event log.
	 * @return Index of the event.
	 * @throws IllegalStateException If this event is currently not in an
	 * {@link EventLog}.
	 */
	public int getLogIndex() {
		if (logIndex == -1) {
			throw new IllegalStateException("event is not in a log");
		}
		return logIndex;
	}
	
	/**
	 * Sets log index. For internal use of {@link EventLog.Mutator} only.
	 * @param logIndex New log index.
	 */
	void setLogIndexInternal(int logIndex) {
		this.logIndex = logIndex;
	}
	
	public Instant getCreationTime() {
		return creationTime;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

}
