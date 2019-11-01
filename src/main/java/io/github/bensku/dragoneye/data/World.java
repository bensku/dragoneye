package io.github.bensku.dragoneye.data;

import java.util.ArrayList;
import java.util.List;

public class World {

	/**
	 * Display name of the world.
	 */
	private String name;
	
	/**
	 * Player characters in this world.
	 */
	private final List<PlayerCharacter> pcs;
	
	public World() {
		this.name = "";
		this.pcs = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PlayerCharacter> getCharacters() {
		throw new UnsupportedOperationException("TODO");
	}
}
