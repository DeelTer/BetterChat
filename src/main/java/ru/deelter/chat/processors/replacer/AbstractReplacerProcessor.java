package ru.deelter.chat.processors.replacer;

import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.model.ProcessorTag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractReplacerProcessor extends AbstractChatProcessor {

	private static final Set<ProcessorTag> TAGS = new HashSet<>(Collections.singleton(ProcessorTag.MARKER));

	public AbstractReplacerProcessor(int priority) {
		super(priority);
	}

	@Override
	public Set<ProcessorTag> getTags() {
		return TAGS;
	}
}