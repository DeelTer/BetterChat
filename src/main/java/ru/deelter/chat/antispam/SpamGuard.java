package ru.deelter.chat.antispam;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class SpamGuard {
	private static final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
	private static final Map<UUID, LinkedList<String>> recentMessages = new HashMap<>();

	public static boolean isSimilar(@NonNull Player player, String message, double threshold, int maxCount) {
		UUID uuid = player.getUniqueId();
		LinkedList<String> list = recentMessages.computeIfAbsent(uuid, k -> new LinkedList<>());
		for (String old : list) {
			if (similarity.apply(message, old) >= threshold) {
				return true;
			}
		}
		list.addLast(message);
		if (list.size() > maxCount) list.removeFirst();
		return false;
	}

	public static void clear(@NonNull Player player) {
		recentMessages.remove(player.getUniqueId());
	}
}