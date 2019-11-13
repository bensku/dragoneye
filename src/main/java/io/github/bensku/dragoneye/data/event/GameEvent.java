package io.github.bensku.dragoneye.data.event;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.dizitart.no2.objects.Id;

import io.github.bensku.dragoneye.data.Game;
import io.github.bensku.dragoneye.data.PlayerCharacter;
import io.github.bensku.dragoneye.data.event.EventLog.Mutator;

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
	private final int xp;
	
	/**
	 * Events that were added due to this event. Null when there are no
	 * such events.
	 * TODO serialization
	 */
	private transient List<GameEvent> dependentEvents;
	
	protected GameEvent() {
	    this(0);
	}
	
	protected GameEvent(int xp) {
		this.logIndex = -1;
		this.creationTime = Instant.now();
		this.xp = xp;
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
	
	protected void addDependentEvent(GameEvent event) {
	    if (dependentEvents == null) {
	        dependentEvents = new ArrayList<>();
	    }
	    dependentEvents.add(event);
	}
	
	/**
	 * Called when this event is exposed from {@link EventLog}. Events may
	 * override it to get references to shared data from given name. An example
	 * of such data would be {@link PlayerCharacter}s.
	 * @param game Game this event belongs to.
	 */
	public void inject(Game game) {
		// No need to do that by default
	}
	
	/**
	 * Called when this event is added to an {@link EventLog}.
	 * @param mut Mutator for event log.
	 * @param game Game the event log is for.
	 */
	public void added(EventLog.Mutator mut, Game game) {
	    game.getWorld().getCharacters().forEach(pc -> {
	        if (pc.addXp(xp)) { // If enough XP for level up, add event for it
	            LevelUpEvent event = new LevelUpEvent(pc, pc.getLevel());
	            addDependentEvent(event);
	            mut.addEvent(event);
	        }
	    });
	}
	
	/**
     * Called when this event is removed from an {@link EventLog}.
     * @param mut Mutator for event log.
     * @param game Game the event log is for.
     */
	public void removed(EventLog.Mutator mut, Game game) {
        // Remove dependent events
        if (dependentEvents != null) {
            for (GameEvent event : dependentEvents) {
                mut.removeEvent(event);
            }
            dependentEvents = null;
        }
        
        // Take away added XP
        game.getWorld().getCharacters().forEach(pc -> {
            pc.setXp(pc.getXp() - xp);
        });
	}

}
