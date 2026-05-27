package ru.deelter.chat.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.utils.ChatUtils;
import ru.deelter.chat.utils.GlobalChatPayload;
import ru.deelter.chat.utils.translator.OnlineTranslator;
import ru.deelter.chat.utils.translator.TranslationLanguage;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class GlobalMessageListener implements PluginMessageListener {

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
		final Component frame = payload.getFrame() != null ? payload.getFrame() : Component.empty();
		final Component originalText = sanitize(payload.getText() != null ? payload.getText() : Component.empty());

		final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

		Bukkit.getScheduler().runTaskAsynchronously(BetterChat.getInstance(), () -> {
			boolean translationEnabled = BetterChat.getInstance().getLang().isTranslationEnabled();
			String originalPlain = translationEnabled
					? PlainTextComponentSerializer.plainText().serialize(originalText)
					: null;

			// Cache translated message per target locale — one HTTP call per unique target, not per player
			Map<Locale, Component> messageByLocale = new HashMap<>();

			for (Player player : players) {
				if (!player.isOnline()) continue;
				var langManager = BetterChat.getInstance().getLanguageManager();

				Component messageComponent;
				if (!translationEnabled || !langManager.shouldTranslate(senderLocale, player)) {
					messageComponent = originalText;
				} else {
					Locale targetLocale = langManager.getTargetTranslationLocale(player);
					messageComponent = messageByLocale.computeIfAbsent(targetLocale, loc -> {
						String translated = OnlineTranslator.translate(
								originalPlain,
								TranslationLanguage.AUTO,
								TranslationLanguage.from(loc)
						);
						return Component.text(translated).hoverEvent(HoverEvent.showText(originalText));
					});
				}

				player.sendMessage(injectMessage(frame, messageComponent));
			}
		});
	}

	/**
	 * Recursively walks the Component tree and replaces the sentinel text node with the actual message,
	 * preserving styling (color, decorations, font) from the sentinel.
	 */
	private static @NotNull Component injectMessage(@NotNull Component component, @NotNull Component message) {
		if (component instanceof TextComponent tc && GlobalChatPayload.SENTINEL.equals(tc.content())) {
			return message
					.color(component.color())
					.decorations(component.decorations())
					.font(component.font());
		}
		List<Component> children = component.children();
		if (children.isEmpty()) return component;

		List<Component> newChildren = new ArrayList<>(children.size());
		for (Component child : children) {
			newChildren.add(injectMessage(child, message));
		}
		return component.children(newChildren);
	}

	/**
	 * Recursively strips clickEvent from a Component tree.
	 * Defends against a malicious peer server embedding runCommand click events
	 * directly into the text Component via Gson.
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
