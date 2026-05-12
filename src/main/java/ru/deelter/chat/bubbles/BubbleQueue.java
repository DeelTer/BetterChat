package ru.deelter.chat.bubbles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BubbleQueue {

	private final List<Bubble> bubbles = new ArrayList<>();
	private final UUID sourceUuid;

	public BubbleQueue(@NotNull Entity source) {
		this(source.getUniqueId());
	}

	public static BubbleQueue get(@NotNull UUID sourceUuid) {
		return QUEUE.containsKey(sourceUuid) ? QUEUE.get(sourceUuid) : new BubbleQueue(sourceUuid);
	}

	public static Map<UUID, BubbleQueue> getQueue() {
		return QUEUE;
	}

	public boolean hasBubbles() {
		return !bubbles.isEmpty();
	}

	public void add(@NotNull Bubble bubble) {
		bubbles.add(bubble);
	}

	public void showNext() {
		Bubble bubble = bubbles.get(0);
		bubble.show();

		Bukkit.getScheduler().runTaskLater(BetterChat.getInstance(), () -> {
			if (!bubbles.isEmpty()) {
				bubbles.removeFirst();
				TextDisplay display = bubble.getDisplay();
				if (display != null) {
					display.remove();
				}
			}
		}, bubble.getDisplayTime());
	}

	public void register() {
		QUEUE.put(sourceUuid, this);
	}

	public void unregister() {
		QUEUE.remove(sourceUuid);
	}

	private static final Map<UUID, BubbleQueue> QUEUE = BubbleManager.QUEUE;
}