package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.language.Lang;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class CoordsReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern COORDS_PATTERN = Pattern.compile("\\[(coords|pos)\\]");

	public CoordsReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		Location loc = player.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();

		FileConfiguration config = BetterChat.getInstance().getConfig();
		TextColor color = parseColor(config.getString("replacers.coords.color", "#C5823E"), 0xC5823E);

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(COORDS_PATTERN)
				.replacement((result, builder) -> {
					Component icon = IconProvider.getIcon("coords");
					Lang lang = BetterChat.getInstance().getLang();
					Component hoverText = lang.getMessage("chat-coords", null);
					if (hoverText == null) hoverText = Component.text("Click to teleport");

					return Component.empty()
							.append(icon)
							.append(Component.text(" "))
							.append(Component.text(x + " " + y + " " + z, color))
							.hoverEvent(HoverEvent.showText(hoverText))
							.clickEvent(ClickEvent.runCommand("/tp " + x + " " + y + " " + z));
				})
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getEntity() instanceof Player;
	}

	private TextColor parseColor(String hex, int fallback) {
		TextColor c = TextColor.fromHexString(hex);
		return c != null ? c : TextColor.color(fallback);
	}
}
