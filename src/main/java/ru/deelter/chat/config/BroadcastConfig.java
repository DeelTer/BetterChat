package ru.deelter.chat.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BroadcastConfig {

    @Value
    public static class BroadcastMessage {
        String text;
        String sound;
    }

    private static boolean enabled;
    private static int intervalTicks;
    private static boolean random;
    private static final List<BroadcastMessage> messages = new ArrayList<>();

    public static void init(FileConfiguration config) {
        messages.clear();
        enabled = config.getBoolean("broadcast.enabled", false);
        intervalTicks = config.getInt("broadcast.interval-ticks", 6000);
        random = config.getBoolean("broadcast.random", false);

        ConfigurationSection section = config.getConfigurationSection("broadcast.messages");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection entry = section.getConfigurationSection(key);
            if (entry == null) continue;
            String text = entry.getString("text", "");
            String sound = entry.getString("sound", "");
            if (!text.isEmpty()) messages.add(new BroadcastMessage(text, sound));
        }
    }

    public static boolean isEnabled() { return enabled; }
    public static int getIntervalTicks() { return intervalTicks; }
    public static boolean isRandom() { return random; }
    public static List<BroadcastMessage> getMessages() { return messages; }
}
