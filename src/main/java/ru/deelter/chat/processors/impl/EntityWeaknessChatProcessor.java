package ru.deelter.chat.processors.impl;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.model.ChatData;

public class EntityWeaknessChatProcessor extends AbstractChatProcessor {

	public EntityWeaknessChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		data.setRadius(data.getRadius() / 2);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof LivingEntity entity)) return false;
		return entity.hasPotionEffect(PotionEffectType.WEAKNESS)
				&& !entity.hasPotionEffect(PotionEffectType.STRENGTH);
	}
}