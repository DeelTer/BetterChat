package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.BetterChat;
import ru.deelter.chat.model.ChatData;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.Lang;

public class EmptyAudienceProcessor extends AbstractChatProcessor {

	public EmptyAudienceProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (data.getAudiences().isEmpty() && data.getEntity() instanceof Player player) {
			Lang lang = BetterChat.getInstance().getLang();
			Component message = lang.getMessage("empty-audience", player);
			if (message != null) {
				player.sendActionBar(message);
			}
		}
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}