package io.github.bensku.dragoneye.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CharacterTest {

	private TestData data = new TestData();
	
	@Test
	public void manageXp() {
		assertFalse(data.pc.addXp(0));
		assertThrows(IllegalArgumentException.class, () -> data.pc.addXp(-1));
		assertTrue(data.pc.addXp(300)); // Level up to level 2
		assertEquals(2, data.pc.getLevel());
	}
	
	@Test
	public void overrideLevel() {
		// Level overrides are not visible in UI yet, but APIs for them are stable
		data.pc.addXp(300); // To level 2?
		data.pc.overrideLevel(10); // No, level 10!
		assertEquals(10, data.pc.getLevel());
		assertThrows(IllegalArgumentException.class, () -> data.pc.overrideLevel(-1));
		assertThrows(IllegalArgumentException.class, () -> data.pc.overrideLevel(21));
		data.pc.removeLevelOverride(); // Back to level computed from XP
		assertEquals(2, data.pc.getLevel());
	}
	
	@Test
	public void longCampaign() {
		int level = 1;
		assertEquals(level, data.pc.getLevel());
		
		// Level up XP amounts from 5e SRD
		assertLevelUp(2, 300);
		assertLevelUp(3, 600);
		assertLevelUp(4, 1_800);
		assertLevelUp(5, 3_800);
		assertLevelUp(6, 7_500);
		assertLevelUp(7, 9_000);
		assertLevelUp(8, 11_000);
		assertLevelUp(9, 14_000);
		assertLevelUp(10, 16_000);
		assertLevelUp(11, 21_000);
		assertLevelUp(12, 15_000);
		assertLevelUp(13, 20_000);
		assertLevelUp(14, 20_000);
		assertLevelUp(15, 25_000);
		assertLevelUp(16, 30_000);
		assertLevelUp(17, 30_000);
		assertLevelUp(18, 40_000);
		assertLevelUp(19, 50_000);
		assertLevelUp(20, 50_000);
	}
	
	private int assertLevelUp(int level, int xp) {
		data.pc.addXp(xp);
		assertEquals(level++, data.pc.getLevel());
		return level;
	}
}
