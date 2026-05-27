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

	/** Unique sentinel marking where the translated message should be inserted in the frame. */
	public static final String SENTINEL = "BETTERCHAT_MSG";

	private String locale;
	private Component frame;
	private Component text;

	public @NotNull String toJson() {
		return GSON.toJson(this);
	}

	public static @NotNull GlobalChatPayload fromJson(@NotNull String json) {
		return GSON.fromJson(json, GlobalChatPayload.class);
	}
}
