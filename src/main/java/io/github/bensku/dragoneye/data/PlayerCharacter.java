package io.github.bensku.dragoneye.data;

import java.util.Objects;

/**
 * A character in game {@link World} played by player (as opposed to NPC, 
 * played by the GM).
 *
 */
public class PlayerCharacter {

	/**
	 * Name of the character.
	 */
	private String name;
	
	/**
	 * Character class.
	 */
	private String charClass;
	
	/**
	 * Current XP the character has.
	 */
	private int xp;
	
	/**
	 * Overrides character level, so that it is not calculated from XP.
	 */
	private int levelOverride;
	
	public PlayerCharacter() {
		this.name = "";
		this.charClass = "";
		this.xp = 0;
		this.levelOverride = -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCharClass() {
		return charClass;
	}

	public void setCharClass(String charClass) {
		this.charClass = charClass;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}
	
	private int computeLevel() {
		// Leveling in D&D 5e doesn't follow any mathematical formula, so we use a lookup table
		// Table source: 5e SRD
		if (xp < 300) return 1;
		else if (xp < 900) return 2;
		else if (xp < 2700) return 3;
		else if (xp < 6500) return 4;
		else if (xp < 14_000) return 5;
		else if (xp < 23_000) return 6;
		else if (xp < 34_000) return 7;
		else if (xp < 48_000) return 8;
		else if (xp < 64_000) return 9;
		else if (xp < 85_000) return 10;
		else if (xp < 100_000) return 11;
		else if (xp < 120_000) return 12;
		else if (xp < 140_000) return 13;
		else if (xp < 165_000) return 14;
		else if (xp < 195_000) return 15;
		else if (xp < 225_000) return 16;
		else if (xp < 265_000) return 17;
		else if (xp < 305_000) return 18;
		else if (xp < 355_000) return 19;
		else return 20;
	}
	
	/**
	 * Gets level of this character. If it has not been overridden, it is
	 * calculated based on XP.
	 * @return Character level.
	 */
	public int getLevel() {
		if (levelOverride != -1) {
			return levelOverride;
		} else {
			return computeLevel();
		}
	}
	
	/**
	 * Overrides level of this character.
	 * @param level Level override.
	 */
	public void overrideLevel(int level) {
		Objects.checkIndex(level, 21); // Level 0 characters are not RAW, but they're sometimes used
		levelOverride = level;
	}
	
	/**
	 * Removes a level override from this character.
	 */
	public void removeLevelOverride() {
		levelOverride = -1;
	}
}
