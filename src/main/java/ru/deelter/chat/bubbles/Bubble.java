package ru.deelter.chat.bubbles;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

@Getter
@Setter
@EqualsAndHashCode
public class Bubble {

	private final Entity source;
	private int displayTime;
	private Component component;
	private int width = 200;
	private TextDisplay display;
	private Display.Billboard billboard = Display.Billboard.VERTICAL;
	private boolean shadowed = false;
	private boolean seeThrough = false;
	private boolean background = false;
	private Transformation transformation = new Transformation(new Vector3f(0F, 0.45F, 0F), new Quaternionf(), new Vector3f(1F), new Quaternionf());
	private Consumer<TextDisplay> bubbleEntityConsumer;

	public Bubble(@NotNull Entity source, @NotNull Component component, int displayTime) {
		this.source = source;
		this.displayTime = displayTime;
		this.component = component;
	}

	public Bubble(@NotNull Entity source, @NotNull Component component) {
		this(source, component, Math.max(PlainTextComponentSerializer.plainText().serialize(component).length() * 2, 40));
	}

	public void setComponent(@NotNull Component component) {
		this.component = component;
	}

	public @Nullable TextDisplay getDisplay() {
		return display;
	}

	public void show() {
		Location location = source.getLocation();
		display = location.getWorld().spawn(location, TextDisplay.class);
		display.text(component);
		display.setShadowed(shadowed);
		display.setSeeThrough(seeThrough);
		display.setBillboard(Display.Billboard.CENTER);
		display.setLineWidth(width);
		display.setTransformation(transformation);
		display.setDefaultBackground(background);
		display.setPersistent(false);

		if (bubbleEntityConsumer != null) {
			bubbleEntityConsumer.accept(display);
		}
		source.addPassenger(display);
	}

	public void queue() {
		BubbleQueue bubbleQueue = BubbleQueue.get(source.getUniqueId());
		bubbleQueue.add(this);
		bubbleQueue.register();
	}

	public void remove() {
		if (display != null)
			display.remove();
	}
}
