package ru.deelter.chat.bubbles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class BubbleChatListener implements Listener {

	@EventHandler
	public void onQuit(@NotNull PlayerQuitEvent event) {
		BubbleManager.invalidateBubbles(event.getPlayer().getUniqueId());
	}
}
