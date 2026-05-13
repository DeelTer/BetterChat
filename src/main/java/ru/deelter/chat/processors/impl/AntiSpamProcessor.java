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
		Player player = (Player) data.getEntity();
		String text = PlainTextComponentSerializer.plainText().serialize(data.getText());

		if (SpamGuard.isSimilar(player, text,
				AntiSpamConfig.getSimilarityThreshold(),
				AntiSpamConfig.getRecentMessagesCount())) {

			Component spamBlockedMessage = BetterChat.getInstance().getLang()
					.getMessage("anti-spam.blocked", player);
			if (spamBlockedMessage != null) {
				player.sendActionBar(spamBlockedMessage);
			}
			data.getAudiences().clear();
			data.setTerminated(true);
		}
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		if (!AntiSpamConfig.isEnabled()) return false;
		if (data.getAudiences().isEmpty()) return false;
		if (!(data.getEntity() instanceof Player player)) return false;
		return !player.hasPermission("betterchat.antispam.bypass");
	}
}