package ru.deelter.chat.bubbles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.BetterChat;
import ru.deelter.chat.config.BubbleConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BubbleManager {

	public static final Map<UUID, BubbleQueue> QUEUE = new ConcurrentHashMap<>();

	public static void sendBubble(@NotNull Entity entity, @NotNull Component content, int ticks, int width) {
		if (Bukkit.getTPS()[0] < BubbleConfig.getMinTpsForBubbles()) return;

		Bubble bubble = new Bubble(entity, content, ticks);
		bubble.setWidth(width);
		bubble.queue();
	}

	public static void sendBubble(@NotNull Entity entity, @NotNull Component content, int ticks) {
		sendBubble(entity, content, ticks, 200);
	}

	public static void sendBubble(@NotNull Entity entity, @NotNull Component content) {
		String text = PlainTextComponentSerializer.plainText().serialize(content);
		sendBubble(entity, content, Math.max(text.length() * 2, 40));
	}

	public static void runQueueTimer() {
		Bukkit.getScheduler().runTaskTimer(BetterChat.getInstance(), () -> {
			QUEUE.entrySet().removeIf(entry -> {
				Entity source = Bukkit.getEntity(entry.getKey());
				BubbleQueue queue = entry.getValue();

				if (source == null || source.isDead() || queue.getBubbles().isEmpty())
					return true;
				if (!source.getPassengers().isEmpty())
					return false;

				queue.showNext();
				return true;
			});
		}, 0, BubbleConfig.getQueueTickInterval());
	}

	public static void invalidateBubbles() {
		QUEUE.values().forEach(queue -> queue.getBubbles().forEach(Bubble::remove));
	}

	public static void invalidateBubbles(@NotNull UUID uuid) {
		BubbleQueue queue = QUEUE.get(uuid);
		if (queue == null) return;
		queue.getBubbles().forEach(Bubble::remove);
	}
}