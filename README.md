# 🗨️ BetterChat

**Flexible chat system with modifiers, bubbles, and auto‑translation for Paper servers.**

[![Paper](https://img.shields.io/badge/Paper-1.21.3+-blue)](https://papermc.io)
[![Velocity](https://img.shields.io/badge/Velocity-Supported-green)](https://velocitypowered.com)
[![bStats](https://img.shields.io/badge/bStats-Enabled-brightgreen)](https://bstats.org/plugin/bukkit/deelter-betterchat/31258)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

BetterChat transforms your server's chat with contextual formatting, animated text bubbles, smart anti‑spam, cross‑server global messages and more — all fully configurable via `config.yml` and localisation files.

---

## ✨ Features

- **Dynamic chat formatting** — colours and templates change depending on player state: underwater, darkness, invisibility, cave, spectator, etc.
- **Auto‑translation** — players who speak different languages automatically see translated messages (original shown on hover). Supports all languages available in Google Translate.
- **Chat bubbles** — text displays pop up above players with configurable pop‑up animation, billboard style, shadow, and performance limits.
- **Tags** — prefix symbols (`!global`, `$trade`, `!g`, etc.) that alter the message radius and format, or send the message across the Velocity network.
- **Inline replacers** — use `[copy:text]`, `[hide:secret]`, `[cmd:/command]` and `[link:label:url]` to add interactive elements.
- **Mentions** — highlight `@player` names and play a ping sound for the mentioned player (permission‑based opt‑out).
- **Anti‑spam** — blocks messages that are too similar to recent ones (Jaro–Winkler similarity), configurable threshold and bypass permission.
- **Empty audience feedback** — tells the player when no one is in range to hear them (action bar message).
- **Cross‑server chat (Velocity)** — global tags can send messages to other servers on the proxy, with optional whitelist/blacklist filtering.
- **WorldGuard add‑on** (optional) — external plugin `DWGFlags` can register additional flags (`chat‑radius`, `chat‑format`, `chat‑zone‑name`) that override chat settings per region.
- **bStats metrics** — anonymous usage statistics help us understand how the plugin is used and improve it. You can disable them in the config.
- **Multi‑language** — all plugin messages are localised; default English and Russian included, easy to add more.

---

## 📥 Installation

1. **Download** the latest `BetterChat-<version>.jar` from [releases](https://github.com/your-repo/releases).
2. Place it into the `plugins/` folder of your **Paper 1.21.4+** server.
3. **For Velocity cross‑server chat**, place the **same jar** into the `plugins/` folder of your Velocity proxy as well.
4. Restart the server (or proxy). The default configuration will be created automatically.

---

## 🚀 Quick start

- Edit `plugins/BetterChat/config.yml` to your liking. Test MiniMessage formats at [webui.advntr.dev](https://webui.advntr.dev/).
- Add your own language files in `plugins/BetterChat/lang/` (e.g. `de.yml`, `fr.yml`).
- Use `/lang <code>` to change your preferred chat language.
- Type a message with a tag (e.g. `!hello world`) to see tag formatting.
- To enable cross‑server chat, set `velocity.enabled: true` and configure the global tags.

---

## ⚙️ Configuration highlights

```yaml
language:
  default: "en"
  auto-detect: true

translation:
  enabled: true

bubbles:
  animation:
    pop-up:
      enabled: true
      start-scale: 0.015
      duration-ticks: 3

chat:
  formats:
    default: "<prefix><color1><sender><suffix>:</color1> <color2><message></color2>"
    cave: "<color:#D0CAA5><sender>:</color> <color:#EFEEE6><message></color>"

mentions:
  enabled: true
  color: "#FFD700"
  sound: "block.note_block.bell"

anti-spam:
  enabled: true
  similarity-threshold: 0.9
  recent-messages-count: 5
```
## 📟 Commands

| Command | Description |
|--------|-------------|
| `/chat <text>` | Send a chat message with full tag and replacer support |
| `/lang <code>` | Set your preferred language (e.g. `en`, `ru`, `de`) |

## 🔐 Permissions

| Permission | Description |
|-----------|-------------|
| `betterchat.use` | Allows using all chat modifiers (default: everyone) |
| `betterchat.antispam.bypass` | Bypass the anti‑spam similarity check |
| `betterchat.mention.bypass` | Disable receiving mention ping sounds |

## 🌐 Velocity integration

BetterChat can function as a Velocity plugin **in the same jar**.  
When `velocity.enabled: true` is set in `config.yml`, the plugin registers a channel `betterchat:global` and forwards messages from global tags to other servers. You can restrict destinations per tag using `whitelist` or `blacklist` mode.

Example tag configuration:
```yaml
tags:
  global:
    symbol: "!"
    radius: -1
    format: "<color:#525252>[G]</color> <prefix><color1><sender><suffix>:</color1> <color2><message></color2>"
    global: true
  global_bw:
    symbol: "!gb"
    global:
      servers:
        - lobby
        - bedwars
```
Velocity‑side filtering is also available via the `velocity.server-filter` section.

## 📊 bStats

We collect anonymous statistics to understand how BetterChat is being used. You can view them at  
[https://bstats.org/plugin/bukkit/deelter-betterchat/31258](https://bstats.org/plugin/bukkit/deelter-betterchat/31258)  
To disable, set `metrics.enabled: false` in the config.

## 🔌 API Guide for Developers

BetterChat provides a flexible API for extending chat functionality through **Processors** and **Replacers**. Both inherit from `AbstractChatProcessor` and operate on `ChatData` objects to modify messages.

### Architecture Overview

All chat processing happens through the `ChatProcessorRegistry`:
1. Message arrives → wrapped in `ChatData` object
2. Registry iterates through processors (sorted by priority, lowest first)
3. Each processor checks `canProcess()` → if true, calls `process()`
4. Message continues until `data.isTerminated()` returns true

### Creating a Custom Processor

**Step 1: Extend `AbstractChatProcessor`**

```java
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import org.jetbrains.annotations.NotNull;

public class MyCustomProcessor extends AbstractChatProcessor {

    public MyCustomProcessor(int priority) {
        super(priority);
    }

    @Override
    public void process(@NotNull ChatData data) {
        // Modify the ChatData object
        // data.setText() - change message content
        // data.setFormat() - change message format
        // data.setAudiences() - modify who sees it
        // data.setRadius() - change broadcast radius
        // data.setColors() - customize colors
    }

    @Override
    public boolean canProcess(@NotNull ChatData data) {
        // Return true if this processor should run for this message
        // Example: check player gamemode, location, effects, etc.
        return true;
    }
}
```

**Step 2: Understand ChatData**

`ChatData` is a mutable object passed through the processor chain. Key properties:

| Property | Type | Purpose |
|----------|------|---------|
| `text` | `Component` | Message content (Adventure Component) |
| `format` | `String` | MiniMessage format string with placeholders |
| `audiences` | `Set<Audience>` | Players who see the message |
| `radius` | `double` | Chat radius in blocks (≤0 = global) |
| `entity` | `Entity` | Sending player/entity |
| `location` | `Location` | Entity location |
| `prefix` / `suffix` | `Component` | Text before/after sender name |
| `color` / `color2` | `TextColor` | Colors for format placeholders |
| `darkness` / `invisible` / `underwater` | `boolean` | Player state flags |
| `locale` | `Locale` | Player's language preference |
| `terminated` | `boolean` | Set to `true` to stop further processing |
| `matchedTag` | `ChatTag` | The tag that matched (if any) |

**Example: Uppercase Processor**

```java
import net.kyori.adventure.text.Component;

public class UppercaseProcessor extends AbstractChatProcessor {

    public UppercaseProcessor(int priority) {
        super(priority);
    }

    @Override
    public void process(@NotNull ChatData data) {
        String content = data.getText().content();
        data.setText(Component.text(content.toUpperCase()));
    }

    @Override
    public boolean canProcess(@NotNull ChatData data) {
        // Only process messages from players in creative mode
        return data.getEntity() instanceof Player player 
            && player.getGameMode() == GameMode.CREATIVE;
    }
}
```

### Creating a Custom Replacer (Inline Replacers)

**Step 1: Extend `AbstractReplacerProcessor`**

Replacers are specialized processors that use regex to find and replace patterns within messages. They automatically get the `ProcessorTag.MARKER` tag.

```java
import ru.deelter.chat.replacer.AbstractReplacerProcessor;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.Component;
import java.util.regex.Pattern;

public class MyReplacer extends AbstractReplacerProcessor {

    private static final Pattern PATTERN = Pattern.compile("\\[(custom:([^\\]]+))\\]");

    public MyReplacer(int priority) {
        super(priority);  // Priority should be high, typically 99990+
    }

    @Override
    public void process(@NotNull ChatData data) {
        TextReplacementConfig replacer = TextReplacementConfig.builder()
            .match(PATTERN)
            .replacement((result, builder) -> {
                String value = result.group(1);
                // Return modified component
                return Component.text("→ " + value + " ←", TextColor.color(255, 100, 100));
            })
            .build();
        
        data.setText(data.getText().replaceText(replacer));
    }

    @Override
    public boolean canProcess(@NotNull ChatData data) {
        return true;  // Process all messages
    }
}
```

**Built-in Replacer Patterns:**

| Syntax | Purpose | Example |
|--------|---------|---------|
| `[copy:text]` | Copies text to clipboard on click | `[copy:server.ip]` |
| `[hide:secret]` | Hidden text, visible only on hover | `[hide:password123]` |
| `[cmd:/command]` | Execute command on click | `[cmd:/tpa Player]` |
| `[link:label:url]` | Clickable link | `[link:Click here:https://example.com]` |
| `[chat:label:text]` | Run `/chat` command on click | `[chat:Say Hi:Hello world]` |

### Registering Processors/Replacers

**Method 1: Direct Registration**

```java
ChatProcessorRegistry registry = BetterChat.getInstance().getManager();

// Simple registration
registry.register(new MyCustomProcessor(500));

// Register multiple
registry.register(Arrays.asList(
    new MyCustomProcessor(500),
    new AnotherProcessor(600)
));
```

**Method 2: Conditional Registration (respects disabled list)**

```java
ChatProcessorRegistry registry = BetterChat.getInstance().getManager();

// Only registers if "my_processor" is not in config disabled-processors list
registry.registerIfEnabled("my_processor", () -> new MyCustomProcessor(500));
```

**Method 3: In config.yml (built-in processors)**

Built-in processors can be disabled without removing code. Add to `config.yml`:

```yaml
disabled-processors:
  - anti_spam           # Disables AntiSpamProcessor
  - tag                 # Disables TagChatProcessor
  - confusion           # Disables EntityConfusionChatProcessor
```

### Priority System

Processors execute in **ascending priority order** (lowest number first). Use these ranges:

| Range | Purpose | Examples |
|-------|---------|----------|
| 1–99 | Preprocessing, validation | AntiSpam (1) |
| 100–199 | Effect processors | Entity effects (100-180) |
| 200–299 | Tag/format processors | Tags (220), Mentions (200) |
| 99990+ | Inline replacers (markers) | All replacers (99991-99996) |
| MAX_INT | Cleanup/finalizers | EmptyAudienceProcessor |

**Why priority matters:**
- Lowest executes first → can preprocess/validate
- Highest executes last → can clean up/finalize
- Replacers run last so they see fully-formatted text

### Processor Tags

Tags categorize processors for selective execution:

```java
import ru.deelter.chat.processors.ProcessorTag;

public class TaggedProcessor extends AbstractChatProcessor {
    @Override
    public Set<ProcessorTag> getTags() {
        return new HashSet<>(Arrays.asList(
            ProcessorTag.ENTITY,      // Affects entity/player state
            ProcessorTag.LOCATION     // Uses location data
        ));
    }
}
```

**Available Tags:**
- `CHAT` – modifies chat content
- `ENTITY` – uses entity data
- `LOCATION` – uses location data
- `EFFECT` – applies effects
- `MARKER` – inline replacers (auto-set by `AbstractReplacerProcessor`)
- `OTHER` – miscellaneous

### Complete Example: Custom Processor Plugin

Create a separate plugin that hooks into BetterChat:

```java
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import net.kyori.adventure.text.Component;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!isPluginLoaded("BetterChat")) {
            getLogger().severe("BetterChat not found!");
            return;
        }

        // Register custom processor
        BetterChat betterChat = (BetterChat) getServer().getPluginManager().getPlugin("BetterChat");
        betterChat.getManager().register(new PrefixProcessor(50));
    }

    private boolean isPluginLoaded(String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    public static class PrefixProcessor extends AbstractChatProcessor {
        public PrefixProcessor(int priority) {
            super(priority);
        }

        @Override
        public void process(ChatData data) {
            Component prefix = Component.text("[MyPlugin] ", NamedTextColor.GOLD);
            data.setText(Component.empty().append(prefix).append(data.getText()));
        }

        @Override
        public boolean canProcess(ChatData data) {
            return true;
        }
    }
}
```

### Testing Your Processor

1. Build the plugin
2. Place JAR in `plugins/`
3. Register processor in `onEnable()`
4. Send a chat message
5. Check that the processor ran (use `Bukkit.getLogger().info()` to debug)

### API Stability

- `AbstractChatProcessor` ✅ Stable
- `ChatProcessorRegistry` ✅ Stable
- `ChatData` ✅ Stable (builder pattern)
- `ProcessorTag` ✅ Stable
- `AbstractReplacerProcessor` ✅ Stable

---

## 📄 License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
