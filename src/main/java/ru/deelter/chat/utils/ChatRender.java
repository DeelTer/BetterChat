package ru.deelter.chat.utils;

import io.papermc.paper.chat.ChatRenderer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ChatRender implements ChatRenderer {

	private final ChatData data;

	@Override
	public @NotNull Component render(@NotNull Player player, @NotNull Component displayName, @NotNull Component message, @NotNull Audience audience) {
		return ChatUtils.render(data, player, displayName, message, audience);
	}
}
