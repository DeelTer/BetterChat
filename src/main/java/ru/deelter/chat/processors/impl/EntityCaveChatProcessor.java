package ru.deelter.chat.processors.impl;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.utils.ChatUtils;
import ru.deelter.chat.utils.LocationUtils;

import java.util.Set;

public class EntityCaveChatProcessor extends AbstractChatProcessor {

	public EntityCaveChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		Set<Audience> viewers = data.getAudiences();
		Component message = ChatUtils.applyFormat(data, ChatConfig.formatCave);
		Location location = data.getLocation();

		Bukkit.getOnlinePlayers().forEach(viewer -> {
			Location viewerLocation = viewer.getLocation();
			if (!isInCave(viewerLocation)) return;
			if (!LocationUtils.isNear(location, viewerLocation, ChatData.CAVE_RADIUS)) return;
			if (viewers.contains(viewer)) return;

			viewer.sendMessage(message);
		});
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return isInCave(data.getLocation());
	}

	private boolean isInCave(@NotNull Location location) {
		if (location.getY() >= 0) return false;
		return location.getWorld().getEnvironment() == World.Environment.NORMAL;
	}
}