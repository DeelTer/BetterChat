package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class LevelReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern LEVEL_PATTERN = Pattern.compile("\\[(level|exp|xp)\\]");

	public LevelReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		int level = player.getLevel();
		float progress = player.getExp();

		FileConfiguration config = BetterChat.getInstance().getConfig();
		String colorHex = config.getString("replacers.level.color", "#55FF55");
		String fullChar = config.getString("replacers.level.full-char", "█");
		String emptyChar = config.getString("replacers.level.empty-char", "░");
		int barSize = config.getInt("replacers.level.bar-size", 10);

		TextColor color = TextColor.fromHexString(colorHex);
		if (color == null) color = TextColor.color(0x55FF55);

		TextColor finalColor = color;
		Component bar = buildBar(progress, barSize, fullChar, emptyChar, finalColor);
		Component hover = Component.text(String.format("%.0f%%", progress * 100), finalColor)
				.append(Component.newline())
				.append(bar);

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(LEVEL_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("level"))
						.append(Component.text(" " + level, finalColor))
						.hoverEvent(HoverEvent.showText(hover)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private Component buildBar(float progress, int size, String full, String empty, TextColor color) {
		int filled = Math.round(progress * size);
		Component bar = Component.empty();
		TextColor emptyColor = TextColor.color(color.red() / 3, color.green() / 3, color.blue() / 3);
		for (int i = 0; i < size; i++) {
			bar = bar.append(Component.text(i < filled ? full : empty, i < filled ? color : emptyColor));
		}
		return bar;
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getEntity() instanceof Player;
	}
}
