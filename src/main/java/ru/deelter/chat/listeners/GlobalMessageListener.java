package ru.deelter.chat.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.utils.ChatUtils;
import ru.deelter.chat.utils.GlobalChatPayload;
import ru.deelter.chat.utils.MiniPlaceholdersHook;
import ru.deelter.chat.language.LangTag;
import ru.deelter.chat.utils.translator.OnlineTranslator;
import ru.deelter.chat.utils.translator.TranslationLanguage;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class GlobalMessageListener implements PluginMessageListener {

	private static final int DEFAULT_COLOR = 16777215;

	/**
	 * Restricted MiniMessage for cross-server format parsing.
	 * Excludes &lt;click&gt; tag — prevents a malicious peer server from injecting
	 * commands that execute when receiver clicks on the message.
	 */
	private static final MiniMessage SAFE_MM = MiniMessage.builder()
			.tags(TagResolver.builder()
					.resolver(StandardTags.color())
					.resolver(StandardTags.decorations())
					.resolver(StandardTags.font())
					.resolver(StandardTags.gradient())
					.resolver(StandardTags.hoverEvent())
					.resolver(StandardTags.insertion())
					.resolver(StandardTags.keybind())
					.resolver(StandardTags.newline())
					.resolver(StandardTags.rainbow())
					.resolver(StandardTags.reset())
					.resolver(StandardTags.score())
					.resolver(StandardTags.selector())
					.resolver(StandardTags.transition())
					.resolver(StandardTags.translatable())
					.build())
			.build();

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player receiver, byte @NonNull [] message) {
		if (!channel.equals(ChatUtils.VELOCITY_MESSAGE_CHANNEL_ID)) return;

		String raw = new String(message, StandardCharsets.UTF_8);
		String[] parts = raw.split("\\|", 2);
		if (parts.length < 2) return;

		final GlobalChatPayload payload;
		try {
			payload = GlobalChatPayload.fromJson(parts[1]);
		} catch (Exception e) {
			BetterChat.getInstance().getLogger().warning("Failed to parse global message payload: " + e.getMessage());
			return;
		}

		final Locale senderLocale = Locale.forLanguageTag(payload.getLocale() != null ? payload.getLocale() : "en");
		final TextColor color1 = parseColor(payload.getColor1());
		final TextColor color2 = parseColor(payload.getColor2());
		final String format = payload.getFormat() != null ? payload.getFormat() : "<message>";
		final Component prefix = sanitize(payload.getPrefix() != null ? payload.getPrefix() : Component.empty());
		final Component suffix = sanitize(payload.getSuffix() != null ? payload.getSuffix() : Component.empty());
		final Component sender = sanitize(payload.getSender() != null ? payload.getSender() : Component.empty());
		final Component originalText = sanitize(payload.getText() != null ? payload.getText() : Component.empty());

		// Snapshot players on main thread — can't iterate online players from async safely
		final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

		Bukkit.getScheduler().runTaskAsynchronously(BetterChat.getInstance(), () -> {
			boolean translationEnabled = BetterChat.getInstance().getLang().isTranslationEnabled();
			String originalPlain = translationEnabled
					? PlainTextComponentSerializer.plainText().serialize(originalText)
					: null;

			// Cache translated message per locale — one HTTP call per unique locale, not per player
			Map<Locale, Component> messageByLocale = new HashMap<>();

			for (Player player : players) {
				if (!player.isOnline()) continue;
				Locale locale = BetterChat.getInstance().getLanguageManager().getLocale(player);

				Component messageComponent = messageByLocale.computeIfAbsent(locale, loc -> {
					if (!translationEnabled || senderLocale.equals(loc)) {
						return originalText;
					}
					String translated = OnlineTranslator.translate(
							originalPlain,
							TranslationLanguage.AUTO,
							TranslationLanguage.from(loc)
					);
					return Component.text(translated).hoverEvent(HoverEvent.showText(originalText));
				});

				Component rendered = SAFE_MM.deserialize(
						format,
						player,
						LangTag.resolver(),
						Placeholder.component("prefix", prefix),
						Placeholder.component("suffix", suffix),
						Placeholder.component("sender", sender),
						Placeholder.component("message", messageComponent),
						Placeholder.styling("color1", color1),
						Placeholder.styling("color2", color2),
						MiniPlaceholdersHook.resolver()
				);

				player.sendMessage(rendered);
			}
		});
	}

	private static @NotNull TextColor parseColor(String hex) {
		if (hex == null) return TextColor.color(DEFAULT_COLOR);
		TextColor textColor = TextColor.fromHexString(hex);
		return textColor != null ? textColor : TextColor.color(DEFAULT_COLOR);
	}

	/**
	 * Recursively strips clickEvent from a Component tree.
	 * Defends against a malicious peer server embedding runCommand click events
	 * directly into prefix/suffix/sender/text Components via Gson.
	 */
	private static @NotNull Component sanitize(@NotNull Component component) {
		Component stripped = component.clickEvent(null);
		List<Component> children = component.children();
		if (children.isEmpty()) return stripped;

		List<Component> sanitizedChildren = new ArrayList<>(children.size());
		for (Component child : children) {
			sanitizedChildren.add(sanitize(child));
		}
		return stripped.children(sanitizedChildren);
	}
}
