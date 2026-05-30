package ru.deelter.chat.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JoinQuitConfig {

    private static boolean enabled;
    private static boolean useActionBar;
    private static boolean welcomeEnabled;
    private static String welcomeSound;
    private static boolean welcomeBackEnabled;
    private static String welcomeBackSound;

    public static void init(FileConfiguration config) {
        enabled = config.getBoolean("join-quit.enabled", true);
        useActionBar = config.getBoolean("join-quit.use-actionbar", false);
        welcomeEnabled = config.getBoolean("join-quit.welcome.enabled", true);
        welcomeSound = config.getString("join-quit.welcome.sound", "");
        welcomeBackEnabled = config.getBoolean("join-quit.welcome-back.enabled", true);
        welcomeBackSound = config.getString("join-quit.welcome-back.sound", "");
    }

    public static boolean isEnabled() { return enabled; }
    public static boolean isUseActionBar() { return useActionBar; }
    public static boolean isWelcomeEnabled() { return welcomeEnabled; }
    public static String getWelcomeSound() { return welcomeSound; }
    public static boolean isWelcomeBackEnabled() { return welcomeBackEnabled; }
    public static String getWelcomeBackSound() { return welcomeBackSound; }
}
