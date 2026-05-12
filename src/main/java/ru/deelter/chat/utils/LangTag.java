package ru.deelter.chat.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;

public final class LangTag {

	private LangTag() {
	}

	public static @NotNull TagResolver resolver() {
		return TagResolver.resolver("lang", (argumentQueue, context) -> {
			if (!argumentQueue.hasNext()) return Tag.selfClosingInserting(Component.text("lang"));
			String key = argumentQueue.pop().value();

			// Узнаём игрока из контекста, если возможно
			if (context instanceof Audience audience && audience instanceof CommandSender commandSender) {
				Component translated = BetterChat.getInstance().getLang().getMessage(key, commandSender);
				if (translated != null) return Tag.selfClosingInserting(translated);
			}

			// Фолбэк без игрока
			Component translated = BetterChat.getInstance().getLang().getMessage(key, null);
			return Tag.selfClosingInserting(translated != null ? translated : Component.text(key));
		});
	}
}