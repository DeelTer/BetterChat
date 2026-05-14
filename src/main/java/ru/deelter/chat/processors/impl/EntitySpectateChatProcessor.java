package ru.deelter.chat.processors.impl;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.HashSet;
import java.util.Set;

public class EntitySpectateChatProcessor extends AbstractChatProcessor {

	private static final String IMPERSONATE_PREFIX = "!";
	private static final String IMPERSONATE_PERMISSION = "betterchat.spectate.impersonate";

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

		if (entity instanceof Player target) {
			Component text = data.getText();
			String plain = PlainTextComponentSerializer.plainText().serialize(text);

			/*
				Если сообщение начинается с "!" и есть право — пишем от лица того,
				в кого вселились (impersonation). Оригинальное сообщение подавляется.
			 */
			if (plain.startsWith(IMPERSONATE_PREFIX) && player.hasPermission(IMPERSONATE_PERMISSION)) {
				Component strippedText = text.replaceText(TextReplacementConfig.builder()
						.matchLiteral(IMPERSONATE_PREFIX)
						.once()
						.replacement("")
						.build());

				ChatData impersonated = ChatData.fromLivingEntity(target);
				impersonated.setText(strippedText);
				impersonated.setRadius(data.getRadius());
				impersonated.setAudiences(new HashSet<>(data.getAudiences()));
				impersonated.setSpectating(false);

				viewers.clear();
				data.setTerminated(true);

				impersonated.process();
				impersonated.send();
				impersonated.sendBubbles();
				return;
			}

			/*
				Обычное сообщение: разговор 1 на 1 с тем, в кого вселился
				(сообщение видно только ему и тому кто в него вселился).
			 */
			viewers.clear();
			viewers.add(Audience.audience(player, target));
			data.setFormat(ChatConfig.formatSpectatorInside);
			return;
		}

		/*
 			Человек может вселиться в овцу и разговаривать от её имени.
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
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.isSpectating();
	}
}
