package ru.deelter.chat.processors;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.processors.impl.*;
import ru.deelter.chat.replacer.impl.*;
import ru.deelter.chat.utils.ChatData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
public class ChatProcessorRegistry {

	private final List<AbstractChatProcessor> processors = new ArrayList<>();

	public void register(AbstractChatProcessor processor) {
		processors.add(processor);
		processors.sort(Comparator.comparingInt(AbstractChatProcessor::getPriority));
	}

	public void registerIfEnabled(String id, Supplier<AbstractChatProcessor> supplier) {
		FileConfiguration config = BetterChat.getInstance().getConfig();
		List<String> disabled = config.getStringList("disabled-processors");
		if (!disabled.contains(id)) {
			register(supplier.get());
		}
	}

	public void register(@NotNull Collection<AbstractChatProcessor> processors) {
		processors.forEach(this::register);
	}

	public List<AbstractChatProcessor> getProcessors(@NotNull ProcessorTag tag) {
		return processors.stream()
				.filter(processor -> processor.getTags().contains(tag))
				.collect(Collectors.toList());
	}

	public void load() {
		registerIfEnabled("anti_spam", () -> new AntiSpamProcessor(1));
		// Entity effect processors
		registerIfEnabled("confusion", () -> new EntityConfusionChatProcessor(100));
		registerIfEnabled("underwater", () -> new EntityUnderwaterChatProcessor(110));
		registerIfEnabled("strength", () -> new EntityStrengthChatProcessor(120));
		registerIfEnabled("weakness", () -> new EntityWeaknessChatProcessor(130));
		registerIfEnabled("scale", () -> new EntityScaleChatProcessor(140));
		registerIfEnabled("cave", () -> new EntityCaveChatProcessor(150));
		registerIfEnabled("invisibility", () -> new EntityInvisibilityChatProcessor(160));
		registerIfEnabled("darkness", () -> new EntityDarknessChatProcessor(170));
		registerIfEnabled("spectate", () -> new EntitySpectateChatProcessor(180));
		registerIfEnabled("tag", () -> new TagChatProcessor(220));
		registerIfEnabled("global_chat", () -> new GlobalChatProcessor(230));
		registerIfEnabled("empty_audience", () -> new EmptyAudienceProcessor(Integer.MAX_VALUE));

		// Replacer processors
		registerIfEnabled("url_label", () -> new URLLabelReplacerProcessor(99991));
		registerIfEnabled("url", () -> new URLReplacerProcessor(99992));
		registerIfEnabled("copy", () -> new CopyReplacerProcessor(99993));
		registerIfEnabled("hide", () -> new HideReplacerProcessor(99994));
		registerIfEnabled("command", () -> new CommandReplacerProcessor(99995));
		registerIfEnabled("chat", () -> new ChatReplacerProcessor(99996));
		registerIfEnabled("item", () -> new ItemReplacerProcessor(99997));
		registerIfEnabled("coords", () -> new CoordsReplacerProcessor(99998));
		registerIfEnabled("health", () -> new HealthReplacerProcessor(99999));
		registerIfEnabled("hunger", () -> new HungerReplacerProcessor(100000));
		registerIfEnabled("armor", () -> new ArmorReplacerProcessor(100001));
		registerIfEnabled("level", () -> new LevelReplacerProcessor(100002));
		registerIfEnabled("biome", () -> new BiomeReplacerProcessor(100003));
		registerIfEnabled("mention", () -> new MentionProcessor(200));
	}

	public void process(ChatData data) {
		for (AbstractChatProcessor processor : processors) {
			if (data.isTerminated()) break;
			if (processor.canProcess(data)) {
				processor.process(data);
			}
		}
	}

	public void unload() {
		processors.clear();
	}
}