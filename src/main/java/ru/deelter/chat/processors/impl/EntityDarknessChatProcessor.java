package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;

public class EntityDarknessChatProcessor extends AbstractChatProcessor {

	public EntityDarknessChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		data.setColors(ChatConfig.colorDarkness, ChatConfig.colorDarkness2);
		data.setName(data.getName().style(Style.style().decorate(TextDecoration.OBFUSCATED).build()));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		if (!data.isDarkness()) return false;
		return data.getLocation().getWorld().getEnvironment() != World.Environment.NETHER;
	}
}