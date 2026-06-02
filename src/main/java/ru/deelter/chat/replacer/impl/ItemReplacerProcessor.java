package ru.deelter.chat.replacer.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.IconProvider;
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class ItemReplacerProcessor extends AbstractReplacerProcessor {

	public static final Pattern ITEM_PATTERN = Pattern.compile("\\[item(?::([a-zA-Z0-9]+))?\\]");

	public ItemReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!(data.getEntity() instanceof Player player)) return;

		TextReplacementConfig replacer = TextReplacementConfig.builder()
				.match(ITEM_PATTERN)
				.replacement((result, builder) -> {
					ItemStack item = resolveItem(player, result.group(1));

					Component icon = IconProvider.getIcon("item");

					if (item == null || item.getType() == Material.AIR) {
						return Component.empty()
								.append(icon)
								.append(Component.text(" [empty]"));
					}

					return Component.empty()
							.append(icon)
							.append(Component.text(" "))
							.append(resolveDisplayName(item))
							.hoverEvent(item.asHoverEvent());
				})
				.build();
		data.setText(data.getText().replaceText(replacer));
	}

	private ItemStack resolveItem(Player player, String slotStr) {
		PlayerInventory inv = player.getInventory();
		if (slotStr == null) return inv.getItem(0);
		return switch (slotStr.toLowerCase()) {
			case "hand", "h", "mainhand" -> inv.getItemInMainHand();
			case "offhand", "off", "oh" -> inv.getItemInOffHand();
			case "helmet", "head" -> inv.getHelmet();
			case "chestplate", "chest" -> inv.getChestplate();
			case "leggings", "legs" -> inv.getLeggings();
			case "boots", "feet" -> inv.getBoots();
			default -> {
				try {
					int slot = Integer.parseInt(slotStr);
					yield (slot >= 0 && slot < inv.getSize()) ? inv.getItem(slot) : inv.getItem(0);
				} catch (NumberFormatException e) {
					yield inv.getItem(0);
				}
			}
		};
	}

	private Component resolveDisplayName(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null && meta.hasDisplayName()) {
			return meta.displayName();
		}
		TextColor plainColor = resolvePlainColor();
		return Component.translatable(item.getType().translationKey()).color(plainColor);
	}

	private TextColor resolvePlainColor() {
		String hex = BetterChat.getInstance().getConfig().getString("chat.item-plain-color", "#AAAAAA");
		TextColor color = TextColor.fromHexString(hex);
		return color != null ? color : TextColor.color(0xAAAAAA);
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return data.getEntity() instanceof Player;
	}
}
