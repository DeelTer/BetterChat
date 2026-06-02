package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class ArmorReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern ARMOR_PATTERN = Pattern.compile("\\[armor\\]");

	public ArmorReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		AttributeInstance attr = player.getAttribute(Attribute.ARMOR);
		int armorPoints = attr != null ? (int) attr.getValue() : 0;

		FileConfiguration config = BetterChat.getInstance().getConfig();
		String colorHex = config.getString("replacers.armor.color", "#AAAAAA");
		TextColor color = TextColor.fromHexString(colorHex);
		if (color == null) color = TextColor.color(0xAAAAAA);

		TextColor finalColor = color;
		Component hoverText = buildArmorHover(player, finalColor);

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(ARMOR_PATTERN)
				.replacement((result, builder) -> Component.empty()
						.append(IconProvider.getIcon("armor"))
						.append(Component.text(" " + armorPoints, finalColor))
						.hoverEvent(HoverEvent.showText(hoverText)))
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private Component buildArmorHover(Player player, TextColor color) {
		PlayerInventory inv = player.getInventory();
		Component hover = Component.empty();
		hover = appendSlot(hover, "Helmet", inv.getHelmet(), color);
		hover = appendSlot(hover, "Chestplate", inv.getChestplate(), color);
		hover = appendSlot(hover, "Leggings", inv.getLeggings(), color);
		hover = appendSlot(hover, "Boots", inv.getBoots(), color);
		return hover;
	}

	private Component appendSlot(Component base, String slot, ItemStack item, TextColor color) {
		if (!base.equals(Component.empty())) {
			base = base.append(Component.newline());
		}
		if (item == null || item.getType().isAir()) {
			return base.append(Component.text(slot + ": empty", TextColor.color(0x555555)));
		}
		Component name;
		if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
			name = item.getItemMeta().displayName();
		} else {
			name = Component.translatable(item.getType().translationKey()).color(color);
		}
		return base.append(Component.text(slot + ": ", color)).append(name);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getEntity() instanceof Player;
	}
}
