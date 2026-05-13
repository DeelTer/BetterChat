package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.tags.ChatTag;
import ru.deelter.chat.tags.ChatTagRegistry;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.utils.ChatUtils;

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

		String mode = tag.getGlobalMode() != null ? tag.getGlobalMode() : "whitelist";
		String servers = tag.getGlobalServers() != null
				? String.join(",", tag.getGlobalServers())
				: "";

		String routing = mode + ":" + servers;

		if (servers.isEmpty() && "whitelist".equalsIgnoreCase(mode)) {
			routing = "whitelist:";
		}

		ChatUtils.sendGlobal(data, routing);
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