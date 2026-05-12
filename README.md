# 🗨️ BetterChat

**Flexible chat system with modifiers, bubbles, and auto‑translation for Paper servers.**

[![Paper](https://img.shields.io/badge/Paper-26.1.2+-blue)](https://papermc.io)
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

## 📄 License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
