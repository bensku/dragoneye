package io.github.bensku.dragoneye.gui.view;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Shows last added view, but keeps track of previous views so the user can go
 * {@link #back()} to them.
 *
 */
public class RootView extends BorderPane {

	/**
	 * Views available.
	 */
	private final List<Parent> views;
	
	/**
	 * Titles of available views.
	 */
	private final List<String> titles;
	
	/**
	 * Title text on top of this view.
	 */
	private final StringProperty title;
	
	public RootView() {
		this.views = new ArrayList<>();
		this.titles = new ArrayList<>();
		
		Button backButton = new Button("<");
		backButton.setOnAction(e -> back());
		
		Label titleLabel = new Label();
		this.title = titleLabel.textProperty();
		
		HBox topRow = new HBox(backButton, titleLabel);
		setTop(topRow);
	}
	
	/**
	 * Goes back to previous view.
	 */
	public void back() {
		views.remove(views.size() - 1);
		titles.remove(titles.size() - 1);
		refresh();
	}
	
	/**
	 * Opens a new view.
	 * @param title Title of view.
	 * @param view View to display to user.
	 */
	public void open(String title, Parent view) {
		views.add(view);
		titles.add(title);
		refresh();
	}
	
	private void refresh() {
		setCenter(views.get(views.size() - 1));
		title.set(titles.get(titles.size() - 1));
	}
}
