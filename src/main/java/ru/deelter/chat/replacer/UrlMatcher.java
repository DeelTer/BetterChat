package ru.deelter.chat.replacer;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Robust URL detection helper.
 * <p>
 * A bare domain (no scheme) is only treated as a link if its TLD is in
 * {@link #COMMON_TLDS}. This prevents false positives like {@code file.txt},
 * {@code e.g.}, {@code config.yml}. Anything with an explicit
 * {@code http://}/{@code https://} scheme is always treated as a link.
 */
@UtilityClass
public class UrlMatcher {

    /**
     * Groups:
     * 1 = scheme ("http://" / "https://") or null
     * 2 = host (domain, no scheme/port/path)
     * 3 = port (":8080") or null
     * 4 = path+query+fragment ("/a/b?x=1#y") or null
     */
    public static final Pattern URL_PATTERN = Pattern.compile(
            "(https?://)?" +
                    "((?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z]{2,24})" +
                    "(:\\d{1,5})?" +
                    "(/[^\\s<>\"']*)?",
            Pattern.CASE_INSENSITIVE
    );

    /** Characters trimmed from the end of a match (sentence punctuation, closing brackets). */
    private static final String TRAILING_PUNCT = ".,!?;:…)»]}>\"'";

    /** Curated common TLDs. Bare domains only become links if their TLD is here. */
    private static final Set<String> COMMON_TLDS = Set.of(
            // gTLDs
            "com", "net", "org", "info", "biz", "name", "pro",
            "dev", "app", "xyz", "online", "site", "shop", "store", "club",
            "tech", "space", "live", "fun", "world", "wiki", "blog", "io", "co",
            "gg", "tv", "fm", "me", "to", "ly", "cc", "gl", "im", "sh", "ws",
            "moe", "lol", "win", "vip", "art", "games", "game",
            // ccTLDs (common in CIS + international)
            "ru", "su", "by", "ua", "kz", "uz", "am", "ge", "az", "md", "tj", "kg", "tm",
            "us", "uk", "de", "fr", "es", "it", "nl", "pl", "se", "no", "fi", "dk",
            "cn", "jp", "kr", "in", "br", "ca", "au", "ch", "at", "cz", "pt", "gr",
            "tr", "ir", "il", "ae", "sa", "eu", "be", "ie", "nz", "za", "mx", "ar"
    );

    /**
     * @return true if the host's TLD is a recognised common TLD (case-insensitive).
     */
    public static boolean hasKnownTld(@NotNull String host) {
        int dot = host.lastIndexOf('.');
        if (dot < 0 || dot == host.length() - 1) return false;
        return COMMON_TLDS.contains(host.substring(dot + 1).toLowerCase());
    }

    /**
     * @return number of trailing punctuation characters that should not be part of the link.
     */
    public static int trailingPunctCount(@NotNull String match) {
        int end = match.length();
        while (end > 0 && TRAILING_PUNCT.indexOf(match.charAt(end - 1)) >= 0) {
            end--;
        }
        return match.length() - end;
    }

    /**
     * Build a clickable href: keeps existing scheme, otherwise prefixes https://.
     */
    public static @NotNull String toHref(String scheme, @NotNull String urlWithoutTrailing) {
        return scheme != null ? urlWithoutTrailing : "https://" + urlWithoutTrailing;
    }
}
