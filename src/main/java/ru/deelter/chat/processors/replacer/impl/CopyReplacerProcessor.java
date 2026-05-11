package ru.deelter.chat.processors.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.utils.Lang;

import java.util.regex.Pattern;

public class CopyReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern COPY_PATTERN = Pattern.compile("\\[(cp|copy|c)\\:(.+?)\\]");

	public CopyReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(COPY_PATTERN)
				.replacement((result, builder) -> {
					String textToCopy = result.group(2);
					Component icon = IconProvider.getIcon("copy");
					Lang lang = BetterChat.getInstance().getLang();
					String hoverCopy = lang.getMessage("chat.copy", null) != null ?
							lang.getMessage("chat.copy", null).toString() : "Click to copy";
					return Component.empty()
							.append(icon)
							.append(Component.text(" "))
							.append(Component.text(textToCopy))
							.hoverEvent(HoverEvent.showText(Component.text(hoverCopy)))
							.clickEvent(ClickEvent.copyToClipboard(textToCopy));
				}).build();
		data.setText(data.getText().replaceText(replacer));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}