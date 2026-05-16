package ru.deelter.chat.language;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.utils.translator.OnlineTranslator;
import ru.deelter.chat.utils.translator.TranslationLanguage;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class LanguageManager {

	private final Cache<UUID, LanguagePreference> preferenceCache = Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(30))
			.maximumSize(1000)
			.build();
	private LanguageStorage storage;

	public void init(@NonNull LanguageStorage storage) {
		this.storage = storage;
		storage.init();
	}

	public void shutdown() {
		storage.shutdown();
		preferenceCache.invalidateAll();
	}

	public LanguagePreference getPreference(@NotNull Player player) {
		UUID uuid = player.getUniqueId();
		LanguagePreference pref = preferenceCache.getIfPresent(uuid);
		if (pref == null) {
			pref = storage.load(uuid);
			if (pref == null) {
				pref = new LanguagePreference();
				pref.setPlayerId(uuid);
				pref.setKnownLanguages(List.of());
				pref.setPrimaryLanguage(null);
				pref.setToggleMode(false);
				storage.save(pref);
			}
			preferenceCache.put(uuid, pref);
		}
		return pref;
	}

	public void savePreference(@NotNull LanguagePreference pref) {
		storage.save(pref);
		preferenceCache.put(pref.getPlayerId(), pref);
	}

	/**
	 * Определяет, нужно ли переводить сообщение для получателя.
	 *
	 * @param senderLocale язык отправителя
	 * @param receiver     игрок-получатель
	 * @return true – перевод нужен, false – оставить как есть
	 */
	public boolean shouldTranslate(Locale senderLocale, Player receiver) {
		LanguagePreference preference = getPreference(receiver);
		List<String> known = preference.getKnownLanguages();
		String primary = preference.getPrimaryLanguage();

		if (primary != null && primary.equals(senderLocale.getLanguage())) {
			return false;
		}
		if (known.contains(senderLocale.getLanguage())) {
			return false;
		}
		if (known.isEmpty()) {
			String clientLang = getLocale(receiver).getLanguage();
			return !clientLang.equals(senderLocale.getLanguage());
		}
		return true;
	}

	/**
	 * Получить язык, на который нужно перевести сообщение.
	 *
	 * @param receiver получатель
	 * @return Locale для перевода (never null)
	 */
	public @NotNull Locale getTargetTranslationLocale(@NotNull Player receiver) {
		LanguagePreference pref = getPreference(receiver);
		String primary = pref.getPrimaryLanguage();
		if (primary != null && !primary.isEmpty()) {
			return Locale.forLanguageTag(primary);
		}
		List<String> known = pref.getKnownLanguages();
		if (!known.isEmpty()) {
			return Locale.forLanguageTag(known.get(0));
		}
		return getLocale(receiver);
	}

	/**
	 * Применить перевод и опционально переключить режим отображения (toggle).
	 */
	public Component processTranslation(@NotNull Component original, @NotNull Locale senderLocale, @NotNull Player receiver) {
		if (!shouldTranslate(senderLocale, receiver)) {
			return original;
		}
		LanguagePreference preference = getPreference(receiver);
		Locale targetLocale = getTargetTranslationLocale(receiver);
		String originalPlain = PlainTextComponentSerializer.plainText().serialize(original);
		String translated = OnlineTranslator.translate(originalPlain, TranslationLanguage.AUTO, TranslationLanguage.from(targetLocale));
		Component translatedComp = Component.text(translated);

		if (preference.isToggleMode()) {
			return original.hoverEvent(HoverEvent.showText(translatedComp));
		} else {
			return translatedComp.hoverEvent(HoverEvent.showText(original));
		}
	}

	public Locale getLocale(@NotNull Player player) {
		LanguagePreference preference = getPreference(player);
		if (preference.getSpeakerLanguage() != null && !preference.getSpeakerLanguage().isEmpty()) {
			return Locale.forLanguageTag(preference.getSpeakerLanguage());
		}
		return player.locale();
	}
}