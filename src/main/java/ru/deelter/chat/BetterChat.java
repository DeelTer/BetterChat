package ru.deelter.chat;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.chat.bubbles.BubbleChatListener;
import ru.deelter.chat.bubbles.BubbleManager;
import ru.deelter.chat.commands.LangCommand;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.config.IconProvider;
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


		lang = new Lang(this);
		manager.load();

		ChatConfig.init(getConfig());
		IconProvider.init(getConfig());
		ChatLink.load(getConfig());
		ChatTagRegistry.init();

		Bukkit.getPluginManager().registerEvents(new PlayerTextListener(), this);
		Bukkit.getPluginManager().registerEvents(new BubbleChatListener(), this);

		BubbleManager.runQueueTimer();

		PluginCommand langCmd = getCommand("lang");
		if (langCmd != null) {
			LangCommand executor = new LangCommand();
			langCmd.setExecutor(executor);
			langCmd.setTabCompleter(executor);
		}
	}

	@Override
	public void onDisable() {
		manager.unload();
	}

	public void reload() {
		this.reloadConfig();
		ChatConfig.init(getConfig());
		IconProvider.init(getConfig());
		ChatLink.reload(this.getConfig());
		lang.reload();
	}
}