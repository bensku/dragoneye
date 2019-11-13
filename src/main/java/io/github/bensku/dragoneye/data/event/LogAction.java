package io.github.bensku.dragoneye.data.event;

import java.util.function.BiConsumer;

import io.github.bensku.dragoneye.data.Game;

/**
 * A change initiated by user to {@link EventLog}.
 *
 */
public class LogAction {

	/**
	 * Function that applies this action to an event log.
	 */
	private final BiConsumer<EventLog.Mutator, Game> applier;
	
	/**
	 * Function that undoes this action.
	 */
	private final BiConsumer<EventLog.Mutator, Game> undoer;
	
	/**
	 * Whether this action has been executed or not. Used by {@link EventLog}
	 * to safeguard against executing same action more once, which would break
	 * undo history.
	 */
	boolean executed;
	
	/**
	 * Creates a new log action. Callers should provide applier and undoer
	 * (e.g. with lambdas), not expose them as public constructor parameters.
	 * @param applier Consumer that applies this action to mutator.
	 * @param undoer Consumer that undoes this action in mutator.
	 */
	public LogAction(BiConsumer<EventLog.Mutator, Game> applier, BiConsumer<EventLog.Mutator, Game> undoer) {
		this.undoer = undoer;
		this.applier = applier;
		this.executed = false;
	}
	
	/**
	 * Applies this action to an event log.
	 * @param mutator Event log mutator.
	 * @param game Game the event log belongs to.
	 */
	protected void apply(EventLog.Mutator mutator, Game game) {
		if (executed) {
			throw new IllegalArgumentException("already applied");
		}
		applier.accept(mutator, game);
	}
	
	/**
	 * Undoes this action in an event log.
	 * @param mutator Event log mutator.
	 * @param game Game the event log belongs to.
	 */
	protected void undo(EventLog.Mutator mutator, Game game) {
		if (!executed) {
			throw new IllegalArgumentException("already undone");
		}
		undoer.accept(mutator, game);
	}
}
