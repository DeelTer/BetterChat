package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.tags.ChatTag;
import ru.deelter.chat.tags.ChatTagRegistry;
import ru.deelter.chat.utils.ChatData;

public class TagChatProcessor extends AbstractChatProcessor {

	public TagChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		Component message = data.getText();
		String text = PlainTextComponentSerializer.plainText().serialize(message);
		ChatTag tag = ChatTagRegistry.getSuitable(text);
		if (tag == null) return;

		data.setMatchedTag(tag);
		if (tag.getFormat() != null) {
			data.setFormat(tag.getFormat());
		}
		data.setText(data.getText().replaceText(tag.getReplacementConfig()));
		data.setRadius(tag.getRadius());
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}