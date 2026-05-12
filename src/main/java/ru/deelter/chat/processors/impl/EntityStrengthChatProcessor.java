package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.model.ChatData;

public class EntityStrengthChatProcessor extends AbstractChatProcessor {

	public EntityStrengthChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		String formatted = PlainTextComponentSerializer.plainText().serialize(data.getText()).toUpperCase();
		data.setText(Component.text(formatted));
		data.setRadius(data.getRadius() * 2);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof LivingEntity entity)) return false;
		return entity.hasPotionEffect(PotionEffectType.STRENGTH);
	}
}