package ru.deelter.chat.replacer;

import lombok.Builder;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Builder
public record ChatLink(List<String> urls, TextColor color, TextColor color2) {

	private static final List<ChatLink> LINKS = new ArrayList<>();
	private static final ChatLink DEFAULT = ChatLink.builder()
			.color(TextColor.fromHexString("#b9b9b9"))
			.color2(TextColor.fromHexString("#ffffff"))
			.urls(List.of(""))
			.build();

	public static void load(@NotNull FileConfiguration config) {

		ConfigurationSection links = config.getConfigurationSection("links");
		assert links != null;

		for (String linkId : links.getKeys(false)) {

			ConfigurationSection link = links.getConfigurationSection(linkId);
			if (link == null) continue;

			ChatLink chatLink = ChatLink.builder()
					.color(TextColor.fromHexString(link.getString("color", "#888888")))
					.color2(TextColor.fromHexString(link.getString("color2", "#888888")))
					.urls(link.getStringList("urls"))
					.build();
			LINKS.add(chatLink);
		}
		LINKS.add(DEFAULT);
	}

	public static void reload(FileConfiguration config) {
		LINKS.clear();
		LINKS.add(DEFAULT);
		load(config);
	}

	public static ChatLink getLinkByUrl(String url) {
		url = url.toLowerCase();
		for (ChatLink chatLink : LINKS) {
			for (String url2 : chatLink.urls()) {
				if (url.contains(url2)) return chatLink;
			}
		}
		return DEFAULT;
	}
}

