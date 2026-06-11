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

    /** Global placeholders only (no audience context). */
    public static TagResolver relationalResolver() {
        if (!enabled) return TagResolver.empty();
        return MiniPlaceholders.relationalGlobalPlaceholders();
    }

    /** Global + audience-specific placeholders (e.g. <player_name>). Pass alongside the audience to MiniMessage. */
    public static TagResolver audienceResolver() {
        if (!enabled) return TagResolver.empty();
        return MiniPlaceholders.audienceGlobalPlaceholders();
    }

    private MiniPlaceholdersHook() {}
}
