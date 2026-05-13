package ru.deelter.chat.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.utils.ChatUtils;
import ru.deelter.chat.utils.PlayerLanguageUtil;
import ru.deelter.chat.utils.translator.OnlineTranslator;
import ru.deelter.chat.utils.translator.TranslationLanguage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GlobalMessageListener implements PluginMessageListener {

	public static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player receiver, byte @NonNull [] message) {
		if (!channel.equals(ChatUtils.VELOCITY_MESSAGE_CHANNEL_ID)) return;

		String payload = new String(message, StandardCharsets.UTF_8);
		String[] parts = payload.split("\\|", 4);

		if (parts.length < 4) return;

		String localeTag = parts[1];
		String originalJson = parts[2];
		String renderedJson = parts[3];

		try {
			Locale senderLocale = Locale.forLanguageTag(localeTag);
			Component originalText = GSON.deserialize(originalJson);
			Component renderedMessage = GSON.deserialize(renderedJson);

			// Snapshot on main thread — player list can't be read safely from async
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

			Bukkit.getScheduler().runTaskAsynchronously(BetterChat.getInstance(), () -> {
				for (Player player : players) {
					if (!player.isOnline()) continue;

					Component textToSend = renderedMessage;
					Locale locale = PlayerLanguageUtil.getLocale(player);

					if (BetterChat.getInstance().getLang().isTranslationEnabled() && !senderLocale.equals(locale)) {
						String translated = OnlineTranslator.translate(
								PlainTextComponentSerializer.plainText().serialize(originalText),
								TranslationLanguage.AUTO,
								TranslationLanguage.from(locale)
						);
						textToSend = Component.text(translated)
								.hoverEvent(HoverEvent.showText(renderedMessage));
					}

					player.sendMessage(textToSend);
				}
			});

		} catch (Exception e) {
			System.err.println("[BetterChat] Failed to process global message");
			e.printStackTrace();
		}
	}
}