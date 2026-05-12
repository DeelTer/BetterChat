package ru.deelter.chat.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NonNull;

public final class BubbleConfig {

	@Getter
	private static int queueTickInterval = 10;
	@Getter
	private static int defaultWidth = 200;
	@Getter
	private static int minTicks = 40;
	@Getter
	private static double offsetY = 0.45;
	@Getter
	private static String billboard = "CENTER";
	@Getter
	private static boolean shadow = false;
	@Getter
	private static boolean seeThrough = false;
	@Getter
	private static boolean defaultBackground = false;
	@Getter
	private static boolean popUpEnabled = true;
	@Getter
	private static double popUpStartScale = 0.2;
	@Getter
	private static int popUpDurationTicks = 8;
	@Getter
	private static double minTpsForBubbles = 15.0;
	@Getter
	private static double minTpsForAnimation = 18.0;

	public static void init(@NonNull FileConfiguration config) {
		queueTickInterval = config.getInt("bubbles.queue-tick-interval", 5);
		defaultWidth = config.getInt("bubbles.default-width", 200);
		minTicks = config.getInt("bubbles.min-ticks", 40);
		offsetY = config.getDouble("bubbles.offset-y", 0.45);
		billboard = config.getString("bubbles.billboard", "CENTER");
		shadow = config.getBoolean("bubbles.shadow", false);
		seeThrough = config.getBoolean("bubbles.see-through", false);
		defaultBackground = config.getBoolean("bubbles.default-background", false);
		popUpEnabled = config.getBoolean("bubbles.animation.pop-up.enabled", true);
		popUpStartScale = config.getDouble("bubbles.animation.pop-up.start-scale", 0.2);
		popUpDurationTicks = config.getInt("bubbles.animation.pop-up.duration-ticks", 8);

		minTpsForBubbles = config.getDouble("min-tps-for-bubbles", 15.0);
		minTpsForAnimation = config.getDouble("bubbles.min-tps-for-animation", 18.0);

		System.out.println("bubbles.default-width: " + defaultWidth);
		System.out.println("bubbles.min-ticks: " + minTicks);
		System.out.println("bubbles.offset-y: " + offsetY);
		System.out.println("bubbles.billboard: " + billboard);
		System.out.println("bubbles.shadow: " + shadow);
		System.out.println("bubbles.see-through: " + seeThrough);
		System.out.println("bubbles.default-background: " + defaultBackground);
		System.out.println("bubbles.pop-up.enabled: " + popUpEnabled);
		System.out.println("bubbles.pop-up.duration-ticks: " + popUpDurationTicks);
	}
}