package io.github.bensku.dragoneye.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bensku.dragoneye.data.Universe;
import io.github.bensku.dragoneye.gui.view.WorldSelectView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DragoneyeApp extends Application {
	
	private static final Logger LOG = LoggerFactory.getLogger(DragoneyeApp.class);
	
	/**
	 * Universe this application instance is working on.
	 */
	private final Universe universe;
	
	public DragoneyeApp(Universe universe) {
		this.universe = universe;
	}

	@Override
	public void start(Stage stage) {
		try {
			WorldSelectView view = new WorldSelectView(universe);
			stage.setScene(new Scene(view));
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

	/**
	 * Handles an uncaught exception (i.e. a crash).
	 * @param t Thread the exception was thrown in.
	 * @param e The exception.
	 */
	private static void handleCrash(Thread t, Throwable e) {
		LOG.error("Uncaught exception; crashing to prevent data corruption");
		e.printStackTrace(); // TODO remove, gate behind system property?
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Dragoneye crash");
		alert.setContentText("Dragoneye has crashed due to a critical error!");
		alert.showAndWait();
		
		System.exit(1);
	}
}
