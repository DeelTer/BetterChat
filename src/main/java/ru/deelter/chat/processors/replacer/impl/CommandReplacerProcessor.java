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

public class CommandReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern COMMAND_PATTERN = Pattern.compile("\\[(cd|cmd|command):(?:([^:\\]]+):)?([^\\]]+)\\]");

	public CommandReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(COMMAND_PATTERN)
				.replacement((result, builder) -> {
					String label = result.group(2);
					String command = result.group(3);
					if (label == null) label = command;
					Component icon = IconProvider.getIcon("command");
					Lang lang = BetterChat.getInstance().getLang();
					String hoverCmd = lang.getMessage("chat.command", null) != null ?
							lang.getMessage("chat.command", null).toString() : "Click to run command";
					return Component.join(JoinConfiguration.builder().separator(Component.text(" ")),
							icon,
							builder.content(label)
									.hoverEvent(HoverEvent.showText(
											Component.join(JoinConfiguration.separator(Component.text(" ")),
													icon,
													Component.text(hoverCmd),
													Component.text("/" + command))))
									.clickEvent(ClickEvent.runCommand("/" + command))
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