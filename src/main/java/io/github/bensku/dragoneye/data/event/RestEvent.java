package io.github.bensku.dragoneye.data.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Short or long rest event.
 *
 */
public class RestEvent extends GameEvent {

	public enum Kind {
		SHORT,
		LONG
	}
	
	private final Kind kind;
	
	public RestEvent(@JsonProperty("kind") Kind kind) {
		this.kind = kind;
	}
	
	/**
	 * Gets kind of the rest that was taken.
	 * @return Rest kind.
	 */
	public Kind getKind() {
		return kind;
	}
}
