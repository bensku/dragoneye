package io.github.bensku.dragoneye.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.github.bensku.dragoneye.data.event.CombatEvent;
import io.github.bensku.dragoneye.data.event.GameEvent;
import io.github.bensku.dragoneye.data.event.LevelUpEvent;
import io.github.bensku.dragoneye.data.event.RestEvent;
import io.github.bensku.dragoneye.data.event.TextEvent;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class EventLogTest {

	private TestData data = new TestData();
	
	@Test
	public void simpleEvent() {
		LevelUpEvent event = new LevelUpEvent(data.pc, 10);
		data.log.addEvent(event);
		LevelUpEvent fromDb = (LevelUpEvent) data.log.getAllEvents().findFirst().get();
		assertEquals(event, fromDb);
		assertEquals(event.getLevel(), fromDb.getLevel());
	}
	
	@Test
	public void manyEvents() {
		List<GameEvent> events = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			int type = i % 4;
			GameEvent event;
			switch (type) {
			case 0:
				event = new CombatEvent("some text", i);
				break;
			case 1:
				event = new LevelUpEvent(data.pc, i / 50);
				break;
			case 2:
				event = new RestEvent(RestEvent.Kind.LONG);
				break;
			case 3:
				event = new TextEvent("text event", i);
				break;
			default:
				throw new AssertionError("modulo broke");
			}
			
			events.add(event);
			data.log.addEvent(event);
		}
		
		for (int i = 0; i < 1000; i++) {
			final int iF = i;
			assertEquals(events.get(i), data.log.getAllEvents().filter(event -> event.getLogIndex() == iF).findFirst().get());
		}
	}
}
