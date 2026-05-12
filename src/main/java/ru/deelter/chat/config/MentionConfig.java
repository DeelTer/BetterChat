package ru.deelter.chat.config;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public final class MentionConfig {
	@Getter
	private static boolean enabled;
	@Getter
	private static TextColor color;
	@Getter
	private static Sound sound;
	@Getter
	private static String soundName;

	private MentionConfig() {
	}

	public static void init(@NonNull FileConfiguration config) {
		enabled = config.getBoolean("mentions.enabled", true);

		String hex = config.getString("mentions.color", "#FFD700");
		if (!hex.startsWith("#")) hex = "#" + hex;
		color = Objects.requireNonNull(TextColor.fromHexString(hex), "Invalid mentions.color");

		soundName = config.getString("mentions.sound", "block.note_block.bell");
		sound = Sound.sound(Key.key(soundName), Sound.Source.PLAYER, 1.0f, 1.0f);
	}

}