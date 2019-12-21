package io.github.bensku.dragoneye.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class GameWorldTest {

	private TestData data = new TestData();
	
	@Test
	public void gettingGames() {
		assertEquals(data.game, data.world.getGames().findFirst().get());
	}
	
	@Test
	public void removeCharacter() {
		data.world.removeCharacter(data.pc);
		assertEquals(0, data.world.getCharacters().totalCount());
	}
	
	@Test
	public void removeGame() {
		data.world.removeGame(data.game);
		assertEquals(0, data.world.getGames().count());
	}
}
