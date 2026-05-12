package ru.deelter.chat.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;

public class LocationUtils {

	public static boolean isNear(@NotNull Location location, @NotNull Location location2, double radius) {
		return location.getWorld().equals(location2.getWorld()) && location.distanceSquared(location2) <= radius * radius;
	}

	public static boolean isNear(@NotNull Entity entity, @NotNull Entity entity2, double radius) {
		return isNear(entity.getLocation(), entity2.getLocation(), radius);
	}

	public static boolean isNearSpawn(@NotNull Location location, double radius) {
		return isNear(location, location.getWorld().getSpawnLocation(), radius);
	}

	public static boolean isNearSpawn(@NotNull Entity entity, double radius) {
		return isNearSpawn(entity.getLocation(), radius);
	}

	@NotNull
	public static Location getLocation(@NotNull Chunk chunk) {
		return new Location(chunk.getWorld(), chunk.getX() * 16, 0, chunk.getZ() * 16);
	}

	@NotNull
	public static Location getHighestLocation(@NotNull Chunk chunk) {
		Location location = getLocation(chunk);
		location.setY(location.getWorld().getHighestBlockYAt(location));
		return location;
	}

	public static long toLong(@NotNull Location location) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		return ((long) x & 0x3FFFFFF) << 38 | ((long) y & 0xFFF) | ((long) z & 0x3FFFFFF) << 12;
	}

	public static void pullEntityToLocation(final @NotNull Entity entity, Location location, double multiply) {
		Location entityLocation = entity.getLocation();
		Vector boost = entity.getVelocity();
		boost.setY(0.3);
		entity.setVelocity(boost);

		Bukkit.getScheduler().scheduleSyncDelayedTask(BetterChat.getInstance(), () -> {
			double g = -0.08;
			double d = location.distance(entityLocation);
			double t = d;
			double v_x = (1.0 + 0.07 * t) * (location.getX() - entityLocation.getX()) / t;
			double v_y = (1.0 + 0.03 * t) * (location.getY() - entityLocation.getY()) / t - 0.5 * g * t;
			double v_z = (1.0 + 0.07 * t) * (location.getZ() - entityLocation.getZ()) / t;

			Vector vector = entity.getVelocity();
			vector.setX(v_x);
			vector.setY(v_y);
			vector.setZ(v_z);
			vector.multiply(multiply);
			entity.setVelocity(vector);
		}, 1L);
	}
}
