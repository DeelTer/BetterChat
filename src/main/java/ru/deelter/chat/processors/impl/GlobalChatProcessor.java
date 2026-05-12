package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.tags.ChatTag;
import ru.deelter.chat.tags.ChatTagRegistry;
import ru.deelter.chat.utils.ChatData;

import java.nio.charset.StandardCharsets;

public class GlobalChatProcessor extends AbstractChatProcessor {

	public GlobalChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		String text = PlainTextComponentSerializer.plainText().serialize(data.getText());
		ChatTag tag = ChatTagRegistry.getSuitable(text);
		if (tag == null || !tag.isGlobal()) return;

		// Формируем payload
		String routing = tag.getGlobalMode() + ":" + String.join(",", tag.getGlobalServers());
		String payload = routing + "|" + data.getLocale().toLanguageTag() + "|" + data.getFormat() + "|" + text;
		byte[] message = payload.getBytes(StandardCharsets.UTF_8);

		player.sendPluginMessage(BetterChat.getInstance(), "betterchat:global", message);

		data.getAudiences().clear();
		setTerminateChain(true);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		if (!BetterChat.isVelocityEnabled()) return false;
		if (!(data.getEntity() instanceof Player)) return false;

		String text = PlainTextComponentSerializer.plainText().serialize(data.getText());
		ChatTag tag = ChatTagRegistry.getSuitable(text);
		return tag != null && tag.isGlobal();
	}
}