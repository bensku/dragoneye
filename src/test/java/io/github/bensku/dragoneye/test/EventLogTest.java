package io.github.bensku.dragoneye.test;

import java.util.ArrayList;

import org.dizitart.no2.Nitrite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.github.bensku.dragoneye.data.event.EventLog;
import io.github.bensku.dragoneye.data.event.GameEvent;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class EventLogTest {

	private EventLog log = new EventLog(new ArrayList<>(), -1, Nitrite.builder().openOrCreate().getRepository(GameEvent.class));
	
	// TODO actual test cases
}
