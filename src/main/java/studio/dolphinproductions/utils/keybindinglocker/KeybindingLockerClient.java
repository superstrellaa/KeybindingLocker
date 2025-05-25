package studio.dolphinproductions.utils.keybindinglocker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import static studio.dolphinproductions.utils.keybindinglocker.KeybindingLocker.MOD_ID;

@Environment(EnvType.CLIENT)
public class KeybindingLockerClient implements ClientModInitializer {

    private static final Set<String> blockedKeys = new HashSet<>();
    private static final Map<String, KeyBinding> originalBindings = new HashMap<>();
    public static boolean isLocked = false;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static final Set<String> EXCLUDED_KEYS = Set.of("key.escape", "key.screenshot");

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(KeybindingLocker.KEY_BLOCK_SYNC_PACKET, (client, handler, buf, responseSender) -> {
            isLocked = buf.readBoolean();
            int size = buf.readInt();
            blockedKeys.clear();

            for (int i = 0; i < size; i++) {
                String key = buf.readString();
                if (!EXCLUDED_KEYS.contains(key)) {
                    blockedKeys.add(key);
                }
            }

            client.execute(() -> {
                if (isLocked) {
                    lockKeys();
                } else {
                    unlockKeys();
                }
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen != null) {
                unlockKeys();
            } else {
                if (isLocked)
                    lockKeys();
            }
        });
    }

    public static Set<String> getBlockedKeys() {
        return blockedKeys;
    }

    private static void lockKeys() {
        if (client == null || client.options == null) return;

        for (String key : blockedKeys) {
            KeyBinding keyBinding = getKeyBinding(key);
            if (keyBinding != null) {
                originalBindings.put(key, keyBinding);
                keyBinding.setBoundKey(null);
                KeyBinding.updateKeysByCode();
            }
        }
    }

    private static void unlockKeys() {
        originalBindings.forEach((key, keyBinding) -> {
            keyBinding.setBoundKey(keyBinding.getDefaultKey());
            KeyBinding.updateKeysByCode();
        });
        originalBindings.clear();
    }

    private static KeyBinding getKeyBinding(String keyId) {
        if (client == null || client.options == null) return null;
        for (KeyBinding keyBinding : client.options.allKeys) {
            if (keyBinding.getTranslationKey().equals(keyId)) {
                return keyBinding;
            }
        }
        return null;
    }
}
