package ru.deelter.chat.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NonNull;

public final class AntiSpamConfig {
	@Getter
	private static boolean enabled = true;
	@Getter
	private static double similarityThreshold = 0.9;
	@Getter
	private static int recentMessagesCount = 5;

	private AntiSpamConfig() {
	}

	public static void init(@NonNull FileConfiguration config) {
		enabled = config.getBoolean("anti-spam.enabled", true);
		similarityThreshold = config.getDouble("anti-spam.similarity-threshold", 0.85);
		recentMessagesCount = config.getInt("anti-spam.recent-messages-count", 5);
	}
}