package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class HealthReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern HEALTH_PATTERN = Pattern.compile("\\[health\\]");

	public HealthReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		double health = player.getHealth();
		AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
		double maxHealth = attr != null ? attr.getValue() : 20.0;

		FileConfiguration config = BetterChat.getInstance().getConfig();
		TextColor color = parseColor(config.getString("replacers.health.color", "#EA4B3C"), 0xEA4B3C);
		Component fullComp = MiniMessage.miniMessage().deserialize(config.getString("replacers.health.full-char", "<color:#FF5555>❤"));
		Component emptyComp = MiniMessage.miniMessage().deserialize(config.getString("replacers.health.empty-char", "<color:#552222>❤"));
		int barSize = config.getInt("replacers.health.bar-size", 10);

		String text = String.format("%.1f / %.1f", health, maxHealth);
		Component bar = buildBar(health, maxHealth, barSize, fullComp, emptyComp);

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(HEALTH_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("health"))
						.append(Component.text(" " + text, color))
						.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(bar)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private Component buildBar(double current, double max, int size, Component full, Component empty) {
		int filled = (int) Math.round(current / max * size);
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
