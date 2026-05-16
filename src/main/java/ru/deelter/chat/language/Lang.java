package ru.deelter.chat.language;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.bukkit.BetterChat;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class Lang {

	private final BetterChat plugin;
	private final Map<String, Map<String, String>> languageMessages = new HashMap<>();
	private final MiniMessage miniMessage = MiniMessage.miniMessage();
	private String defaultLanguage;
	private boolean autoDetect;
	private boolean translationEnabled;

	public Lang(BetterChat plugin) {
		this.plugin = plugin;
		reload();
	}

	public void reload() {
		languageMessages.clear();
		defaultLanguage = plugin.getConfig().getString("language.default", "en");
		autoDetect = plugin.getConfig().getBoolean("language.auto-detect", true);
		translationEnabled = plugin.getConfig().getBoolean("language.translation", true);

		File langFolder = new File(plugin.getDataFolder(), "lang");
		if (!langFolder.exists()) {
			langFolder.mkdirs();
		}

		String[] resourceFiles = {"lang/en.yml", "lang/ru.yml"};
		for (String resourcePath : resourceFiles) {
			File target = new File(plugin.getDataFolder(), resourcePath);
			if (!target.exists()) {
				plugin.saveResource(resourcePath, false);
			}
		}

		File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
		if (files != null) {
			for (File file : files) {
				String langCode = file.getName().replace(".yml", "");
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				Map<String, String> messages = new HashMap<>();
				if (cfg.isConfigurationSection("messages")) {
					for (String key : cfg.getConfigurationSection("messages").getKeys(false)) {
						messages.put(key, cfg.getString("messages." + key));
					}
				}
				languageMessages.put(langCode, messages);
			}
		}
	}

	@Nullable
	public Component getMessage(String key, @Nullable CommandSender sender) {
		return getMessage(key, sender, TagResolver.empty());
	}

	@Nullable
	public Component getMessage(String key, CommandSender sender, TagResolver... resolvers) {
		Player player = (sender instanceof Player playerSender) ? playerSender : null;
		String raw = resolveRawMessage(key, player);
		if (raw == null || raw.isEmpty()) {
			return null;
		}
		TagResolver combined = TagResolver.resolver(resolvers);
		return miniMessage.deserialize(raw, combined);
	}

	@Nullable
	public Component getMessage(String key, CommandSender sender, String placeholderKey, String value) {
		return getMessage(key, sender, Placeholder.unparsed(placeholderKey, value));
	}

	private String resolveRawMessage(String key, Player player) {
		String lang = resolvePlayerLanguage(player);
		Map<String, String> messages = languageMessages.get(lang);
		if (messages != null && messages.containsKey(key)) {
			return messages.get(key);
		}
		Map<String, String> defaultMessages = languageMessages.get(defaultLanguage);
		if (defaultMessages != null && defaultMessages.containsKey(key)) {
			return defaultMessages.get(key);
		}
		return key;
	}

	private String resolvePlayerLanguage(Player player) {
		if (!autoDetect || player == null) {
			return defaultLanguage;
		}
		Locale locale = BetterChat.getInstance().getLanguageManager().getLocale(player);
		String shortLang = locale.toString().split("_")[0].toLowerCase();
		if (languageMessages.containsKey(shortLang)) {
			return shortLang;
		}
		return defaultLanguage;
	}

	@Nullable
	public Component getMessage(String key, @Nullable CommandSender sender, String @NonNull ... placeholders) {
		if (placeholders.length % 2 != 0) {
			throw new IllegalArgumentException("Placeholders must be key-value pairs");
		}
		TagResolver[] resolvers = new TagResolver[placeholders.length / 2];
		for (int i = 0; i < placeholders.length; i += 2) {
			resolvers[i / 2] = Placeholder.unparsed(placeholders[i], placeholders[i + 1]);
		}
		return getMessage(key, sender, resolvers);
	}
}