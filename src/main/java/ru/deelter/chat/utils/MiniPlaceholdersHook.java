package ru.deelter.chat.utils;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class MiniPlaceholdersHook {

    private static boolean enabled = false;

    public static void init(boolean pluginLoaded) {
        enabled = pluginLoaded;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static TagResolver resolver() {
        if (!enabled) return TagResolver.empty();
        return MiniPlaceholders.audienceGlobalPlaceholders();
    }

    private MiniPlaceholdersHook() {}
}
