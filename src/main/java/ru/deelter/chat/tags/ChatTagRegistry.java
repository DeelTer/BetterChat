package ru.deelter.chat.tags;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.chat.bukkit.BetterChat;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class ChatTagRegistry {

	private static volatile Set<ChatTag> TAGS = Set.of();

	public static void init() {
		FileConfiguration config = BetterChat.getInstance().getConfig();
		ConfigurationSection tags = config.getConfigurationSection("tags");
		if (tags == null) {
			TAGS = Set.of();
			return;
		}

		Set<ChatTag> built = new LinkedHashSet<>();
		tags.getKeys(false).forEach(key -> {

			ConfigurationSection tagSection = tags.getConfigurationSection(key);
			if (tagSection == null) return;

			ChatTag.ChatTagBuilder builder = ChatTag.builder()
					.id(key)
					.symbol(tagSection.getString("symbol"))
					.radius(tagSection.getDouble("radius"))
					.format(tagSection.getString("format"));

			if (tagSection.isConfigurationSection("global")) {
				ConfigurationSection globalSec = tagSection.getConfigurationSection("global");
				builder.global(true);
				builder.globalMode(globalSec.getString("mode", "whitelist"));
				builder.globalServers(globalSec.getStringList("servers"));
			} else if (tagSection.isBoolean("global")) {
				boolean globalValue = tagSection.getBoolean("global", false);
				builder.global(globalValue);
				if (globalValue) {
					builder.globalMode("whitelist");
					builder.globalServers(List.of());
				}
			} else {
				builder.global(false);
			}
			built.add(builder.build());
		});

		TAGS = Set.copyOf(built);
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
