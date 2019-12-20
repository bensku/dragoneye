package io.github.bensku.dragoneye.gui.model;

import io.github.bensku.dragoneye.data.GameWorld;
import io.github.bensku.dragoneye.data.Universe;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A model of all worlds in an universe.
 *
 */
public class WorldListModel {

	/**
	 * Backing universe of this world list.
	 */
	private final Universe universe;
	
	/**
	 * The worlds.
	 */
	private final ObservableList<GameWorld> worlds;
	
	/**
	 * Creates a new model of all worlds in given universe.
	 * @param universe Universe to get worlds from.
	 */
	public WorldListModel(Universe universe) {
		this.universe = universe;
		this.worlds = FXCollections.observableArrayList();
		
		universe.getWorlds().forEach(worlds::add); // Add worlds from storage
		worlds.addListener(this::worldsChanged);
	}
	
	/**
	 * Handles removal of worlds only.
	 * {@link #createWorld()}.
	 * @param c List change event.
	 */
	private void worldsChanged(ListChangeListener.Change<? extends GameWorld> c) {
		while (c.next()) {
			c.getRemoved().stream().forEach(universe::removeWorld);
		}
	}
	
	/**
	 * Gets all worlds.
	 * @return Observable list of worlds.
	 */
	public ObservableList<GameWorld> getWorlds() {
		return worlds;
	}
	
	/**
	 * Creates a new world and append it to end of the list.
	 * @return A new game world.
	 */
	public GameWorld createWorld() {
		GameWorld world = universe.createWorld();
		worlds.add(world);
		return world;
	}
	
	/**
	 * Updates changes made to given world to the database.
	 * @param world The world that has been updated.
	 */
	public void updateWorld(GameWorld world) {
		universe.updateWorld(world);
	}
}
