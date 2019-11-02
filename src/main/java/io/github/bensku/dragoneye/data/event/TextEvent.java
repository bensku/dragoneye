package io.github.bensku.dragoneye.data.event;

/**
 * An event that contains user-provided text.
 *
 */
public class TextEvent extends GameEvent {

	/**
	 * Event text.
	 */
	private String text;
	
	public TextEvent(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
