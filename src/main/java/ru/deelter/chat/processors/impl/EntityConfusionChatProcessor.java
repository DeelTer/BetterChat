package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.model.ChatData;
import ru.deelter.chat.utils.ChatUtils;

public class EntityConfusionChatProcessor extends AbstractChatProcessor {

	public EntityConfusionChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		Component formatted = ChatUtils.applyConfusedFormat(data.getText());
		data.setText(formatted);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof LivingEntity entity)) return false;
		return entity.hasPotionEffect(PotionEffectType.NAUSEA);
	}
}