package ru.deelter.chat.processors.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.config.MentionConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;

import java.util.regex.Pattern;

public class MentionProcessor extends AbstractChatProcessor {

	private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

	public MentionProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData data) {
		if (!MentionConfig.isEnabled()) return;

		data.setText(data.getText().replaceText(
				TextReplacementConfig.builder()
						.match(MENTION_PATTERN)
						.replacement((result, _) -> {
							String targetName = result.group(1);
							Player target = Bukkit.getPlayerExact(targetName);
							if (target == null) return Component.text(result.group());

							boolean canHear = data.getAudiences().stream()
									.filter(a -> a instanceof Player)
									.map(a -> (Player) a)
									.anyMatch(p -> p.getName().equalsIgnoreCase(targetName));

							boolean wantsPing = !target.hasPermission("betterchat.mention.bypass");

							if (canHear && wantsPing) {
								target.playSound(MentionConfig.getSound());
							}

							return Component.text("@" + targetName, MentionConfig.getColor());
						})
						.build()
		));
	}

	@Override
	public boolean canProcess(@NotNull ChatData data) {
		return MentionConfig.isEnabled();
	}
}