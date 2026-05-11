package ru.deelter.chat.utils.translator;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public record TranslationLanguage(
		@NotNull String id,
		@NotNull String displayName
) {

	/**
	 * Auto detect language
	 */
	public static final TranslationLanguage AUTO = new TranslationLanguage("auto", "Auto");
	public static final TranslationLanguage EN = new TranslationLanguage("en", "English");

	@Contract("_ -> new")
	public static @NotNull TranslationLanguage from(@NotNull Locale locale) {
		if (!isAvailable(locale)) {
			return EN;
		}
		return new TranslationLanguage(locale.getLanguage(), locale.getDisplayLanguage(locale));
	}

	private static boolean isAvailable(@NotNull Locale locale) {
		return Locale.availableLocales().anyMatch(locale1 -> locale1.equals(locale));
	}

}
