package ru.deelter.chat.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.utils.ChatUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GlobalChatListener {

	private final ProxyServer server;
	private final String filterMode;
	private final List<String> filterServers;

	public GlobalChatListener(ProxyServer server, String filterMode, List<String> filterServers) {
		this.server = server;
		this.filterMode = filterMode != null ? filterMode.toLowerCase() : "none";
		this.filterServers = filterServers != null ? filterServers : List.of();
	}

	@Subscribe
	public void onPluginMessage(@NonNull PluginMessageEvent event) {
		if (!event.getIdentifier().getId().equals(ChatUtils.VELOCITY_MESSAGE_CHANNEL_ID)) {
			return;
		}

		if (!(event.getSource() instanceof ServerConnection serverConnection)) {
			return;
		}

		String sourceServer = serverConnection.getServer().getServerInfo().getName();
		String payload = new String(event.getData(), StandardCharsets.UTF_8);

		// Формат: routing|locale|originalJson|renderedJson
		String[] parts = payload.split("\\|", 2);
		String routing = parts.length > 0 ? parts[0] : "whitelist:";

		for (RegisteredServer target : server.getAllServers()) {
			String targetName = target.getServerInfo().getName();
			if (targetName.equals(sourceServer)) continue;

			if (!isAllowedByRouting(routing, targetName)) continue;
			if (!isAllowedByFilter(targetName)) continue;

			target.sendPluginMessage(
					MinecraftChannelIdentifier.create("betterchat", "global"),
					event.getData()
			);
		}
	}

	private boolean isAllowedByRouting(String routing, String targetServer) {
		try {
			String[] r = routing.split(":", 2);
			String mode = r[0].toLowerCase();

			if (r.length < 2 || r[1].isEmpty()) return true;

			List<String> servers = new ArrayList<>();
			for (String s : r[1].split(",")) {
				String trimmed = s.trim();
				if (!trimmed.isEmpty()) servers.add(trimmed);
			}

			if (servers.isEmpty()) return true;

			if ("whitelist".equals(mode)) return servers.contains(targetServer);
			if ("blacklist".equals(mode)) return !servers.contains(targetServer);
		} catch (Exception ignored) {}
		return true;
	}

	private boolean isAllowedByFilter(String serverName) {
		if ("none".equals(filterMode)) return true;
		if ("blacklist".equals(filterMode)) return !filterServers.contains(serverName);
		if ("whitelist".equals(filterMode)) return filterServers.contains(serverName);
		return true;
	}
}