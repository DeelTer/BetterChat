package ru.deelter.chat.model;

import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.regex.Pattern;

@Data
@Builder
public class ChatTag {


	private String id;
	private String symbol;
	private String format;
	private double radius;
	private TextReplacementConfig replacementConfig;


	public TextReplacementConfig getReplacementConfig() {
		if (replacementConfig == null) {
			this.replacementConfig = TextReplacementConfig.builder()
					.match(Pattern.compile("^" + Pattern.quote(symbol)))
					.replacement((result, builder) -> Component.empty()).build();
		}
		return replacementConfig;
	}
}
