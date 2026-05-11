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

public class HideReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern HIDE_PATTERN = Pattern.compile("\\[(hd|hide|h)\\:(.+?)\\]");

	public HideReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(HIDE_PATTERN)
				.replacement((result, builder) -> {
					String originalText = result.group(2);
					Component icon = IconProvider.getIcon("hide");
					Lang lang = BetterChat.getInstance().getLang();
					String hoverHide = lang.getMessage("chat.hide", null) != null ?
							lang.getMessage("chat.hide", null).toString() : "Click to reveal";
					return Component.join(JoinConfiguration.builder().separator(Component.text(" ")),
							icon,
							builder.content("[...]")
									.hoverEvent(HoverEvent.showText(Component.text(originalText)))
									.clickEvent(ClickEvent.copyToClipboard(originalText))
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