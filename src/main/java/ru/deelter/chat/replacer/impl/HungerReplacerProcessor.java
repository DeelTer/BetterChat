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

public class HungerReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern HUNGER_PATTERN = Pattern.compile("\\[(hunger|food)\\]");

	public HungerReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		int food = player.getFoodLevel();

		FileConfiguration config = BetterChat.getInstance().getConfig();
		String colorHex = config.getString("replacers.hunger.color", "#FFAA00");
		String fullChar = config.getString("replacers.hunger.full-char", "🍗");
		String emptyChar = config.getString("replacers.hunger.empty-char", "🍖");
		int barSize = config.getInt("replacers.hunger.bar-size", 10);

		TextColor color = TextColor.fromHexString(colorHex);
		if (color == null) color = TextColor.color(0xFFAA00);

		TextColor finalColor = color;
		Component bar = buildBar(food, 20, barSize, fullChar, emptyChar, finalColor);

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(HUNGER_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("hunger"))
						.append(Component.text(" " + food + " / 20", finalColor))
						.hoverEvent(HoverEvent.showText(bar)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private Component buildBar(int current, int max, int size, String full, String empty, TextColor color) {
		int filled = (int) Math.round((double) current / max * size);
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
