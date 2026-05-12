package ru.deelter.chat;

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
import ru.deelter.chat.listeners.PlayerTextListener;
import ru.deelter.chat.managers.ChatProcessorRegistry;
import ru.deelter.chat.processors.replacer.ChatLink;
import ru.deelter.chat.utils.Lang;
import ru.deelter.chat.utils.tags.ChatTagRegistry;

@Getter
public final class BetterChat extends JavaPlugin {

	@Getter
	private static BetterChat instance;
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

		Bukkit.getPluginManager().registerEvents(new PlayerTextListener(), this);
		Bukkit.getPluginManager().registerEvents(new BubbleChatListener(), this);

		BubbleManager.runQueueTimer();

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