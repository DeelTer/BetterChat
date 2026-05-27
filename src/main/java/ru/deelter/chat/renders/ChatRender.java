package ru.deelter.chat.renders;

import io.papermc.paper.chat.ChatRenderer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.language.LanguageManager;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.utils.MiniPlaceholdersHook;
import ru.deelter.chat.language.LangTag;

import java.util.Locale;

@RequiredArgsConstructor
public class ChatRender implements ChatRenderer {

	private final ChatData data;

	@Override
	public @NotNull Component render(@NotNull Player player, @NotNull Component displayName,
	                                 @NotNull Component message, @NotNull Audience audience) {
		Component text = data.getText();

		if (BetterChat.getInstance().getLang().isTranslationEnabled() && audience instanceof Player receiver) {
			LanguageManager languageManager = BetterChat.getInstance().getLanguageManager();
			Locale senderLocale = data.getLocale();
			text = languageManager.processTranslation(text, senderLocale, receiver);
		}

		return MiniMessage.miniMessage().deserialize(
				data.getFormat(),
				player,
				LangTag.resolver(),
				Placeholder.component("prefix", data.getPrefix()),
				Placeholder.component("suffix", data.getSuffix()),
				Placeholder.component("sender", data.getName()),
				Placeholder.component("message", text),
				Placeholder.styling("color1", data.getColor()),
				Placeholder.styling("color2", data.getColor2()),
				MiniPlaceholdersHook.resolver()
		);
	}
}