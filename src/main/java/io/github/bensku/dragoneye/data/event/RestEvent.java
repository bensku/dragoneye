package io.github.bensku.dragoneye.data.event;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(kind);
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
		RestEvent other = (RestEvent) obj;
		return kind == other.kind;
	}
	
}
