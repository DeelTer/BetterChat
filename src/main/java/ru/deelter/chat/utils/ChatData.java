package ru.deelter.chat.utils;

import io.github.miniplaceholders.api.types.RelationalAudience;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bubbles.BubbleManager;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.tags.ChatTag;

import java.util.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChatData {

	private String format;
	@Builder.Default
	private Component text = Component.text("text");
	@Builder.Default
	private Set<Audience> audiences = new HashSet<>();
	private Entity entity;
	private Location location;
	@Builder.Default
	private double radius = ChatConfig.defaultRadius;
	private boolean darkness;
	private boolean invisible;
	private boolean underwater;
	private boolean deepSleeping;
	private boolean spectating;
	private AsyncChatEvent asyncChatEvent;
	@Builder.Default
	private Locale locale = Locale.forLanguageTag(BetterChat.getInstance()
			.getLang()
			.getDefaultLanguage());
	@Builder.Default
	private Component prefix = Component.empty();
	@Builder.Default
	private Component suffix = Component.empty();
	private Component name;
	@Builder.Default
	private TextColor color = ChatConfig.colorDefault;
	@Builder.Default
	private TextColor color2 = ChatConfig.colorDefault2;
	@Builder.Default
	private boolean terminated = false;
	private ChatTag matchedTag;

	// Фабрики
	public static ChatData fromEntity(@NotNull Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			return fromLivingEntity(livingEntity);
		}
		return ChatData.builder()
				.entity(entity)
				.underwater(entity.isUnderWater())
				.name(entity.name())
				.location(entity.getLocation())
				.darkness(entity.getLocation().getBlock().getLightLevel() < 2.0)
				.format(ChatConfig.formatDefault)
				.build();
	}

	public static ChatData fromLivingEntity(@NotNull LivingEntity entity) {
		ChatDataBuilder builder = ChatData.builder()
				.entity(entity)
				.underwater(entity.isUnderWater())
				.name(entity.name())
				.location(entity.getLocation())
				.darkness(entity.getEyeLocation().getBlock().getLightLevel() < 2.0)
				.invisible(entity.hasPotionEffect(PotionEffectType.INVISIBILITY))
				.format(ChatConfig.formatDefault);
		if (entity instanceof Player player) {
			builder.name(player.displayName())
					.deepSleeping(player.isDeeplySleeping())
					.spectating(player.getGameMode() == GameMode.SPECTATOR)
					.locale(BetterChat.getInstance().getLanguageManager().getLocale(player));
		}
		return builder.build();
	}

	public static ChatData fromAsyncEvent(@NotNull AsyncChatEvent event) {
		Player player = event.getPlayer();
		return ChatData.builder()
				.entity(player)
				.name(player.displayName())
				.locale(BetterChat.getInstance().getLanguageManager().getLocale(player))
				.underwater(player.isUnderWater())
				.location(player.getLocation())
				.darkness(player.getEyeLocation().getBlock().getLightLevel() < 2.0)
				.invisible(player.hasPotionEffect(PotionEffectType.INVISIBILITY))
				.deepSleeping(player.isDeeplySleeping())
				.spectating(player.getGameMode() == GameMode.SPECTATOR)
				.text(event.message())
				.audiences(new HashSet<>(event.viewers()))
				.asyncChatEvent(event)
				.format(ChatConfig.formatDefault)
				.build();
	}

	public void setColors(@NotNull TextColor color, @NotNull TextColor color2) {
		this.color = color;
		this.color2 = color2;
	}

	public void setColors(@NotNull String hex1, @NotNull String hex2) {
		setColors(Objects.requireNonNull(TextColor.fromHexString(hex1)),
				Objects.requireNonNull(TextColor.fromHexString(hex2)));
	}

	public void process(@NotNull List<AbstractChatProcessor> processors) {
		processors.stream()
				.filter(processor -> processor.canProcess(this))
				.forEach(processor -> processor.process(this));
	}

	public void process() {
		BetterChat.getInstance().getManager().process(this);
	}

	public void sendBubbles() {
		if (entity == null) return;
		if (isInvisible() || isDarkness() || isDeepSleeping() || isSpectating()) return;

		BubbleManager.sendBubble(entity, getText());
	}

	public void stripAudienceByRadius() {
		if (radius <= 0.0D) return;
		audiences.removeIf(audience -> {
			if (!(audience instanceof LivingEntity livingEntity)) return false;
			return !LocationUtils.isNear(livingEntity.getLocation(), location, radius);
		});
	}

	public void createAudience() {
		if (radius <= 0D) {
			audiences.addAll(Bukkit.getOnlinePlayers());
		} else {
			audiences.addAll(location.getNearbyPlayers(radius));
		}
	}

	public void send(boolean stripByRadius) {
		if (stripByRadius) stripAudienceByRadius();
		send(audiences);
	}

	public void send() {
		send(true);
	}

	public void send(@NotNull Collection<Audience> audiences) {
		audiences.forEach(audience -> {
			Component text = getText();
			text = ChatUtils.translate(locale, audience, text);
			Component rendered = MiniMessage.miniMessage()
					.deserialize(
							getFormat(),
							MiniPlaceholdersHook.isEnabled() && this.entity != null ? new RelationalAudience<>(audience, this.entity) : audience,
							Placeholder.component("prefix", getPrefix()),
							Placeholder.component("suffix", getSuffix()),
							Placeholder.component("sender", getName()),
							Placeholder.component("message", text),
							Placeholder.styling("color1", getColor()),
							Placeholder.styling("color2", getColor2()),
							MiniPlaceholdersHook.relationalResolver()
					);
			audience.sendMessage(rendered);
		});
	}

}