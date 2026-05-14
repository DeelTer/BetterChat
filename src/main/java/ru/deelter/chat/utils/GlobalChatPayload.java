package ru.deelter.chat.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalChatPayload {

	public static final Gson GSON = GsonComponentSerializer.gson().populator()
			.apply(new GsonBuilder())
			.create();

	private String locale;
	private String format;
	private String color1;
	private String color2;
	private Component prefix;
	private Component suffix;
	private Component sender;
	private Component text;

	public static @NotNull GlobalChatPayload from(@NotNull ChatData data) {
		return new GlobalChatPayload(
				data.getLocale() != null ? data.getLocale().toLanguageTag() : "en",
				data.getFormat() != null ? data.getFormat() : "",
				data.getColor() != null ? data.getColor().asHexString() : "#ffffff",
				data.getColor2() != null ? data.getColor2().asHexString() : "#ffffff",
				data.getPrefix() != null ? data.getPrefix() : Component.empty(),
				data.getSuffix() != null ? data.getSuffix() : Component.empty(),
				data.getName() != null ? data.getName() : Component.empty(),
				data.getText() != null ? data.getText() : Component.empty()
		);
	}

	public @NotNull String toJson() {
		return GSON.toJson(this);
	}

	public static @NotNull GlobalChatPayload fromJson(@NotNull String json) {
		return GSON.fromJson(json, GlobalChatPayload.class);
	}
}
