package ru.deelter.chat.utils.tags;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ChatTagRegistry {

	public static final Set<ChatTag> TAGS = new HashSet<>();

	public static void init() {
		FileConfiguration config = ru.deelter.chat.BetterChat.getInstance().getConfig();
		ConfigurationSection tags = config.getConfigurationSection("tags");
		if (tags == null) return;

		tags.getKeys(false).forEach(key -> {

			ConfigurationSection tag = tags.getConfigurationSection(key);
			if (tag == null) return;

			TAGS.add(ChatTag.builder()
					.id(key)
					.symbol(tag.getString("symbol"))
					.radius(tag.getDouble("radius"))
					.format(tag.getString("format"))
					.build());
		});
	}

	public static @Nullable ChatTag getSuitable(@NotNull String text) {
		for (ChatTag tag : TAGS) {
			if (text.startsWith(tag.getSymbol())) {
				return tag;
			}
		}
		return null;
	}
}
