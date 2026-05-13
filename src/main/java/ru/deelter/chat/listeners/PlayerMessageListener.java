package ru.deelter.chat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.processors.ProcessorTag;
import ru.deelter.chat.renders.ChatRender;
import ru.deelter.chat.utils.ChatData;

import java.util.stream.Collectors;

public class PlayerMessageListener implements Listener {
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onChat(@NotNull AsyncChatEvent event) {
		ChatData data = ChatData.fromAsyncEvent(event);
		data.process();
		data.stripAudienceByRadius();

		event.viewers().retainAll(data.getAudiences().stream()
				.filter(a -> a instanceof Player)
				.collect(Collectors.toSet()));
		data.sendBubbles();
		event.renderer(new ChatRender(data));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBookProcessor(@NotNull PlayerEditBookEvent event) {
		BookMeta book = event.getNewBookMeta();
		if (book.equals(event.getPreviousBookMeta())) return;
		if (!event.isSigning()) return;

		for (int i = 1; i <= book.pages().size(); i++) {
			ChatData chatData = new ChatData();
			chatData.setText(book.page(i));
			chatData.process(BetterChat.getInstance()
					.getManager()
					.getProcessors(ProcessorTag.MARKER));
			book.page(i, chatData.getText());
		}
		event.setNewBookMeta(book);
	}
}