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

import java.util.regex.Pattern;

public class BiomeReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern BIOME_PATTERN = Pattern.compile("\\[biome\\]");

	public BiomeReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		Biome biome = player.getLocation().getBlock().getBiome();

		FileConfiguration config = BetterChat.getInstance().getConfig();
		String colorHex = config.getString("replacers.biome.color", "#59A65E");
		TextColor color = TextColor.fromHexString(colorHex);
		if (color == null) color = TextColor.color(0x59A65E);

		TextColor finalColor = color;
		Component hoverText = Component.text(biome.getKey().toString(), TextColor.color(0x777777));

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(BIOME_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("biome"))
						.append(Component.translatable(biome.translationKey()).color(finalColor))
						.hoverEvent(HoverEvent.showText(hoverText)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getEntity() instanceof Player;
	}
}
