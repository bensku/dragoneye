package io.github.bensku.dragoneye.data.event;

import java.util.Objects;

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
	public TextEvent(@JsonProperty("text") String text, @JsonProperty("xp") int xp) {
		super(xp);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(text);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TextEvent other = (TextEvent) obj;
		return Objects.equals(text, other.text);
	}
	
}
