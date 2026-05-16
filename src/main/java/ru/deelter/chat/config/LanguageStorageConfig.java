package ru.deelter.chat.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NonNull;

@Getter
public final class LanguageStorageConfig {

	@Getter
	private static String type;
	@Getter
	private static String mysqlHost;
	@Getter
	private static int mysqlPort;
	@Getter
	private static String mysqlDatabase;
	@Getter
	private static String mysqlUser;
	@Getter
	private static String mysqlPassword;

	public static void init(@NonNull FileConfiguration config) {
		type = config.getString("language-storage.type", "H2");
		mysqlHost = config.getString("language-storage.mysql.host", "localhost");
		mysqlPort = config.getInt("language-storage.mysql.port", 3306);
		mysqlDatabase = config.getString("language-storage.mysql.database", "betterchat");
		mysqlUser = config.getString("language-storage.mysql.user", "root");
		mysqlPassword = config.getString("language-storage.mysql.password", "");
	}
}