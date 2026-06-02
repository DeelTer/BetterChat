package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
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
		String colorHex = config.getString("replacers.health.color", "#FF5555");
		String fullChar = config.getString("replacers.health.full-char", "❤");
		String emptyChar = config.getString("replacers.health.empty-char", "🖤");
		int barSize = config.getInt("replacers.health.bar-size", 10);

		TextColor color = TextColor.fromHexString(colorHex);
		if (color == null) color = TextColor.color(0xFF5555);

		TextColor finalColor = color;
		String text = String.format("%.1f / %.1f", health, maxHealth);
		Component bar = buildBar(health, maxHealth, barSize, fullChar, emptyChar, finalColor);

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(HEALTH_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("health"))
						.append(Component.text(" " + text, finalColor))
						.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(bar)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private Component buildBar(double current, double max, int size, String full, String empty, TextColor color) {
		int filled = (int) Math.round(current / max * size);
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
