package ru.deelter.chat.processors.impl;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.HashSet;
import java.util.Set;

public class EntitySpectateChatProcessor extends AbstractChatProcessor {

	public EntitySpectateChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;
		if (data.isSpectating()) {
			data.setColors(ChatConfig.colorSpectate, ChatConfig.colorSpectate2);
		}

		Entity entity = player.getSpectatorTarget();
		if (entity == null) {
			return;
		}
		Set<Audience> viewers = data.getAudiences();
		if (!(entity instanceof Player target)) {
 			/*
 				Человек может вселиться в овцу и разговаривать от её имени
 			 */
			ChatData data2 = ChatData.fromEntity(entity);
			data2.setLocale(player.locale());
			data2.setFormat(ChatConfig.formatSpectatorEntity);
			data2.setText(data.getText());
			data2.setRadius(data.getRadius());
			data2.setAudiences(new HashSet<>(data.getAudiences()));

			viewers.clear();

			data2.process();
			data2.send();
			data2.sendBubbles();
			return;
		}
		viewers.clear();
		viewers.add(Audience.audience(player, target));

		data.setFormat(ChatConfig.formatSpectatorInside);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.isSpectating();
	}
}