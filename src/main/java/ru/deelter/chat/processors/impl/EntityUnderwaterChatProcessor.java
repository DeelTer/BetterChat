package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.BetterChat;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.model.ChatData;

public class EntityUnderwaterChatProcessor extends AbstractChatProcessor {

	public EntityUnderwaterChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {

		Component message = BetterChat.getInstance().getLang().getMessage("underwater-chat", null);
		if (message != null) {
			data.setText(message);
		}
		data.setColors(ChatConfig.colorUnderwater, ChatConfig.colorUnderwater2);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		Entity entity = data.getEntity();
		if (entity == null) return false;
		if (!entity.isUnderWater()) return false;
		if (!(entity instanceof LivingEntity livingEntity)) return true;
		if (livingEntity.getRemainingAir() > 0) return false;
		return !livingEntity.hasPotionEffect(PotionEffectType.WATER_BREATHING);
	}
}