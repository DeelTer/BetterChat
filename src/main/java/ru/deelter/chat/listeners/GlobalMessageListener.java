package ru.deelter.chat.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.utils.ChatData;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class GlobalMessageListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
		if (!channel.equals("betterchat:global")) return;

		String payload = new String(message, StandardCharsets.UTF_8);
		String[] parts = payload.split("\\|", 3);
		if (parts.length < 3) return;

		Locale locale = Locale.forLanguageTag(parts[0]);
		String format = parts[1];
		String rawText = parts[2];

		Component text = MiniMessage.miniMessage().deserialize(rawText);
		ChatData data = ChatData.builder()
				.locale(locale)
				.format(format)
				.text(text)
				.name(Component.text("[GLOBAL]"))
				.color(ChatConfig.colorDefault)
				.color2(ChatConfig.colorDefault2)
				.build();
		data.send(false);
	}
}