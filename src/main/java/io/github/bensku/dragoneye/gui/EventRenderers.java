package io.github.bensku.dragoneye.gui;

import io.github.bensku.dragoneye.data.event.CombatEvent;
import io.github.bensku.dragoneye.data.event.LevelUpEvent;
import io.github.bensku.dragoneye.data.event.RestEvent;
import io.github.bensku.dragoneye.data.event.TextEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Event renderers as static methods. Use them as method references!
 *
 */
class EventRenderers {
	
	private static BorderPane newFrame(Paint background) {
		BorderPane pane = new BorderPane();
		pane.setBackground(new Background(new BackgroundFill(background, new CornerRadii(2), null)));
		pane.setPadding(new Insets(3, 2, 4, 2));
		return pane;
	}

	public static Parent textEvent(TextEvent event) {
		BorderPane pane = newFrame(Color.CORNSILK);
		pane.setLeft(new Label(event.getText()));
		pane.setRight(new Label("+" + event.getXp() + " xp"));
		return pane;
	}
	
	public static Parent levelUpEvent(LevelUpEvent event) {
		BorderPane pane = newFrame(Color.AQUAMARINE);
		pane.setLeft(new Label(event.getCharacter().getName() + " levels up!"));
		return pane;
	}
	
	public static Parent restEvent(RestEvent event) {
		BorderPane pane = newFrame(Color.CHARTREUSE);
		pane.setLeft(new Label(event.getKind() == RestEvent.Kind.SHORT ? "Short rest" : "Long rest"));
		return pane;
	}
	
	public static Parent combatEvent(CombatEvent event) {
		BorderPane pane = newFrame(Color.INDIANRED);
		pane.setLeft(new Label(event.getText()));
		pane.setRight(new Label("+" + event.getXp() + " xp"));
		return pane;
	}
}
