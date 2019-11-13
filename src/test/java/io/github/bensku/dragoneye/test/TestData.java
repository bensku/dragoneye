package io.github.bensku.dragoneye.test;

import org.dizitart.no2.Nitrite;

import io.github.bensku.dragoneye.data.Game;
import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.data.PlayerCharacter;
import io.github.bensku.dragoneye.data.Universe;
import io.github.bensku.dragoneye.data.event.EventLog;

public class TestData {

	public final Universe universe;
	public final GameWorld world;
	public final PlayerCharacter pc;
	public final Game game;
	public final EventLog log;
	
	public TestData() {
		this.universe = new Universe(Nitrite.builder().openOrCreate());
		this.world = universe.createWorld();
		this.pc = world.createCharacter();
		this.game = world.createGame();
		this.log = game.getEventLog();
	}
}
