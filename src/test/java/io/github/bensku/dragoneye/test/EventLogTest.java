package io.github.bensku.dragoneye.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.github.bensku.dragoneye.data.event.CombatEvent;
import io.github.bensku.dragoneye.data.event.GameEvent;
import io.github.bensku.dragoneye.data.event.LevelUpEvent;
import io.github.bensku.dragoneye.data.event.LogAction;
import io.github.bensku.dragoneye.data.event.RestEvent;
import io.github.bensku.dragoneye.data.event.TextEvent;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class EventLogTest {

	private TestData data = new TestData();

	@Test
	public void manualAction() {
		// doAction is public API, even if all current uses are in EventLog
		AtomicBoolean done = new AtomicBoolean();
		LogAction action = new LogAction((mut, game) -> {
			assertFalse(done.get());
			done.set(true);
		},
				(mut, game) -> {
					assertTrue(done.get());
					done.set(false);
				});
		data.log.doAction(action);
		assertTrue(done.get());
		assertTrue(action.hasBeenExecuted());
		assertThrows(IllegalArgumentException.class, () -> data.log.doAction(action));
		
		data.log.undo();
		assertFalse(done.get());
		assertFalse(action.hasBeenExecuted());
		assertDoesNotThrow(() -> data.log.doAction(action));
	}

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
		for (int i = 0; i < 100; i++) {
			int type = i % 4;
			GameEvent event;
			switch (type) {
			case 0:
				event = new CombatEvent("some text", i);
				break;
			case 1:
				event = new LevelUpEvent(data.pc, i / 5);
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

		for (int i = 0; i < 100; i++) {
			final int iF = i;
			assertEquals(events.get(i), data.log.getAllEvents().filter(event -> event.getLogIndex() == iF).findFirst().get());
		}
	}

	@Test
	public void undoRedo() {
		LevelUpEvent event = new LevelUpEvent(data.pc, 10);
		data.log.addEvent(event);

		data.log.undo();
		data.log.undo(); // Undoing nothingness should do nothing
		assertEquals(0, data.log.getAllEvents().count());
		assertThrows(IllegalStateException.class, () -> event.getLogIndex());

		data.log.redo();
		data.log.redo(); // Redoing nonexistent event should do nothing
		assertEquals(event, data.log.getAllEvents().findFirst().get());
		assertEquals(0, event.getLogIndex());
	}

	@Test
	public void removeEvent() {
		LevelUpEvent event = new LevelUpEvent(data.pc, 10);
		data.log.addEvent(event);
		data.log.removeEvent(event);

		assertEquals(0, data.log.getAllEvents().count());

		// Undo should bring the event back!
		data.log.undo();
		assertEquals(event, data.log.getAllEvents().findFirst().get());
	}
	
	@Test
	public void dependentEvents() {
		TextEvent event = new TextEvent("300 xp!", 300);
		data.log.addEvent(event);
		List<GameEvent> events = data.log.getAllEvents().collect(Collectors.toList());
		assertEquals(2, events.size());
		assertEquals(event, events.get(0));
		assertEquals(LevelUpEvent.class, events.get(1).getClass());
		data.pc = data.world.getCharacter(0); // It should have been updated in db
		assertEquals(300, data.pc.getXp());
		assertEquals(2, data.pc.getLevel());
		
		data.log.removeEvent(event);
		data.pc = data.world.getCharacter(0); // It should have been updated in db
		assertEquals(0, data.log.getAllEvents().count());
		assertEquals(0, data.pc.getXp());
		assertEquals(1, data.pc.getLevel());
	}
}
