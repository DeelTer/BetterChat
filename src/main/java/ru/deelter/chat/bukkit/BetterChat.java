package ru.deelter.chat.bukkit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.chat.bubbles.BubbleChatListener;
import ru.deelter.chat.bubbles.BubbleManager;
import ru.deelter.chat.commands.ChatCommand;
import ru.deelter.chat.commands.LangCommand;
import ru.deelter.chat.config.*;
import ru.deelter.chat.listeners.AntiSpamAuthListener;
import ru.deelter.chat.listeners.GlobalMessageListener;
import ru.deelter.chat.listeners.PlayerMessageListener;
import ru.deelter.chat.processors.ChatProcessorRegistry;
import ru.deelter.chat.replacer.ChatLink;
import ru.deelter.chat.tags.ChatTagRegistry;
import ru.deelter.chat.utils.Lang;

@Getter
public final class BetterChat extends JavaPlugin {

	@Getter
	private static BetterChat instance;
	@Getter
	private static boolean velocityEnabled = false;
	private final ChatProcessorRegistry manager = new ChatProcessorRegistry();
	private Lang lang;

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
		IconProvider.init(config);
		BubbleConfig.init(config);
		MentionConfig.init(config);
		AntiSpamConfig.init(config);

		ChatLink.load(config);
		ChatTagRegistry.init();
		lang = new Lang(this);
		manager.load();

		linkVelocity();
		BubbleManager.runQueueTimer();

		Bukkit.getPluginManager().registerEvents(new PlayerMessageListener(), this);
		Bukkit.getPluginManager().registerEvents(new BubbleChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new AntiSpamAuthListener(), this);

		PluginCommand langCmd = getCommand("lang");
		if (langCmd != null) {
			LangCommand executor = new LangCommand();
			langCmd.setExecutor(executor);
			langCmd.setTabCompleter(executor);
		}
		PluginCommand chatCmd = getCommand("chat");
		if (chatCmd != null) {
			chatCmd.setExecutor(new ChatCommand());
		}
		MetricsSetup.init(this);
	}

	private void linkVelocity() {
		velocityEnabled = getConfig().getBoolean("velocity.enabled", false);
		if (!velocityEnabled) return;

		getServer().getMessenger().registerOutgoingPluginChannel(this, "betterchat:global");
		getServer().getMessenger().registerIncomingPluginChannel(this, "betterchat:global", new GlobalMessageListener());
	}

	@Override
	public void onDisable() {
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
}