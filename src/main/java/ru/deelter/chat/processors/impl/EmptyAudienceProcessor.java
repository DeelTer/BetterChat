package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.language.Lang;

public class EmptyAudienceProcessor extends AbstractChatProcessor {

	public EmptyAudienceProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		Player player = (Player) data.getEntity();
		Lang lang = BetterChat.getInstance().getLang();
		Component message = lang.getMessage("empty-audience", player);
		if (message != null) {
			player.sendActionBar(message);
		}
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getAudiences().isEmpty() && data.getEntity() instanceof Player;
	}
}