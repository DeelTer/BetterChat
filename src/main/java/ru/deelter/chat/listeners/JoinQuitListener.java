package ru.deelter.chat.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.JoinQuitConfig;
import ru.deelter.chat.language.Lang;
import ru.deelter.chat.utils.MiniPlaceholdersHook;

public class JoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (JoinQuitConfig.isEnabled()) {
            event.joinMessage(null);
            broadcast(player, "join");
        }
        sendWelcome(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        if (!JoinQuitConfig.isEnabled()) return;
        event.quitMessage(null);
        broadcast(event.getPlayer(), "quit");
    }

    private void sendWelcome(Player player) {
        Lang lang = BetterChat.getInstance().getLang();
        boolean firstJoin = !player.hasPlayedBefore();

        if (firstJoin && JoinQuitConfig.isWelcomeEnabled()) {
            sendPrivate(player, lang.getRaw("welcome", player), JoinQuitConfig.getWelcomeSound());
        } else if (!firstJoin && JoinQuitConfig.isWelcomeBackEnabled()) {
            sendPrivate(player, lang.getRaw("welcome-back", player), JoinQuitConfig.getWelcomeBackSound());
        }
    }

    private void sendPrivate(Player player, String raw, String sound) {
        if (raw.isEmpty()) return;
        Component message = MiniMessage.miniMessage().deserialize(raw, player, MiniPlaceholdersHook.resolver());
        player.sendMessage(message);
        if (sound != null && !sound.isEmpty()) {
            player.playSound(player.getLocation(), sound, 1f, 1f);
        }
    }

    private void broadcast(Player subject, String langKey) {
        boolean actionBar = JoinQuitConfig.isUseActionBar();
        Lang lang = BetterChat.getInstance().getLang();
        for (Player online : Bukkit.getOnlinePlayers()) {
            String raw = lang.getRaw(langKey, online);
            if (raw.isEmpty()) continue;
            Component message = MiniMessage.miniMessage().deserialize(raw, subject, MiniPlaceholdersHook.resolver());
            if (actionBar) {
                online.sendActionBar(message);
            } else {
                online.sendMessage(message);
            }
        }
    }
}
