package io.github.bensku.dragoneye.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bensku.dragoneye.data.Game;
import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.data.Universe;
import io.github.bensku.dragoneye.data.event.CombatEvent;
import io.github.bensku.dragoneye.data.event.EventLog;
import io.github.bensku.dragoneye.data.event.LevelUpEvent;
import io.github.bensku.dragoneye.data.event.RestEvent;
import io.github.bensku.dragoneye.data.event.TextEvent;
import io.github.bensku.dragoneye.gui.controller.CreateEventController;
import io.github.bensku.dragoneye.gui.model.GameListModel;
import io.github.bensku.dragoneye.gui.model.WorldListModel;
import io.github.bensku.dragoneye.gui.view.EventListView;
import io.github.bensku.dragoneye.gui.view.GameRootView;
import io.github.bensku.dragoneye.gui.view.GameSelectView;
import io.github.bensku.dragoneye.gui.view.RootView;
import io.github.bensku.dragoneye.gui.view.WorldSelectView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DragoneyeApp extends Application {
	
	private static final Logger LOG = LoggerFactory.getLogger(DragoneyeApp.class);
	
	/**
	 * Universe this application instance is working on.
	 */
	private final Universe universe;
	
	/**
	 * Root view.
	 */
	private final RootView rootView;
	
	/**
	 * Creates a new instance of Dragoneye. Must be called from JavaFX thread.
	 * @param universe Universe to launch on.
	 */
	public DragoneyeApp(Universe universe) {
		this.universe = universe;
		this.rootView = new RootView();
	}

	@Override
	public void start(Stage stage) {
		try {
			openWorldSelect(universe);
			stage.setScene(new Scene(rootView, 900, 620));
			stage.show();
			
			// Startup is done, we should now be able to handle crashes
			Thread.currentThread().setUncaughtExceptionHandler(DragoneyeApp::handleCrash);
		} catch (Throwable e) {
			// Crashes during startup can't be handled by default exception handler
			stage.setScene(new Scene(new Pane())); // Clear window, using any UI element is unsafe
			Platform.runLater(() -> { // Show dialog and quit ASAP
				handleCrash(Thread.currentThread(), e);
			});
		}
	}
	
	private void openWorldSelect(Universe universe) {
		rootView.open("Select a world", new WorldSelectView(new WorldListModel(universe), this::openGameSelect));
	}
	
	private void openGameSelect(GameWorld world) {
		rootView.open("Select a game", new GameSelectView(new GameListModel(world), this::openGame));
	}
	
	private void openGame(Game game) {
		// Event list
		EventLog log = game.getEventLog();
		EventListView eventList = new EventListView(log);
		eventList.addType(TextEvent.class, EventRenderers::textEvent);
		eventList.addType(LevelUpEvent.class, EventRenderers::levelUpEvent);
		eventList.addType(RestEvent.class, EventRenderers::restEvent);
		eventList.addType(CombatEvent.class, EventRenderers::combatEvent);
		eventList.initialize();
		
		// Event creation tools
		CreateEventController createController = newCreateEventControl(log);
		
		// Open the view with both of them
		rootView.open("Game " + game.getCreationTime().toString(), new GameRootView(eventList, createController));
	}
	
	private CreateEventController newCreateEventControl(EventLog log) {
		return CreateEventController.builder()
				.eventType()
				.button(makeSneakyRadioButton("Text"))
				.constructor((details, xp) -> details.isEmpty() ? null : new TextEvent(details, xp))
				.finish()
		.eventType()
				.button(makeSneakyRadioButton("Combat"))
				.constructor((details, xp) -> new CombatEvent(details, xp))
				.finish()
		.eventType()
				.button(makeSneakyRadioButton("Short rest"))
				.constructor((details, xp) -> new RestEvent(RestEvent.Kind.SHORT))
				.activateImmediately()
				.finish()
		.eventType()
				.button(makeSneakyRadioButton("Long rest"))
				.constructor((details, xp) -> new RestEvent(RestEvent.Kind.LONG))
				.activateImmediately()
				.finish()
		.eventListener(log::addEvent)
		.build();
	}
	
	private RadioButton makeSneakyRadioButton(String label) {
		RadioButton button = new RadioButton(label);
		button.getStyleClass().remove("radio-button");
		button.getStyleClass().add("toggle-button");
		return button;
	}

	/**
	 * Handles an uncaught exception (i.e. a crash).
	 * @param t Thread the exception was thrown in.
	 * @param e The exception.
	 */
	private static void handleCrash(Thread t, Throwable e) {
		LOG.error("Uncaught exception; crashing to prevent data corruption");
		e.printStackTrace(); // TODO remove, gate behind system property?
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Dragoneye: Critical Error");
		alert.setContentText("Dragoneye has encountered a critical error and must be closed.");
		alert.showAndWait();
		
		System.exit(1);
	}
}
