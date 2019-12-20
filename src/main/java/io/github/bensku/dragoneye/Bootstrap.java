package io.github.bensku.dragoneye;

import java.nio.file.Paths;

import io.github.bensku.dragoneye.data.Universe;
import io.github.bensku.dragoneye.data.event.EventTypes;
import io.github.bensku.dragoneye.gui.DragoneyeApp;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Entry point of Dragoneye.
 *
 */
public class Bootstrap {

	/**
	 * Launches the Dragoneye GUI.
	 * @param args Command-line arguments. Currently none are used.
	 */
	public static void main(String... args) {
		// Application setup
		EventTypes.finishRegistrations();
		
		// Load universe
		Universe universe = Universe.openOrCreate(Paths.get("dragoneye.db"));
		
		Platform.startup(() -> {
			DragoneyeApp app = new DragoneyeApp(universe);
			app.start(new Stage());
		});
	}
}
