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
		TextColor color = parseColor(config.getString("replacers.hunger.color", "#FF851B"), 0xFF851B);
		Component fullComp = MiniMessage.miniMessage().deserialize(config.getString("replacers.hunger.full-char", "<color:#FF851B>🍖"));
		Component emptyComp = MiniMessage.miniMessage().deserialize(config.getString("replacers.hunger.empty-char", "<color:#525252>🍖"));
		int barSize = config.getInt("replacers.hunger.bar-size", 10);

		Component bar = buildBar(food, 20, barSize, fullComp, emptyComp);

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(HUNGER_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("hunger"))
						.append(Component.text(" " + food + " / 20", color))
						.hoverEvent(HoverEvent.showText(bar)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private Component buildBar(int current, int max, int size, Component full, Component empty) {
		int filled = (int) Math.round((double) current / max * size);
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
