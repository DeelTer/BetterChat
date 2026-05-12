package ru.deelter.chat.bubbles;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.deelter.chat.BetterChat;
import ru.deelter.chat.config.BubbleConfig;

import java.util.function.Consumer;

@Getter
@Setter
@EqualsAndHashCode
public class Bubble {

	private final Entity source;
	private int displayTime;
	private Component component;
	private int width = BubbleConfig.getDefaultWidth();
	private TextDisplay display;
	private Display.Billboard billboard = Display.Billboard.valueOf(BubbleConfig.getBillboard());
	private boolean shadowed = BubbleConfig.isShadow();
	private boolean seeThrough = BubbleConfig.isSeeThrough();
	private boolean background = BubbleConfig.isDefaultBackground();
	private Transformation transformation = new Transformation(
			new Vector3f(0F, (float) BubbleConfig.getOffsetY(), 0F),
			new Quaternionf(), new Vector3f(1F), new Quaternionf());
	private Consumer<TextDisplay> bubbleEntityConsumer;

	public Bubble(@NotNull Entity source, @NotNull Component component, int displayTime) {
		this.source = source;
		this.displayTime = displayTime;
		this.component = component;
	}

	public Bubble(@NotNull Entity source, @NotNull Component component) {
		this(source, component, Math.max(
				PlainTextComponentSerializer.plainText().serialize(component).length() * 2,
				BubbleConfig.getMinTicks()));
	}

	public void show() {
		Location location = source.getLocation();
		display = location.getWorld().spawn(location, TextDisplay.class, d -> {
			d.text(component);
			d.setShadowed(shadowed);
			d.setSeeThrough(seeThrough);
			d.setBillboard(billboard);
			d.setLineWidth(width);
			d.setDefaultBackground(background);
			d.setPersistent(false);
		});

		if (BubbleConfig.isPopUpEnabled() && Bukkit.getTPS()[0] >= BubbleConfig.getMinTpsForAnimation()) {
			float startScale = (float) BubbleConfig.getPopUpStartScale();
			int duration = BubbleConfig.getPopUpDurationTicks();

			display.setInterpolationDelay(0);
			display.setInterpolationDuration(0);
			display.setTransformation(new Transformation(
					transformation.getTranslation(),
					transformation.getLeftRotation(),
					new Vector3f(startScale, startScale, startScale),
					transformation.getRightRotation()
			));

			Bukkit.getScheduler().runTaskLater(BetterChat.getInstance(), () -> {
				if (display != null && display.isValid()) {
					display.setInterpolationDelay(0);
					display.setInterpolationDuration(duration);
					display.setTransformation(transformation);
				}
			}, 1L);
		} else {
			display.setTransformation(transformation);
		}

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