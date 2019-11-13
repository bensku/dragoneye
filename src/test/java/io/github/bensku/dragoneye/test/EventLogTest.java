package io.github.bensku.dragoneye.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.github.bensku.dragoneye.data.event.LevelUpEvent;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class EventLogTest {

	private TestData data = new TestData();
	
	@Test
	public void simpleEvent() {
		data.log.addEvent(new LevelUpEvent(data.pc, 10));
		data.log.getAllEvents().forEach(e -> System.out.println(e));
	}
}
