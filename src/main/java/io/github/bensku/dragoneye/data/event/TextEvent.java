package io.github.bensku.dragoneye.data.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An event that contains user-provided text.
 *
 */
public class TextEvent extends GameEvent {

	/**
	 * Event text.
	 */
	private final String text;
	
	@JsonCreator
	public TextEvent(@JsonProperty("text") String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
}
