package ru.deelter.chat.config;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NonNull;

public class IconProvider {
	private static FileConfiguration config;

	public static void init(FileConfiguration config) {
		IconProvider.config = config;
	}

	public static @NonNull Component getIcon(String key) {
		String symbol = config.getString("icons." + key, "?");
		return Component.text(symbol);
	}
}