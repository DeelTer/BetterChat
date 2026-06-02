package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BiomeReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern BIOME_PATTERN = Pattern.compile("\\[biome\\]");

	public BiomeReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		Biome biome = player.getLocation().getBlock().getBiome();
		String biomeName = formatBiomeName(biome);

		FileConfiguration config = BetterChat.getInstance().getConfig();
		String colorHex = config.getString("replacers.biome.color", "#55FFFF");
		TextColor color = TextColor.fromHexString(colorHex);
		if (color == null) color = TextColor.color(0x55FFFF);

		TextColor finalColor = color;
		Component hoverText = Component.text(biome.getKey().toString(), TextColor.color(0x777777));

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(BIOME_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("biome"))
						.append(Component.text(" " + biomeName, finalColor))
						.hoverEvent(HoverEvent.showText(hoverText)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private String formatBiomeName(Biome biome) {
		return Arrays.stream(biome.name().split("_"))
				.map(w -> w.charAt(0) + w.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getEntity() instanceof Player;
	}
}
