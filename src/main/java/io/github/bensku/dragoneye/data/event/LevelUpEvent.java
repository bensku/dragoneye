package io.github.bensku.dragoneye.data.event;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.bensku.dragoneye.data.Game;
import io.github.bensku.dragoneye.data.PlayerCharacter;

/**
 * Event that is triggered when a character levels up.
 *
 */
public class LevelUpEvent extends GameEvent {

	/**
	 * Character that leveled up.
	 */
	private transient PlayerCharacter character;
	
	/**
	 * Character id.
	 */
	private final int charId;
	
	/**
	 * Level reached due to this event.
	 */
	private final int level;
	
	@JsonCreator
	private LevelUpEvent(@JsonProperty("charId") int charId, @JsonProperty("level") int level) {
		this.charId = charId;
		this.level = level;
	}
	
	public LevelUpEvent(PlayerCharacter character, int level) {
		this.character = character;
		this.charId = character.getId();
		this.level = level;
	}
	
	public PlayerCharacter getCharacter() {
		Objects.requireNonNull(character, "not injected yet");
		return character;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public void inject(Game game) {
		character = game.getWorld().getCharacter(charId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(charId, level);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LevelUpEvent other = (LevelUpEvent) obj;
		return charId == other.charId && level == other.level;
	}
	
}
