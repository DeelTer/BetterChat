package ru.deelter.chat.bukkit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.chat.bubbles.BubbleChatListener;
import ru.deelter.chat.bubbles.BubbleManager;
import ru.deelter.chat.commands.ChatCommand;
import ru.deelter.chat.commands.LanguageCommand;
import ru.deelter.chat.config.*;
import ru.deelter.chat.language.*;
import ru.deelter.chat.listeners.AntiSpamAuthListener;
import ru.deelter.chat.listeners.GlobalMessageListener;
import ru.deelter.chat.listeners.PlayerMessageListener;
import ru.deelter.chat.processors.ChatProcessorRegistry;
import ru.deelter.chat.replacer.ChatLink;
import ru.deelter.chat.tags.ChatTagRegistry;
import ru.deelter.chat.utils.ChatUtils;
import ru.deelter.chat.language.Lang;

@Getter
public final class BetterChat extends JavaPlugin {

	@Getter
	private static BetterChat instance;
	@Getter
	private static boolean velocityEnabled = false;
	private final ChatProcessorRegistry manager = new ChatProcessorRegistry();
	private Lang lang;
	private LanguageManager languageManager;

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();

		FileConfiguration config = getConfig();
		ChatConfig.init(config);
		LanguageStorageConfig.init(config);
		IconProvider.init(config);
		BubbleConfig.init(config);
		MentionConfig.init(config);
		AntiSpamConfig.init(config);

		ChatLink.load(config);
		ChatTagRegistry.init();

		lang = new Lang(this);
		manager.load();

		initLanguageStorage();
		linkVelocity();
		BubbleManager.runQueueTimer();

		Bukkit.getPluginManager().registerEvents(new PlayerMessageListener(), this);
		Bukkit.getPluginManager().registerEvents(new BubbleChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new AntiSpamAuthListener(), this);

		PluginCommand langCommand = getCommand("lang");
		if (langCommand != null) {
			LanguageCommand executor = new LanguageCommand();
			langCommand.setExecutor(executor);
			langCommand.setTabCompleter(executor);
		}
		PluginCommand chatCommand = getCommand("chat");
		if (chatCommand != null) {
			chatCommand.setExecutor(new ChatCommand());
		}
		MetricsSetup.init(this);
	}

	private void linkVelocity() {
		velocityEnabled = getConfig().getBoolean("velocity.enabled", false);
		if (!velocityEnabled) return;

		getServer().getMessenger().registerOutgoingPluginChannel(this, ChatUtils.VELOCITY_MESSAGE_CHANNEL_ID);
		getServer().getMessenger().registerIncomingPluginChannel(this, ChatUtils.VELOCITY_MESSAGE_CHANNEL_ID, new GlobalMessageListener());
	}

	@Override
	public void onDisable() {
		languageManager.shutdown();
		manager.unload();
	}

	public void reload() {
		this.reloadConfig();
		FileConfiguration config = getConfig();
		ChatConfig.init(config);
		IconProvider.init(config);
		ChatLink.reload(config);
		lang.reload();
	}

	private void initLanguageStorage() {
		LanguageStorage storage = switch (LanguageStorageConfig.getType().toUpperCase()) {
			case "MYSQL" -> new MySQLStorage(
					LanguageStorageConfig.getMysqlHost(),
					LanguageStorageConfig.getMysqlPort(),
					LanguageStorageConfig.getMysqlDatabase(),
					LanguageStorageConfig.getMysqlUser(),
					LanguageStorageConfig.getMysqlPassword()
			);
			case "SQLITE" -> new SQLiteStorage();
			default -> new H2Storage();
		};
		languageManager = new LanguageManager();
		languageManager.init(storage);
	}
}