package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.replacer.ChatLink;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class URLLabelReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern URL_LABEL_PATTERN = Pattern.compile("\\[(u|link|url|site)\\:(.+?)\\:(.+?)\\]");

	public URLLabelReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(URL_LABEL_PATTERN)
				.replacement((result, builder) -> {
					String label = result.group(2);
					String url = result.group(3);
					ChatLink link = ChatLink.getLinkByUrl(url);
					Component hoverOpen = BetterChat.getInstance().getLang()
							.getMessage("links-open", null);
					if (hoverOpen == null) hoverOpen = Component.text("Click to open link");
					return builder.content(label)
							.hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.noSeparators(),
									Component.text(url),
									Component.newline(),
									Component.newline(),
									hoverOpen)))
							.clickEvent(ClickEvent.openUrl(url.startsWith("http") ? url : "https://" + url))
							.color(link.color());
				}).build();
		data.setText(data.getText().replaceText(replacer));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}