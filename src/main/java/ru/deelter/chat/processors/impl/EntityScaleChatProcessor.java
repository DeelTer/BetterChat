package ru.deelter.chat.processors.impl;

import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.model.ChatData;

public class EntityScaleChatProcessor extends AbstractChatProcessor {

	public EntityScaleChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Attributable attributable)) return;

		AttributeInstance scaleAttribute = attributable.getAttribute(Attribute.SCALE);
		if (scaleAttribute == null) return;

		double scale = scaleAttribute.getValue();
		if (scale == scaleAttribute.getDefaultValue()) return;

		double radius = Math.max(data.getRadius() * scale, 6);
		data.setRadius(radius);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getEntity() instanceof Attributable;
	}
}