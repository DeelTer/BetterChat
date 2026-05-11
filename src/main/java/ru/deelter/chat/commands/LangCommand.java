package ru.deelter.chat.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.deelter.chat.BetterChat;
import ru.deelter.chat.utils.PlayerLanguageUtil;

import java.util.List;

public class LangCommand implements CommandExecutor, TabCompleter {

	private static final List<String> POPULAR_LANGUAGES = List.of(
			"en", "ru", "de", "fr", "es", "pt", "it", "zh", "ja", "ko", "ar", "hi", "tr", "uk", "pl"
	);

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
	                         @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		if (args.length == 0) {
			player.sendMessage(Component.text("Usage: /lang <language_code>"));
			return true;
		}
		String code = args[0].toLowerCase();
		PlayerLanguageUtil.setLocale(player, code);
		Component message = BetterChat.getInstance().getLang().getMessage("lang-set", player, "lang", code);
		if (message != null) {
			player.sendMessage(message);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
	                                  @NotNull String alias, @NotNull String @NonNull [] args) {
		if (args.length == 1) {
			return POPULAR_LANGUAGES.stream()
					.filter(s -> s.startsWith(args[0].toLowerCase()))
					.toList();
		}
		return List.of();
	}
}