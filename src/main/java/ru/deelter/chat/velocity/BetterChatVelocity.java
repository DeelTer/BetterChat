package ru.deelter.chat.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Plugin(id = "betterchat", name = "BetterChat", version = "1.0")
public class BetterChatVelocity {

	private final ProxyServer server;
	private final Logger logger;

	@Inject
	public BetterChatVelocity(ProxyServer server, Logger logger) {
		this.server = server;
		this.logger = logger;
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {

		Path configPath = Path.of("plugins", "BetterChat", "config.yml");
		String filterMode = "none";
		List<String> filterServers = List.of();
		if (Files.exists(configPath)) {
			try {
				YamlConfiguration config = YamlConfiguration.loadConfiguration(configPath.toFile());
				filterMode = config.getString("velocity.server-filter.mode", "none");
				filterServers = config.getStringList("velocity.server-filter.servers");
			} catch (Exception e) {
				logger.warn("Cannot read velocity config section", e);
			}
		}
		server.getChannelRegistrar().register(MinecraftChannelIdentifier.create("betterchat", "global"));
		server.getEventManager().register(this, new GlobalChatListener(server, filterMode, filterServers));
	}
}