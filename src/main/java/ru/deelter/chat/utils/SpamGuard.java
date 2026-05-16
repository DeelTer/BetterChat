package ru.deelter.chat.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.LinkedList;
import java.util.UUID;

public class SpamGuard {

	private static final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
	private static final Cache<UUID, LinkedList<String>> recentMessages = Caffeine.newBuilder()
			.expireAfterAccess(Duration.ofSeconds(5))
			.build();

	public static boolean isSimilar(@NonNull Player player, String message, double threshold, int maxCount) {
		UUID uuid = player.getUniqueId();
		LinkedList<String> list = recentMessages.get(uuid, k -> new LinkedList<>());

		synchronized (list) {
			for (String old : list) {
				if (similarity.apply(message, old) >= threshold) {
					return true;
				}
			}
			list.addLast(message);
			if (list.size() > maxCount) list.removeFirst();
		}
		return false;
	}

	public static void clear(@NonNull Player player) {
		recentMessages.invalidate(player.getUniqueId());
	}
}