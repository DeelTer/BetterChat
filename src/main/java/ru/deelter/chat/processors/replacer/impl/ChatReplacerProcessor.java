package ru.deelter.chat.processors.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.utils.Lang;

import java.util.regex.Pattern;

public class ChatReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern PATTERN = Pattern.compile("\\[(chat|say|send):(?:([^:\\]]+):)?([^\\]]+)\\]");

	public ChatReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(PATTERN)
				.replacement((result, builder) -> {
					String label = result.group(2);
					String cmdText = result.group(3);
					if (label == null) label = cmdText;
					Component icon = IconProvider.getIcon("command");
					Lang lang = BetterChat.getInstance().getLang();

					String hoverSay = lang.getMessage("chat.say", null) != null ?
							lang.getMessage("chat.say", null).toString() : "Click to say";
					return Component.join(JoinConfiguration.builder().separator(Component.text(" ")),
							icon,
							builder.content(label)
									.hoverEvent(HoverEvent.showText(
											Component.join(JoinConfiguration.separator(Component.text(" ")),
													icon,
													Component.text(hoverSay),
													Component.text(cmdText))))
									.clickEvent(ClickEvent.runCommand("/chat " + cmdText))
									.build()
					);
				}).build();
		data.setText(data.getText().replaceText(replacer));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}