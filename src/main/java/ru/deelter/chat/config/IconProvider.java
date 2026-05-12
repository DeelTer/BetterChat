package ru.deelter.chat.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class IconProvider {
	private static final Map<String, Component> iconsCache = new HashMap<>();
	private static FileConfiguration config;

	public static void init(FileConfiguration config) {
		IconProvider.config = config;
		iconsCache.clear();
	}

	public static @NonNull Component getIcon(String key) {
		return iconsCache.computeIfAbsent(key, name -> {
			String raw = config.getString("icons." + name, null);
			if (raw == null || raw.isEmpty()) return Component.empty();

			if (raw.contains("<") && raw.contains(">")) {
				try {
					return MiniMessage.miniMessage().deserialize(raw);
				} catch (Exception e) {
					e.printStackTrace();
					return Component.empty();
				}
			}
			return Component.text(raw);
		});
	}
}