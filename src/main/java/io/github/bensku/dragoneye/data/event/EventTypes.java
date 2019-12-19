package io.github.bensku.dragoneye.data.event;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.bensku.dragoneye.data.Universe;

/**
 * A static registry of all event types.
 *
 */
public class EventTypes {
	
	/**
	 * Registered events.
	 */
	private static final Set<Class<? extends GameEvent>> events = new HashSet<>();
	
	/**
	 * If new registrations are allowed.
	 */
	private static boolean finished = false;
	
	/**
	 * Registers an event type. {@link #finishRegistrations()} must have not
	 * been called before this.
	 * @param type Event type to register.
	 */
	public static void registerEvent(Class<? extends GameEvent> type) {
		events.add(type);
	}

	/**
	 * Enables creation of {@link Universe}s, and registration of new event
	 * types. Calling this more than once is safe and has no effect.
	 */
	public static void finishRegistrations() {
		finished = true;
	}
	
	/**
	 * Gets registered event types. {@link #finishRegistrations()} must have
	 * been called before this is called.
	 * @return Event types.
	 * @throws IllegalStateException When called before registrations have been
	 * finished.
	 */
	public static Collection<Class<? extends GameEvent>> getEventTypes() {
		if (!finished) {
			throw new IllegalStateException("registrations not finished");
		}
		return Collections.unmodifiableCollection(events);
	}
	
	// Our default event types
	static {
		registerEvent(LevelUpEvent.class);
		registerEvent(TextEvent.class);
		registerEvent(CombatEvent.class);
		registerEvent(RestEvent.class);
	}
}
