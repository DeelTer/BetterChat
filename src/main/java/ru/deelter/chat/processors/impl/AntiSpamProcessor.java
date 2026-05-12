package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.antispam.SpamGuard;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.AntiSpamConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;

public class AntiSpamProcessor extends AbstractChatProcessor {

	public AntiSpamProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;
		if (player.hasPermission("betterchat.antispam.bypass")) return;
		if (data.getAudiences().isEmpty()) return; // тишина – не наказываем

		String text = PlainTextComponentSerializer.plainText().serialize(data.getText());

		if (SpamGuard.isSimilar(player, text,
				AntiSpamConfig.getSimilarityThreshold(),
				AntiSpamConfig.getRecentMessagesCount())) {

			Component blockMsg = BetterChat.getInstance().getLang().getMessage("anti-spam.blocked", player);
			if (blockMsg != null) {
				player.sendActionBar(blockMsg);
			}

			data.getAudiences().clear();
			setTerminateChain(true); // прерываем цепочку
		}
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return AntiSpamConfig.isEnabled();
	}
}