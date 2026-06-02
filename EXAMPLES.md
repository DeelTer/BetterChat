# BetterChat Examples

Practical examples demonstrating URL parsing, ChatData manipulation, and custom processor development.

## Table of Contents

1. [URL Parsing Examples](#url-parsing-examples)
2. [ChatData Usage Examples](#chatdata-usage-examples)
3. [Custom Processor Examples](#custom-processor-examples)
4. [Replacer Examples](#replacer-examples)

---

## URL Parsing Examples

### Basic URL Detection

```java
import ru.deelter.chat.replacer.UrlMatcher;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// Test if text contains URLs
String text = "Check out github.com and https://stackoverflow.com!";
Pattern urlPattern = UrlMatcher.URL_PATTERN;
Matcher matcher = urlPattern.matcher(text);

if (matcher.find()) {
    System.out.println("Found URL: " + matcher.group());
    System.out.println("Scheme: " + matcher.group(1));
    System.out.println("Host: " + matcher.group(2));
}
```

**Output:**
```
Found URL: github.com
Scheme: null
Host: github.com
```

### Extracting Multiple URLs

```java
import ru.deelter.chat.replacer.UrlMatcher;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

String text = "Visit github.com, youtube.com, and https://stackoverflow.com for help";
List<String> urls = new ArrayList<>();
Matcher matcher = UrlMatcher.URL_PATTERN.matcher(text);

while (matcher.find()) {
    urls.add(matcher.group());
}

urls.forEach(System.out::println);
// Output: github.com, youtube.com, stackoverflow.com
```

### URL Validation with TLD Check

```java
import ru.deelter.chat.replacer.UrlMatcher;

// Check if bare domain (no scheme) has known TLD
String bareUrl = "example.com";
boolean hasKnownTld = UrlMatcher.hasKnownTld(bareUrl);
System.out.println("Valid: " + hasKnownTld); // true

// Invalid TLD won't be linked
String invalidUrl = "user@notreal"; // Not a URL
boolean isValid = UrlMatcher.hasKnownTld(invalidUrl);
System.out.println("Valid: " + isValid); // false
```

### Handling Trailing Punctuation

```java
import ru.deelter.chat.replacer.UrlMatcher;

// URLs often end with punctuation that shouldn't be part of the link
String fullMatch = "Check out github.com!";
int trailing = UrlMatcher.trailingPunctCount(fullMatch);

if (trailing > 0) {
    String url = fullMatch.substring(0, fullMatch.length() - trailing);
    String punctuation = fullMatch.substring(fullMatch.length() - trailing);
    
    System.out.println("URL: " + url);          // github.com
    System.out.println("Punctuation: " + punctuation);  // !
}
```

### Converting to Href

```java
import ru.deelter.chat.replacer.UrlMatcher;

// Get proper href for ClickEvent.openUrl()
String scheme = null;
String url = "github.com";
String href = UrlMatcher.toHref(scheme, url); // https://github.com

scheme = "http";
String url2 = "example.com";
String href2 = UrlMatcher.toHref(scheme, url2); // http://example.com

// Already has scheme
scheme = "https";
String href3 = UrlMatcher.toHref(scheme, "example.com"); // https://example.com
```

### Creating URL Links Programmatically

```java
import ru.deelter.chat.replacer.ChatLink;
import ru.deelter.chat.replacer.UrlMatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

String url = "github.com";

// Get styling for this URL
ChatLink link = ChatLink.getLinkByUrl(url);

// Create clickable link component
Component linkComponent = Component.text(url)
    .color(link.color())
    .clickEvent(ClickEvent.openUrl(UrlMatcher.toHref(null, url)))
    .hoverEvent(HoverEvent.showText(Component.text("Click to open")));

System.out.println("Link created with color: " + link.color());
```

### Processing URLs in Chat Messages

```java
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.replacer.URLReplacerProcessor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public void processMessageWithUrls(Player player, String text) {
    // Create ChatData from player
    ChatData data = ChatData.builder()
        .entity(player)
        .text(Component.text(text))
        .format("<sender>: <message>")
        .build();
    
    // Manually process with URL replacer
    URLReplacerProcessor processor = new URLReplacerProcessor(10);
    processor.process(data);
    
    // URLs are now linked in data.getText()
    System.out.println("Processed: " + data.getText());
}
```

---

## ChatData Usage Examples

### Creating ChatData from Different Sources

```java
import ru.deelter.chat.utils.ChatData;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import net.kyori.adventure.text.Component;

// From AsyncChatEvent
AsyncChatEvent event = /* received event */;
ChatData data1 = ChatData.fromAsyncEvent(event);

// From Player
Player player = /* some player */;
ChatData data2 = ChatData.fromLivingEntity(player);

// From any LivingEntity
LivingEntity mob = /* some mob */;
ChatData data3 = ChatData.fromLivingEntity(mob);

// From generic Entity
org.bukkit.entity.Entity entity = /* some entity */;
ChatData data4 = ChatData.fromEntity(entity);

// Completely manual builder
ChatData data5 = ChatData.builder()
    .entity(player)
    .text(Component.text("Hello"))
    .location(player.getLocation())
    .format("[<sender>] <message>")
    .build();
```

### Setting Message Format and Colors

```java
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

Player player = /* some player */;
ChatData data = ChatData.fromLivingEntity(player);

// Set format string (MiniMessage syntax)
data.setFormat("<color1>[<sender>]</color1> <color2><message></color2>");

// Set colors with hex strings
data.setColors("#FF5555", "#55FF55");

// Or set TextColor objects directly
data.setColor(TextColor.color(255, 85, 85));
data.setColor2(TextColor.color(85, 255, 85));

System.out.println("Format: " + data.getFormat());
System.out.println("Color1: " + data.getColor());
```

### Working with Prefix and Suffix

```java
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

Player admin = /* some admin player */;
ChatData data = ChatData.fromLivingEntity(admin);

// Add prefix and suffix
data.setPrefix(Component.text("[Admin] ", NamedTextColor.RED));
data.setSuffix(Component.text(" ⭐", NamedTextColor.GOLD));

// Format string will use {prefix} and {suffix} placeholders
data.setFormat("<prefix><sender><suffix>: <message>");

// Result: [Admin] AdminName ⭐: Hello everyone
```

### Controlling Message Radius and Audience

```java
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.entity.Player;

Player speaker = /* some player */;
ChatData data = ChatData.fromLivingEntity(speaker);

// Set broadcast radius (blocks)
data.setRadius(50);  // Only players within 50 blocks hear this

// Create audience from location and radius
data.createAudience();

// Remove audience members outside radius
data.stripAudienceByRadius();

System.out.println("Audience size: " + data.getAudiences().size());

// Send to filtered audience
data.send();

// Or send to custom audience
Set<Audience> admins = /* custom audience */;
data.send(admins);
```

### Processing Player State

```java
import ru.deelter.chat.utils.ChatData;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffectType;

Player player = /* some player */;
ChatData data = ChatData.fromLivingEntity(player);

// Check player state flags (automatically set)
if (data.isInvisible()) {
    System.out.println("Player is invisible!");
}

if (data.isUnderwater()) {
    System.out.println("Player is underwater!");
}

if (data.isDarkness()) {
    System.out.println("Player is in darkness!");
}

if (data.isDeepSleeping()) {
    System.out.println("Player is deep sleeping!");
}

if (data.isSpectating()) {
    System.out.println("Player is in spectator mode!");
}
```

### Language and Localization

```java
import ru.deelter.chat.utils.ChatData;
import org.bukkit.entity.Player;
import java.util.Locale;

Player player = /* some player */;
ChatData data = ChatData.fromLivingEntity(player);

// Get player's language locale
Locale locale = data.getLocale();
System.out.println("Player language: " + locale.getLanguage());

// Change locale
data.setLocale(Locale.forLanguageTag("ru"));

// Send with automatic translation
data.send();  // Message will be translated for each recipient
```

### Complete Chat Pipeline Example

```java
import ru.deelter.chat.utils.ChatData;
import ru.deelter.chat.bukkit.BetterChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public void handlePlayerChat(Player player, String message) {
    // Step 1: Create ChatData from player
    ChatData data = ChatData.fromLivingEntity(player);
    data.setText(Component.text(message));
    
    // Step 2: Add custom formatting
    if (player.isOp()) {
        data.setPrefix(Component.text("[OP] ", NamedTextColor.RED));
    }
    
    // Step 3: Set message format
    data.setFormat("<prefix><color1><sender></color1>: <color2><message></color2>");
    
    // Step 4: Create audience
    data.createAudience();
    
    // Step 5: Process through global registry
    data.process();
    
    // Step 6: Send bubbles if configured
    data.sendBubbles();
    
    // Step 7: Send to audience
    data.send();
    
    System.out.println("Message sent to " + data.getAudiences().size() + " players");
}
```

---

## Custom Processor Examples

### Simple Uppercase Processor

```java
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class UppercaseProcessor extends AbstractChatProcessor {
    
    public UppercaseProcessor(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        Component text = data.getText();
        String content = text.content();
        data.setText(Component.text(content.toUpperCase()));
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return true;  // Process all messages
    }
}

// Register in your plugin
BetterChat.getInstance().getManager().register(new UppercaseProcessor(50));
```

### Effect-Based Processor (Darkness Echo)

```java
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public class DarknessEchoProcessor extends AbstractChatProcessor {
    
    public DarknessEchoProcessor(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        if (data.isDarkness()) {
            // Darken the text color
            data.setColor(TextColor.color(100, 100, 100));
            data.setColor2(TextColor.color(150, 150, 150));
            
            // Add visual indicator
            Component text = data.getText();
            text = Component.text("[dark] ", TextColor.color(80, 80, 80)).append(text);
            data.setText(text);
        }
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return data.isDarkness();  // Only process if in darkness
    }
}
```

### Radius-Based Processor

```java
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RoleBasedRadiusProcessor extends AbstractChatProcessor {
    
    public RoleBasedRadiusProcessor(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        if (data.getEntity() instanceof Player player) {
            if (player.isOp()) {
                data.setRadius(-1);  // Ops broadcast globally
            } else if (player.hasPermission("betterchat.trusted")) {
                data.setRadius(100);  // Trusted players: 100 block radius
            } else {
                data.setRadius(50);   // Regular players: 50 block radius
            }
        }
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return data.getEntity() instanceof Player;
    }
}
```

### Filtering Processor with Early Termination

```java
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

public class ForbiddenWordsProcessor extends AbstractChatProcessor {
    
    private static final List<String> FORBIDDEN = Arrays.asList(
        "badword1", "badword2", "offensive"
    );
    
    public ForbiddenWordsProcessor(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        String text = data.getText().content().toLowerCase();
        
        for (String forbidden : FORBIDDEN) {
            if (text.contains(forbidden)) {
                // Block the message entirely
                data.getAudiences().clear();
                data.setTerminated(true);
                
                // Notify player
                if (data.getEntity() instanceof Player player) {
                    player.sendMessage("Your message contains forbidden content!");
                }
                return;
            }
        }
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return true;
    }
}
```

### Conditional Format Processor

```java
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GamemodeFormatProcessor extends AbstractChatProcessor {
    
    public GamemodeFormatProcessor(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        if (!(data.getEntity() instanceof Player player)) return;
        
        String format = switch (player.getGameMode()) {
            case CREATIVE -> "[<color:#FF00FF>C</color>] <sender>: <message>";
            case SPECTATOR -> "[<color:#808080>S</color>] <sender>: <message>";
            case ADVENTURE -> "[<color:#00FF00>A</color>] <sender>: <message>";
            default -> data.getFormat();  // Keep original
        };
        
        data.setFormat(format);
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return data.getEntity() instanceof Player;
    }
}
```

---

## Replacer Examples

### Custom Emoji Replacer

```java
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Pattern;

public class EmojiReplacer extends AbstractReplacerProcessor {
    
    static final Pattern EMOJI_PATTERN = Pattern.compile(
        ":(smile|laugh|sad|heart|fire):"
    );
    
    public EmojiReplacer(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        TextReplacementConfig replacer = TextReplacementConfig.builder()
            .match(EMOJI_PATTERN)
            .replacement((result, builder) -> {
                String emoji = switch (result.group(1)) {
                    case "smile" -> "😊";
                    case "laugh" -> "😂";
                    case "sad" -> "😢";
                    case "heart" -> "❤️";
                    case "fire" -> "🔥";
                    default -> "";
                };
                return builder.content(emoji);
            })
            .build();
        
        data.setText(data.getText().replaceText(replacer));
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return true;
    }
}
```

### Spoiler Tag Replacer

```java
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Pattern;

public class SpoilerReplacer extends AbstractReplacerProcessor {
    
    static final Pattern SPOILER_PATTERN = Pattern.compile("\\[spoiler:(.+?)\\]");
    
    public SpoilerReplacer(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        TextReplacementConfig replacer = TextReplacementConfig.builder()
            .match(SPOILER_PATTERN)
            .replacement((result, builder) -> {
                String spoilerText = result.group(1);
                return Component.text("[SPOILER]")
                    .color(TextColor.color(100, 100, 100))
                    .hoverEvent(HoverEvent.showText(
                        Component.text(spoilerText, TextColor.WHITE)
                    ));
            })
            .build();
        
        data.setText(data.getText().replaceText(replacer));
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return true;
    }
}
```

### Custom Wiki Link Replacer

```java
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Pattern;

public class WikiLinkReplacer extends AbstractReplacerProcessor {
    
    // [wiki:topic] — links to wiki
    static final Pattern WIKI_PATTERN = Pattern.compile("\\[wiki:([^\\]]+)\\]");
    private static final String WIKI_URL = "https://wiki.example.com/";
    
    public WikiLinkReplacer(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        TextReplacementConfig replacer = TextReplacementConfig.builder()
            .match(WIKI_PATTERN)
            .replacement((result, builder) -> {
                String topic = result.group(1);
                String url = WIKI_URL + topic.replace(" ", "_");
                
                return Component.text("📖 " + topic)
                    .color(TextColor.color(100, 149, 237))  // Cornflower blue
                    .clickEvent(ClickEvent.openUrl(url))
                    .hoverEvent(HoverEvent.showText(
                        Component.text("Click to view wiki: " + topic)
                    ));
            })
            .build();
        
        data.setText(data.getText().replaceText(replacer));
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return true;
    }
}
```

### Player Mention Replacer with Tagging

```java
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Pattern;

public class MentionTagReplacer extends AbstractReplacerProcessor {
    
    // @playername or @"player name"
    static final Pattern MENTION_PATTERN = Pattern.compile("@([a-zA-Z0-9_]+|\"[^\"]+\")");
    
    public MentionTagReplacer(int priority) {
        super(priority);
    }
    
    @Override
    public void process(@NotNull ChatData data) {
        TextReplacementConfig replacer = TextReplacementConfig.builder()
            .match(MENTION_PATTERN)
            .replacement((result, builder) -> {
                String playerName = result.group(1).replaceAll("[\"\\s]", "");
                Player target = Bukkit.getPlayer(playerName);
                
                if (target == null) {
                    return builder;  // Not found, leave as-is
                }
                
                return Component.text("@" + target.getName())
                    .color(NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(
                        Component.text("Mentioned: " + target.getName())
                            .color(NamedTextColor.YELLOW)
                    ));
            })
            .build();
        
        data.setText(data.getText().replaceText(replacer));
    }
    
    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return true;
    }
}
```

---

## Integration Examples

### Plugin Using BetterChat API

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listeners;
import io.papermc.paper.event.player.AsyncChatEvent;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;

public class MyPlugin extends JavaPlugin implements Listeners {
    
    private BetterChat betterChat;
    
    @Override
    public void onEnable() {
        betterChat = (BetterChat) Bukkit.getPluginManager().getPlugin("BetterChat");
        if (betterChat == null) {
            getLogger().severe("BetterChat not found!");
            return;
        }
        
        // Register our custom processor
        betterChat.getManager().register(new MyCustomProcessor(500));
        
        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        // You can intercept and modify ChatData here if needed
        // But usually just register processors instead
    }
}
```

### Modifying Existing Messages

```java
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public void appendMessageSuffix(ChatData data, String suffix) {
    Component text = data.getText();
    text = text.append(Component.text(" " + suffix));
    data.setText(text);
}

public void prependMessagePrefix(ChatData data, String prefix) {
    Component text = data.getText();
    text = Component.text(prefix + " ").append(text);
    data.setText(text);
}

public void wrapMessage(ChatData data, String before, String after) {
    Component text = Component.text(before)
        .append(data.getText())
        .append(Component.text(after));
    data.setText(text);
}
```

---

## Common Patterns

### Checking Player Permissions in Processor

```java
@Override
public boolean canProcess(@NotNull ChatData data) {
    if (!(data.getEntity() instanceof Player player)) {
        return false;
    }
    return player.hasPermission("myplugin.special.chat");
}
```

### Debugging Chat Data

```java
public void debugChatData(ChatData data) {
    getLogger().info("=== ChatData Debug ===");
    getLogger().info("Text: " + data.getText().content());
    getLogger().info("Format: " + data.getFormat());
    getLogger().info("Radius: " + data.getRadius());
    getLogger().info("Audiences: " + data.getAudiences().size());
    getLogger().info("Entity: " + data.getEntity());
    getLogger().info("Location: " + data.getLocation());
    getLogger().info("Invisible: " + data.isInvisible());
    getLogger().info("Underwater: " + data.isUnderwater());
    getLogger().info("Darkness: " + data.isDarkness());
    getLogger().info("Language: " + data.getLocale().getLanguage());
}
```

### Safe Type Casting

```java
@Override
public void process(@NotNull ChatData data) {
    if (data.getEntity() instanceof Player player) {
        // Use player safely
        player.sendMessage(Component.text("Debug info"));
    } else if (data.getEntity() instanceof LivingEntity living) {
        // Use living entity
    }
}
```

---

## Testing Tips

1. **Local Testing**: Use `/chat [message]` command to test with full replacer support
2. **Debug Output**: Add `Bukkit.getLogger().info()` calls to processors
3. **Console Inspection**: Watch for exceptions in console
4. **Player Messages**: Use `player.sendMessage()` to feedback to players
5. **Component Inspection**: Print component content with `.content()` method

