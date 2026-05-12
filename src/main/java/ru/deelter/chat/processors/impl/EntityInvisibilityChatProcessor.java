package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;

public class EntityInvisibilityChatProcessor extends AbstractChatProcessor {

	public EntityInvisibilityChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		data.setColors(ChatConfig.colorInvisible, ChatConfig.colorInvisible2);
		data.setName(data.getName().style(Style.style().decorate(TextDecoration.OBFUSCATED).build()));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.isInvisible();
	}
}