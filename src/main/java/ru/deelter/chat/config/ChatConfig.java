package ru.deelter.chat.config;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public final class ChatConfig {
	private static FileConfiguration config;

	// Форматы
	public static String formatDefault, formatCave, formatSpectatorInside,
			formatSpectatorEntity, formatSculk;

	// Цвета
	public static TextColor colorDefault, colorDefault2, colorSpectate, colorSpectate2,
			colorUnderwater, colorUnderwater2, colorInvisible, colorInvisible2,
			colorDarkness, colorDarkness2;

	public static double defaultRadius, caveRadius;

	public static void init(FileConfiguration configuration) {
		config = configuration;

		defaultRadius = config.getDouble("chat.default-radius", 50.0);
		caveRadius = config.getDouble("chat.cave-radius", 256.0);

		formatDefault = getString("chat.formats.default", "<prefix><color1><sender><suffix>:</color1> <color2><message></color2>");
		formatCave = getString("chat.formats.cave", "<color:#D0CAA5><sender>:</color> <color:#EFEEE6><message></color>");
		formatSpectatorInside = getString("chat.formats.spectator-inside", "<color:#CBCCD2>[...]:</color> <color:#EFF0F6><message></color>");
		formatSpectatorEntity = getString("chat.formats.spectator-entity", "<color:#CBCCD2><sender>:</color> <color:#EFF0F6><message></color>");
		formatSculk = getString("chat.formats.sculk", "<color:#375e5b><sender>:</color> <color:#688a87><message></color>");

		colorDefault = getColor("chat.colors.default", "#FFD700");
		colorDefault2 = getColor("chat.colors.default2", "#c7b9a6");
		colorSpectate = getColor("chat.colors.spectate", "#CBCCD2");
		colorSpectate2 = getColor("chat.colors.spectate2", "#EFF0F6");
		colorUnderwater = getColor("chat.colors.underwater", "#6BCCE4");
		colorUnderwater2 = getColor("chat.colors.underwater2", "#B4D9E2");
		colorInvisible = getColor("chat.colors.invisible", "#D0CAA5");
		colorInvisible2 = getColor("chat.colors.invisible2", "#EFEEE6");
		colorDarkness = getColor("chat.colors.darkness", "#654C7D");
		colorDarkness2 = getColor("chat.colors.darkness2", "#A795B9");
	}

	private static String getString(String path, String def) {
		return config.getString(path, def);
	}

	public static TextColor getColor(String path, String defHex) {
		String hex = config.getString(path, defHex);
		if (!hex.startsWith("#")) hex = "#" + hex;
		return Objects.requireNonNull(TextColor.fromHexString(hex), "Invalid color at " + path);
	}
}