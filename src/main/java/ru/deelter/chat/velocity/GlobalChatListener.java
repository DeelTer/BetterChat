package ru.deelter.chat.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.jspecify.annotations.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GlobalChatListener {

	private final ProxyServer server;
	private final String filterMode;          // "none", "blacklist", "whitelist"
	private final List<String> filterServers;

	public GlobalChatListener(ProxyServer server, String filterMode, List<String> filterServers) {
		this.server = server;
		this.filterMode = filterMode;
		this.filterServers = filterServers;
	}

	@Subscribe
	public void onPluginMessage(PluginMessageEvent event) {
		if (!event.getIdentifier().getId().equals("betterchat:global")) return;

		byte[] data = event.getData();
		String fullPayload = new String(data, StandardCharsets.UTF_8);
		String[] parts = fullPayload.split("\\|", 4);
		if (parts.length < 4) return;

		String routing = parts[0];
		String locale = parts[1];
		String format = parts[2];
		String rawText = parts[3];

		String[] routingParts = routing.split(":", 2);
		String mode = routingParts[0];                     // whitelist / blacklist
		List<String> targetServers = new ArrayList<>();
		if (routingParts.length > 1 && !routingParts[1].isEmpty()) {
			targetServers = List.of(routingParts[1].split(","));
		}

		String sourceServer = null;
		if (event.getSource() instanceof Player player) {
			sourceServer = player.getCurrentServer()
					.map(server -> server.getServerInfo().getName())
					.orElse(null);
		}

		for (var server : server.getAllServers()) {
			String serverName = server.getServerInfo().getName();
			if (serverName.equals(sourceServer)) continue;
			if (!isAllowedByRouting(serverName, mode, targetServers)) continue;

			// payload without routing-part (locale|format|raw)
			String newPayload = locale + "|" + format + "|" + rawText;
			server.sendPluginMessage(
					MinecraftChannelIdentifier.create("betterchat", "global"),
					newPayload.getBytes(StandardCharsets.UTF_8)
			);
		}
	}

	private boolean isAllowedByRouting(String serverName, String mode, @NonNull List<String> serverList) {
		if (serverList.isEmpty()) return true; // Empty = all servers
		if ("whitelist".equalsIgnoreCase(mode)) {
			return serverList.contains(serverName);
		} else if ("blacklist".equalsIgnoreCase(mode)) {
			return !serverList.contains(serverName);
		}
		return true;
	}

	private boolean isAllowed(String serverName) {
		if ("none".equalsIgnoreCase(filterMode)) return true;
		if ("blacklist".equalsIgnoreCase(filterMode)) return !filterServers.contains(serverName);
		if ("whitelist".equalsIgnoreCase(filterMode)) return filterServers.contains(serverName);
		return true;
	}
}