package ru.deelter.chat.commands;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.language.Lang;
import ru.deelter.chat.language.LanguageManager;
import ru.deelter.chat.language.LanguagePreference;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LanguageCommand implements TabExecutor {

	private static final List<String> POPULAR_LANGUAGES = List.of(
			"ru", "uk", "be", "kz", "en", "de", "fr", "es", "pt", "it", "nl", "pl",
			"cs", "sk", "hu", "ro", "bg", "el", "sv", "no", "da", "fi",
			"zh", "ja", "ko", "th", "vi", "id", "ms",
			"ar", "he", "tr", "fa", "ur",
			"hi", "sw", "zu", "af", "is", "lt", "lv", "et", "sq", "hr", "sr", "mk", "sl", "bs", "cy", "ga"
	);

	private static final Map<String, String> LANGUAGE_DISPLAY_NAMES = new HashMap<>();

	static {
		for (Locale locale : Locale.getAvailableLocales()) {
			String code = locale.getLanguage();
			if (code.isEmpty()) continue;
			String display = locale.getDisplayName(Locale.ENGLISH);
			if (display.isBlank() || display.equals(code)) {
				display = code.toUpperCase();
			}
			LANGUAGE_DISPLAY_NAMES.put(code, display);
		}
		for (String code : List.of("be", "kz", "uk", "zh", "ar", "he", "fa")) {
			LANGUAGE_DISPLAY_NAMES.putIfAbsent(code, code.toUpperCase());
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
	                         @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			sendMessageSafe("lang-player-only", sender);
			return true;
		}

		if (args.length == 0) {
			sendHelp(player);
			return true;
		}

		LanguageManager manager = BetterChat.getInstance().getLanguageManager();
		LanguagePreference preference = manager.getPreference(player);

		switch (args[0].toLowerCase()) {
			case "understand" -> {
				if (args.length == 1) {
					preference.setKnownLanguages(List.of());
					if (preference.getPrimaryLanguage() != null &&
							(preference.getPrimaryLanguage().equals("auto") ||
									(preference.getKnownLanguages().isEmpty() && !preference.getPrimaryLanguage().equals("auto")))) {
						preference.setPrimaryLanguage(null);
					}
					manager.savePreference(preference);
					sendMessageSafe("lang-understand-reset", player);
					return true;
				}
				List<String> rawTokens = new ArrayList<>();
				for (int i = 1; i < args.length; i++) {
					String[] parts = args[i].split("[ ,]+");
					for (String part : parts) {
						if (!part.isEmpty()) rawTokens.add(part.toLowerCase());
					}
				}
				List<String> validCodes = new ArrayList<>();
				List<String> invalidCodes = new ArrayList<>();
				for (String code : rawTokens) {
					if (isValidLanguageCodeStrict(code)) {
						validCodes.add(code);
					} else {
						invalidCodes.add(code);
					}
				}
				if (!invalidCodes.isEmpty()) {
					for (String inv : invalidCodes) {
						showSuggestions(player, inv);
					}
					return true;
				}
				List<String> known = validCodes.stream().distinct().collect(Collectors.toList());
				preference.setKnownLanguages(known);
				if (preference.getPrimaryLanguage() == null && !known.isEmpty()) {
					preference.setPrimaryLanguage(known.get(0));
				}
				manager.savePreference(preference);
				String displayNames = known.stream()
						.map(LanguageCommand::getDisplayName)
						.collect(Collectors.joining(", "));
				sendMessageSafe("lang-set-success", player, "langs", displayNames);
			}
			case "speak" -> {
				if (args.length == 1) {
					preference.setSpeakerLanguage(null);
					manager.savePreference(preference);
					sendMessageSafe("lang-speak-reset", player);
					return true;
				}
				String lang = args[1].toLowerCase();
				if (!isValidLanguageCodeStrict(lang)) {
					showSuggestions(player, lang);
					return true;
				}
				preference.setSpeakerLanguage(lang);
				manager.savePreference(preference);
				sendMessageSafe("lang-speak-success", player, "lang", getDisplayName(lang));
			}
			case "primary" -> {
				if (args.length == 1) {
					preference.setPrimaryLanguage(null);
					manager.savePreference(preference);
					sendMessageSafe("lang-primary-reset", player);
					return true;
				}
				String primary = args[1].toLowerCase();
				if (!isValidLanguageCode(primary) && !primary.equals("auto")) {
					showSuggestions(player, primary);
					return true;
				}
				preference.setPrimaryLanguage(primary);
				manager.savePreference(preference);
				String displayName = primary.equals("auto") ? "Auto" : getDisplayName(primary);
				sendMessageSafe("lang-primary-success", player, "lang", displayName);
			}
			case "toggle" -> {
				preference.setToggleMode(!preference.isToggleMode());
				manager.savePreference(preference);
				String modeKey = preference.isToggleMode() ? "lang-toggle-on" : "lang-toggle-off";
				sendMessageSafe(modeKey, player);
			}
			case "info" -> {
				String knownDisplay = preference.getKnownLanguages().isEmpty()
						? "(using client locale)"
						: preference.getKnownLanguages().stream()
						  .map(LanguageCommand::getDisplayName)
						  .collect(Collectors.joining(", "));
				String primaryDisplay = preference.getPrimaryLanguage() == null
						? "Auto"
						: (preference.getPrimaryLanguage().equals("auto") ? "Auto" : getDisplayName(preference.getPrimaryLanguage()));
				String toggleDisplay = preference.isToggleMode() ? "ON (original first)" : "OFF (translation first)";
				sendMessageSafe("lang-info-known", player, "langs", knownDisplay);
				sendMessageSafe("lang-info-primary", player, "primary", primaryDisplay);
				sendMessageSafe("lang-info-toggle", player, "mode", toggleDisplay);
			}
			case "reset" -> {
				preference.setKnownLanguages(List.of());
				preference.setPrimaryLanguage(null);
				preference.setToggleMode(false);
				preference.setSpeakerLanguage(null);
				manager.savePreference(preference);
				sendMessageSafe("lang-reset-success", player);
			}
			default -> sendHelp(player);
		}
		return true;
	}

	private void sendHelp(Player player) {
		sendMessageSafe("lang-help-text", player);
	}

	private void sendMessageSafe(String key, CommandSender target, String... placeholders) {
		Lang lang = BetterChat.getInstance().getLang();
		Component langMessage = lang.getMessage(key, target, placeholders);
		target.sendMessage(Objects.requireNonNullElseGet(langMessage,
				() -> Component.text("[BetterChat] Missing language key: " + key)));
	}

	private static boolean isValidLanguageCode(String code) {
		if (code == null || code.isEmpty()) return false;
		if (code.equals("auto")) return true;
		try {
			Locale locale = Locale.forLanguageTag(code);
			String display = locale.getDisplayName(Locale.ENGLISH);
			return !display.isBlank() && !display.equals(code);
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean isValidLanguageCodeStrict(String code) {
		if (code == null || code.isEmpty()) return false;
		try {
			Locale locale = Locale.forLanguageTag(code);
			String display = locale.getDisplayName(Locale.ENGLISH);
			return !display.isBlank() && !display.equals(code);
		} catch (Exception e) {
			return false;
		}
	}

	private static @NonNull String getDisplayName(@NonNull String code) {
		if (code.equals("auto")) return "Auto";
		String cached = LANGUAGE_DISPLAY_NAMES.get(code);
		if (cached != null) return cached;
		try {
			Locale locale = Locale.forLanguageTag(code);
			String display = locale.getDisplayName(Locale.ENGLISH);
			if (!display.isBlank()) {
				LANGUAGE_DISPLAY_NAMES.put(code, display);
				return display;
			}
		} catch (Exception ignored) {
		}
		return code.toUpperCase();
	}

	private static List<String> getSimilarLanguages(String input) {
		Set<String> allCodes = new HashSet<>(POPULAR_LANGUAGES);
		allCodes.addAll(LANGUAGE_DISPLAY_NAMES.keySet());
		List<Map.Entry<String, Integer>> scored = new ArrayList<>();
		for (String code : allCodes) {
			if (code.equals(input)) continue;
			int distance = StringUtils.getLevenshteinDistance(input, code);
			if (distance >= 0 && distance <= 2) {
				scored.add(Map.entry(code, distance));
			}
		}
		scored.sort(Comparator.comparingInt(Map.Entry::getValue));
		return scored.stream().limit(5).map(Map.Entry::getKey).collect(Collectors.toList());
	}

	private void showSuggestions(Player player, String invalidCode) {
		List<String> similar = getSimilarLanguages(invalidCode);
		if (similar.isEmpty()) {
			player.sendMessage("Invalid language code: " + invalidCode + ". No suggestions.");
		} else {
			player.sendMessage("Invalid language code: " + invalidCode + ". Did you mean: " + String.join(", ", similar) + "?");
		}
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
	                                  @NotNull String alias, @NotNull String @NonNull [] args) {
		if (args.length == 1) {
			return Stream.of("understand", "primary", "toggle", "info", "reset", "speak")
					.filter(s -> s.startsWith(args[0].toLowerCase()))
					.toList();
		}
		if (args.length >= 2 && (args[0].equalsIgnoreCase("understand") || args[0].equalsIgnoreCase("primary"))) {
			return POPULAR_LANGUAGES.stream()
					.filter(l -> l.startsWith(args[1].toLowerCase()))
					.toList();
		}
		if (args.length >= 2 && args[0].equalsIgnoreCase("speak")) {
			return POPULAR_LANGUAGES.stream()
					.filter(l -> l.startsWith(args[1].toLowerCase()))
					.toList();
		}
		return List.of();
	}
}