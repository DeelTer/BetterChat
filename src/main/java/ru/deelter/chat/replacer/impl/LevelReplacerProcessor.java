package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
		TextColor color = parseColor(config.getString("replacers.level.color", "#55FF55"), 0x55FF55);
		Component fullComp = MiniMessage.miniMessage().deserialize(config.getString("replacers.level.full-char", "<color:#55FF55>█"));
		Component emptyComp = MiniMessage.miniMessage().deserialize(config.getString("replacers.level.empty-char", "<color:#1A4D1A>█"));
		int barSize = config.getInt("replacers.level.bar-size", 10);

		Component bar = buildBar(progress, barSize, fullComp, emptyComp);
		Component hover = Component.text(String.format("%.0f%%", progress * 100), color)
				.append(Component.newline())
				.append(bar);

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(LEVEL_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("level"))
						.append(Component.text(" " + level, color))
						.hoverEvent(HoverEvent.showText(hover)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private Component buildBar(float progress, int size, Component full, Component empty) {
		int filled = Math.round(progress * size);
		Component bar = Component.empty();
		for (int i = 0; i < size; i++) {
			bar = bar.append(i < filled ? full : empty);
		}
		return bar;
	}

	private TextColor parseColor(String hex, int fallback) {
		TextColor c = TextColor.fromHexString(hex);
		return c != null ? c : TextColor.color(fallback);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getEntity() instanceof Player;
	}
}
