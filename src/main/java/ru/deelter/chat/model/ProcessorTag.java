package ru.deelter.chat.model;

public record ProcessorTag(String id) {

	public static final ProcessorTag CHAT = new ProcessorTag("chat");
	public static final ProcessorTag ENTITY = new ProcessorTag("entity");
	public static final ProcessorTag LOCATION = new ProcessorTag("location");
	public static final ProcessorTag EFFECT = new ProcessorTag("effect");
	public static final ProcessorTag MARKER = new ProcessorTag("marker");
	public static final ProcessorTag OTHER = new ProcessorTag("other");
}
