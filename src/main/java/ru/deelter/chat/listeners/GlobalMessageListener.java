package ru.deelter.chat.listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.utils.ChatUtils;
import ru.deelter.chat.utils.LangTag;
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
		String[] parts = payload.split("\\|", 2);
		if (parts.length < 2) return;

		try {
			JsonObject obj = JsonParser.parseString(parts[1]).getAsJsonObject();

			Locale senderLocale = Locale.forLanguageTag(obj.get("locale").getAsString());
			String format = obj.get("format").getAsString();

			TextColor color1 = TextColor.fromHexString(obj.get("color1").getAsString());
			if (color1 == null) color1 = TextColor.color(0xFFFFFF);
			TextColor color2 = TextColor.fromHexString(obj.get("color2").getAsString());
			if (color2 == null) color2 = TextColor.color(0xFFFFFF);

			Component prefix = GSON.deserializeFromTree(obj.get("prefix"));
			Component suffix = GSON.deserializeFromTree(obj.get("suffix"));
			Component sender = GSON.deserializeFromTree(obj.get("sender"));
			Component originalText = GSON.deserializeFromTree(obj.get("text"));

			// Snapshot on main thread — player list can't be read safely from async
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

			final TextColor finalColor1 = color1;
			final TextColor finalColor2 = color2;

			Bukkit.getScheduler().runTaskAsynchronously(BetterChat.getInstance(), () -> {
				for (Player player : players) {
					if (!player.isOnline()) continue;

					Locale locale = PlayerLanguageUtil.getLocale(player);
					Component messageComponent = originalText;

					if (BetterChat.getInstance().getLang().isTranslationEnabled() && !senderLocale.equals(locale)) {
						String translated = OnlineTranslator.translate(
								PlainTextComponentSerializer.plainText().serialize(originalText),
								TranslationLanguage.AUTO,
								TranslationLanguage.from(locale)
						);
						messageComponent = Component.text(translated).hoverEvent(HoverEvent.showText(originalText));
					}

					Component rendered = MiniMessage.miniMessage().deserialize(
							format,
							LangTag.resolver(),
							Placeholder.component("prefix", prefix),
							Placeholder.component("suffix", suffix),
							Placeholder.component("sender", sender),
							Placeholder.component("message", messageComponent),
							Placeholder.styling("color1", finalColor1),
							Placeholder.styling("color2", finalColor2)
					);

					player.sendMessage(rendered);
				}
			});

		} catch (Exception e) {
			System.err.println("[BetterChat] Failed to process global message");
			e.printStackTrace();
		}
	}
}
