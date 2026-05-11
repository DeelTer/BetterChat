package ru.deelter.chat.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.BetterChat;

import java.util.Locale;

@UtilityClass
public final class PlayerLanguageUtil {
	private static NamespacedKey langKey;

	private static NamespacedKey getLangKey() {
		if (langKey == null) {
			langKey = new NamespacedKey(BetterChat.getInstance(), "chat_language");
		}
		return langKey;
	}

	public static Locale getLocale(@NotNull Player player) {
		String stored = player.getPersistentDataContainer().get(getLangKey(), PersistentDataType.STRING);
		if (stored != null && !stored.isEmpty()) {
			return Locale.forLanguageTag(stored);
		}
		return player.locale();
	}

	public static void setLocale(@NotNull Player player, String languageCode) {
		player.getPersistentDataContainer().set(getLangKey(), PersistentDataType.STRING, languageCode);
	}
}