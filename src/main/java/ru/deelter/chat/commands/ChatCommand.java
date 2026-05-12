package ru.deelter.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
	                         @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		if (args.length == 0) {
			player.sendMessage("Usage: /chat <message>");
			return true;
		}
		String message = String.join(" ", args);
		player.chat(message);
		return true;
	}
}