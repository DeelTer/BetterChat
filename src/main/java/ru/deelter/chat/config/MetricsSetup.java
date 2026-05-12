package ru.deelter.chat.config;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.BetterChat;

public final class MetricsSetup {

	private static final int PLUGIN_ID = 31258;

	private MetricsSetup() {
	}

	public static void init(@NonNull BetterChat plugin) {
		if (!plugin.getConfig().getBoolean("metrics.enabled", true)) {
			plugin.getLogger().info("bStats metrics disabled in config.");
			return;
		}

		Metrics metrics = new Metrics(plugin, PLUGIN_ID);

		metrics.addCustomChart(new SimplePie("default_language", () ->
				plugin.getConfig().getString("language.default", "en")));
		metrics.addCustomChart(new SimplePie("auto_detect_language", () ->
				plugin.getConfig().getBoolean("language.auto-detect", true) ? "Yes" : "No"));
		metrics.addCustomChart(new SimplePie("auto_translation_enabled", () ->
				plugin.getConfig().getBoolean("translation.enabled", true) ? "Yes" : "No"));


		metrics.addCustomChart(new SingleLineChart("custom_tags_count", () -> {
			var section = plugin.getConfig().getConfigurationSection("tags");
			return section == null ? 0 : section.getKeys(false).size();
		}));
		metrics.addCustomChart(new SingleLineChart("custom_links_count", () -> {
			var section = plugin.getConfig().getConfigurationSection("links");
			return section == null ? 0 : section.getKeys(false).size();
		}));

		metrics.addCustomChart(new SingleLineChart("active_processors", () ->
				plugin.getManager().getProcessors().size()));

		metrics.addCustomChart(new SimplePie("bubbles_pop_up_animation", () ->
				plugin.getConfig().getBoolean("bubbles.animation.pop-up.enabled", true) ? "Yes" : "No"));

		metrics.addCustomChart(new SimplePie("default_chat_radius", () -> {
			double radius = plugin.getConfig().getDouble("chat.default-radius", 50.0);
			if (radius <= 0) return "global";
			if (radius <= 30) return "small (1-30)";
			if (radius <= 100) return "medium (31-100)";
			return "large (>100)";
		}));

		plugin.getLogger().info("bStats initialized (deelter-betterchat, ID: " + PLUGIN_ID + ")");
	}
}