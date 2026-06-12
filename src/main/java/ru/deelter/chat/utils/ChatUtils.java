package ru.deelter.chat.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.language.LangTag;
import ru.deelter.chat.language.LanguageManager;

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

		LanguageManager languageManager = BetterChat.getInstance().getLanguageManager();
		return languageManager.processTranslation(message, originalLocale, receiver);
	}

	public static void sendGlobal(@NotNull ChatData data, String routing) {
		if (!BetterChat.isVelocityEnabled()) return;
		if (!(data.getEntity() instanceof Player player)) return;

		Component sentinel = Component.text(GlobalChatPayload.SENTINEL);
		Component frame = MiniMessage.miniMessage().deserialize(
				data.getFormat() != null ? data.getFormat() : "<message>",
				player,
				Placeholder.component("prefix", data.getPrefix() != null ? data.getPrefix() : Component.empty()),
				Placeholder.component("suffix", data.getSuffix() != null ? data.getSuffix() : Component.empty()),
				Placeholder.component("sender", data.getName() != null ? data.getName() : Component.empty()),
				Placeholder.component("message", sentinel),
				Placeholder.styling("color1", data.getColor() != null ? data.getColor() : net.kyori.adventure.text.format.TextColor.color(0xffffff)),
				Placeholder.styling("color2", data.getColor2() != null ? data.getColor2() : net.kyori.adventure.text.format.TextColor.color(0xffffff)),
				MiniPlaceholdersHook.audienceResolver(),
				LangTag.resolver()
		);

		GlobalChatPayload chatPayload = new GlobalChatPayload(
				data.getLocale() != null ? data.getLocale().toLanguageTag() : "en",
				frame,
				data.getText() != null ? data.getText() : Component.empty()
		);

		String payloadStr = routing + "|" + chatPayload.toJson();
		byte[] bytes = payloadStr.getBytes(StandardCharsets.UTF_8);

		if (bytes.length > Messenger.MAX_MESSAGE_SIZE) {
			BetterChat.getInstance().getLogger().warning("Global message too large, dropping: " + bytes.length);
			return;
		}
		player.sendPluginMessage(BetterChat.getInstance(), VELOCITY_MESSAGE_CHANNEL_ID, bytes);
	}
}
