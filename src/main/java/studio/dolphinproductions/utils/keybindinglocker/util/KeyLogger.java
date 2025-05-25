package studio.dolphinproductions.utils.keybindinglocker.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public class KeyLogger {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen == null) {
                for (int keyCode = 0; keyCode < 256; keyCode++) {
                    if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyCode)) {
                        System.out.println("Pressed key: " + InputUtil.fromKeyCode(keyCode, 0).getTranslationKey());
                    }
                }
            }
        });
    }
}
