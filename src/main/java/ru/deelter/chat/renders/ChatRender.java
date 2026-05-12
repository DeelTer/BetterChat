package ru.deelter.chat.renders;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.BetterChat;
import ru.deelter.chat.model.ChatData;
import ru.deelter.chat.utils.LangTag;
import ru.deelter.chat.utils.PlayerLanguageUtil;
import ru.deelter.chat.utils.translator.OnlineTranslator;
import ru.deelter.chat.utils.translator.TranslationLanguage;

import java.util.Locale;

public class ChatRender implements ChatRenderer {

	private final ChatData data;

	public ChatRender(ChatData data) {
		this.data = data;
	}

	@Override
	public @NotNull Component render(@NotNull Player player, @NotNull Component displayName,
	                                 @NotNull Component message, @NotNull Audience audience) {
		Component text = data.getText();

		if (BetterChat.getInstance().getLang().isTranslationEnabled() && audience instanceof Player receiver) {
			Locale receiverLocale = PlayerLanguageUtil.getLocale(receiver);
			if (!data.getLocale().equals(receiverLocale)) {
				String translated = OnlineTranslator.translate(
						PlainTextComponentSerializer.plainText().serialize(text),
						TranslationLanguage.AUTO,
						TranslationLanguage.from(receiverLocale));
				text = Component.text(translated).hoverEvent(HoverEvent.showText(text));
			}
		}
		return MiniMessage.miniMessage().deserialize(
				data.getFormat(),
				LangTag.resolver(),
				Placeholder.component("prefix", data.getPrefix()),
				Placeholder.component("suffix", data.getSuffix()),
				Placeholder.component("sender", data.getName()),
				Placeholder.component("message", text),
				Placeholder.styling("color1", data.getColor()),
				Placeholder.styling("color2", data.getColor2())
		);
	}
}