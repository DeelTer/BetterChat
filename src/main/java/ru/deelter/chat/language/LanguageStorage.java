package ru.deelter.chat.language;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface LanguageStorage {

	void init();

	void shutdown();

	@Nullable LanguagePreference load(@NotNull UUID playerId);

	void save(@NotNull LanguagePreference pref);

	void delete(@NotNull UUID playerId);
}