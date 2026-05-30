package ru.deelter.chat.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JoinQuitConfig {

	@Getter
	private static boolean enabled;
	@Getter
	private static boolean useActionBar;
	@Getter
	private static boolean welcomeEnabled;
	@Getter
	private static String welcomeSound;
	@Getter
	private static boolean welcomeBackEnabled;
	@Getter
	private static String welcomeBackSound;

	public static void init(FileConfiguration config) {
		enabled = config.getBoolean("join-quit.enabled", true);
		useActionBar = config.getBoolean("join-quit.use-actionbar", false);
		welcomeEnabled = config.getBoolean("join-quit.welcome.enabled", true);
		welcomeSound = config.getString("join-quit.welcome.sound", "");
		welcomeBackEnabled = config.getBoolean("join-quit.welcome-back.enabled", true);
		welcomeBackSound = config.getString("join-quit.welcome-back.sound", "");
	}
}
