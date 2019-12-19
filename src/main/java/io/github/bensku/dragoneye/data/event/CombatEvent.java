package io.github.bensku.dragoneye.data.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CombatEvent extends TextEvent {

	@JsonCreator
	public CombatEvent(@JsonProperty("text") String text, @JsonProperty("xp") int xp) {
		super(text, xp);
	}

}
