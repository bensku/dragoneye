package io.github.bensku.dragoneye.test;

import io.github.bensku.dragoneye.data.Game;
import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.data.PlayerCharacter;
import io.github.bensku.dragoneye.data.Universe;
import io.github.bensku.dragoneye.data.event.EventLog;
import io.github.bensku.dragoneye.data.event.EventTypes;

public class TestData {

	public final Universe universe;
	public final GameWorld world;
	public final PlayerCharacter pc;
	public final Game game;
	public final EventLog log;
	
	public TestData() {
		EventTypes.finishRegistrations();
		this.universe = Universe.create();
		this.world = universe.createWorld();
		this.pc = world.createCharacter();
		pc.addXp(10_000_000); // To level 20
		world.updateCharacter(pc);
		this.game = world.createGame();
		this.log = game.getEventLog();
	}
}
