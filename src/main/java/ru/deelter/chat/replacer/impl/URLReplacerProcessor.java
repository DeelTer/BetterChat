package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.replacer.ChatLink;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class URLReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern URL_PATTERN = Pattern.compile(
			"(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?([a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5})(:[0-9]{1,5})?(\\/.*?(?=$|\\s|[,.!?…]+($|\\s)))?"
	);

	public URLReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(URL_PATTERN)
				.replacement((result, builder) -> {
					String url = result.group();
					String shortenUrl = result.group(2);
					ChatLink link = ChatLink.getLinkByUrl(shortenUrl);
					Component icon = IconProvider.getIcon("link");
					Component hoverOpen = BetterChat.getInstance().getLang()
							.getMessage("links-open", null);
					if (hoverOpen == null) hoverOpen = Component.text("Click to open link");
					return icon.append(Component.space())
							.append(builder.content(shortenUrl)
									.hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.noSeparators(),
											Component.text(url),
											Component.newline(),
											Component.newline(),
											hoverOpen)))
									.clickEvent(ClickEvent.openUrl(url.startsWith("http") ? url : "https://" + url))
									.color(link.color())
									.build());
				}).build();
		data.setText(data.getText().replaceText(replacer));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}