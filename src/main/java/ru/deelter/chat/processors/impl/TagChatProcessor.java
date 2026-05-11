package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.utils.tags.ChatTag;
import ru.deelter.chat.utils.tags.ChatTagRegistry;

public class TagChatProcessor extends AbstractChatProcessor {

	public TagChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		Component message = data.getText();
		String text = PlainTextComponentSerializer.plainText().serialize(message);
		ChatTag chatTag = ChatTagRegistry.getSuitable(text);
		if (chatTag == null) return;

		if (chatTag.getFormat() != null) {
			data.setFormat(chatTag.getFormat());
		}
		data.setText(data.getText().replaceText(chatTag.getReplacementConfig()));
		data.setRadius(chatTag.getRadius());
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}