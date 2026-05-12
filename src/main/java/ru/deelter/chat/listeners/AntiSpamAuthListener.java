package ru.deelter.chat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.antispam.SpamGuard;

public class AntiSpamAuthListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(@NotNull PlayerQuitEvent event) {
		SpamGuard.clear(event.getPlayer());
	}
}