package ru.deelter.chat.processors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.utils.ChatData;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractChatProcessor {

	private static final Set<ProcessorTag> EMPTY_SET = new HashSet<>();
	private int priority = Integer.MAX_VALUE;

	public abstract void process(@NotNull ChatData data);

	public abstract boolean canProcess(@NotNull ChatData data);

	public Set<ProcessorTag> getTags() {
		return EMPTY_SET;
	}
}