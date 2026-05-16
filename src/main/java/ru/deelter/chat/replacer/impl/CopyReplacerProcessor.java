package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.language.Lang;

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
					Component hoverCopy = lang.getMessage("chat-copy", null);
					if (hoverCopy == null) hoverCopy = Component.text("Click to copy");
					return Component.empty()
							.append(icon)
							.append(Component.text(" "))
							.append(Component.text(textToCopy))
							.hoverEvent(HoverEvent.showText(hoverCopy))
							.clickEvent(ClickEvent.copyToClipboard(textToCopy));
				}).build();
		data.setText(data.getText().replaceText(replacer));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}