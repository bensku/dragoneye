package io.github.bensku.dragoneye.data.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CombatEvent extends TextEvent {

	/**
	 * Creates a new combat event.
	 * @param text Text description of what happened.
	 * @param xp XP gained.
	 */
	@JsonCreator
	public CombatEvent(@JsonProperty("text") String text, @JsonProperty("xp") int xp) {
		super(text, xp);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
