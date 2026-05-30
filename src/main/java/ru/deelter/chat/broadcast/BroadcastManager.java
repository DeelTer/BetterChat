package ru.deelter.chat.broadcast;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.BroadcastConfig;
import ru.deelter.chat.config.BroadcastConfig.BroadcastMessage;
import ru.deelter.chat.language.Lang;
import ru.deelter.chat.utils.MiniPlaceholdersHook;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BroadcastManager {

    private static BukkitTask task;
    private static int index = 0;

    public static void start() {
        stop();
        if (!BroadcastConfig.isEnabled()) return;
        int interval = BroadcastConfig.getIntervalTicks();
        task = Bukkit.getScheduler().runTaskTimer(BetterChat.getInstance(), BroadcastManager::tick, interval, interval);
    }

    public static void stop() {
        if (task != null && !task.isCancelled()) task.cancel();
        task = null;
    }

    private static void tick() {
        List<BroadcastMessage> messages = BroadcastConfig.getMessages();
        if (messages.isEmpty()) return;

        BroadcastMessage msg;
        if (BroadcastConfig.isRandom()) {
            msg = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
        } else {
            msg = messages.get(index % messages.size());
            index++;
        }

        Lang lang = BetterChat.getInstance().getLang();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String raw = lang.getRaw(msg.getText(), player);
            if (raw.isEmpty()) continue;
            Component text = MiniMessage.miniMessage().deserialize(raw, player, MiniPlaceholdersHook.resolver());
            player.sendMessage(text);
            if (msg.getSound() != null && !msg.getSound().isEmpty()) {
                player.playSound(player.getLocation(), msg.getSound(), 1f, 1f);
            }
        }
    }
}
