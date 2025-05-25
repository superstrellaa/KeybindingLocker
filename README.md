# 🔒 KeybindingLocker

**KeybindingLocker** is a minimal Fabric mod that allows server administrators to remotely **lock specific keybind actions** for all connected players.  
For example, you can prevent players from opening chat, inventory, or using specific controls during events or custom gameplay scenarios.

## ✅ Supported Versions

- Minecraft 1.20.1
- Minecraft 1.20.4

## 📦 Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) and [Fabric API](https://modrinth.com/mod/fabric-api).
2. Download the mod `.jar` from [Modrinth](https://modrinth.com/mod/keybindinglocker) or [CurseForge](https://curseforge.com/minecraft/mc-mods/keybindinglocker).
3. Drop it into your `mods` folder on both **client and server**.

## ⚙️ How It Works

- Server config defines which keybinding **actions** (like `key.chat`, `key.inventory`) are blocked.
- When the server runs `/keybindinglocker lock`, all clients receive the updated list and the matching keybinds are disabled.
- Players cannot override the locked keys until the server unlocks them again.

## 🧠 Use Cases

- Roleplay servers where UI access needs to be restricted.
- Custom minigames where certain actions are temporarily disabled.
- Events where the player interface must be controlled.

## 👤 Author

Created by [superstrellaa](https://superstrellaa.is-a.dev)

## 📜 License

Licensed under the [GNU GPLv3](LICENSE).  
This mod is free software — feel free to use, share, and improve it under the same license.
