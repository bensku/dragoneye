package io.github.bensku.dragoneye.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.github.bensku.dragoneye.data.event.LevelUpEvent;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class EventLogTest {

	private TestData data = new TestData();
	
	@Test
	public void simpleEvent() {
		LevelUpEvent event = new LevelUpEvent(data.pc, 10);
		data.log.addEvent(event);
		assertEquals(event, data.log.getAllEvents().findFirst().get());
	}
}
