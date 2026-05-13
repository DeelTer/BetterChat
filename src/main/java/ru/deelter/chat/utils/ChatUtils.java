package ru.deelter.chat.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.utils.translator.OnlineTranslator;
import ru.deelter.chat.utils.translator.TranslationLanguage;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@UtilityClass
public class ChatUtils {

	public static final @NotNull String VELOCITY_MESSAGE_CHANNEL_ID = "betterchat:global";

	public static @NotNull String applyConfusedFormat(@NotNull String s) {
		ExtendedRandom random = ExtendedRandom.getInstance();
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (!Character.isSpaceChar(chars[i]) && random.getDouble() < 0.35) {
				chars[i] = chars[random.getInt(0, chars.length)];
				i += random.getInt(2, 6);
			}
		}
		return String.valueOf(chars);
	}

	public static @NotNull Component applyConfusedFormat(@NotNull Component component) {
		String text = PlainTextComponentSerializer.plainText().serialize(component);
		text = applyConfusedFormat(text);
		return PlainTextComponentSerializer.plainText().deserialize(text);
	}

	public static @NotNull Component applyFormat(@NotNull ChatData data, String format) {
		return MiniMessage.miniMessage()
				.deserialize(
						format,
						Placeholder.component("sender", data.getName()),
						Placeholder.component("message", data.getText()),
						Placeholder.styling("color1", data.getColor()),
						Placeholder.styling("color2", data.getColor2())
				);
	}

	/**
	 * Переводит сообщение, если язык отправителя отличается от языка получателя.
	 * Используется как в ChatRender, так и в ChatData.send().
	 */
	public static Component translate(Locale originalLocale, Audience audience, Component message) {
		if (!BetterChat.getInstance().getLang().isTranslationEnabled()) return message;
		if (!(audience instanceof Player receiver)) return message;

		Locale receiverLocale = PlayerLanguageUtil.getLocale(receiver);
		if (originalLocale.equals(receiverLocale)) return message;

		String translated = OnlineTranslator.translate(
				PlainTextComponentSerializer.plainText().serialize(message),
				TranslationLanguage.AUTO,
				TranslationLanguage.from(receiverLocale)
		);
		return Component.text(translated).hoverEvent(HoverEvent.showText(message));
	}

	public static void sendGlobal(@NotNull ChatData data, String routing) {
		if (!BetterChat.isVelocityEnabled()) return;
		if (!(data.getEntity() instanceof Player player)) return;

		Component rendered = data.renderGlobal();

		String localeTag = data.getLocale().toLanguageTag();
		String originalTextJson = GsonComponentSerializer.gson().serialize(data.getText());
		String renderedJson = GsonComponentSerializer.gson().serialize(rendered);

		String payload = routing + "|" + localeTag + "|" + originalTextJson + "|" + renderedJson;

		byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
		if (bytes.length > 32767) {
			System.err.println("[BetterChat] Global message too large, dropping: " + bytes.length);
			return;
		}
		player.sendPluginMessage(BetterChat.getInstance(), VELOCITY_MESSAGE_CHANNEL_ID, bytes);
	}
}