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
import ru.deelter.chat.replacer.UrlMatcher;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class URLReplacerProcessor extends AbstractReplacerProcessor {

	/** @deprecated kept for API compatibility, use {@link UrlMatcher#URL_PATTERN}. */
	@Deprecated
	public static final Pattern URL_PATTERN = UrlMatcher.URL_PATTERN;

	public URLReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(UrlMatcher.URL_PATTERN)
				.replacement((result, builder) -> {
					String scheme = result.group(1);
					String host = result.group(2);
					String fullMatch = result.group();

					// Bare domain without scheme: only link it if the TLD is a known one.
					// Returning the pre-filled builder leaves the text untouched (no link).
					if (scheme == null && !UrlMatcher.hasKnownTld(host)) {
						return builder;
					}

					// Strip trailing sentence punctuation so it stays as plain text after the link.
					int trailing = UrlMatcher.trailingPunctCount(fullMatch);
					String url = trailing > 0 ? fullMatch.substring(0, fullMatch.length() - trailing) : fullMatch;
					String trailingText = trailing > 0 ? fullMatch.substring(fullMatch.length() - trailing) : "";

					ChatLink link = ChatLink.getLinkByUrl(host);
					Component icon = IconProvider.getIcon("link");
					Component hoverOpen = BetterChat.getInstance().getLang().getMessage("links-open", null);
					if (hoverOpen == null) hoverOpen = Component.text("Click to open link");

					Component linkComponent = builder.content(host)
							.hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.noSeparators(),
									Component.text(url),
									Component.newline(),
									Component.newline(),
									hoverOpen)))
							.clickEvent(ClickEvent.openUrl(UrlMatcher.toHref(scheme, url)))
							.color(link.color())
							.build();

					Component output = icon.append(Component.space()).append(linkComponent);
					if (!trailingText.isEmpty()) {
						output = output.append(Component.text(trailingText));
					}
					return output;
				}).build();
		data.setText(data.getText().replaceText(replacer));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return true;
	}
}
