package studio.dolphinproductions.utils.keybindinglocker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KeybindingLocker implements ModInitializer {

    public static final String MOD_ID = "keybindinglocker";
    public static final Identifier KEY_BLOCK_SYNC_PACKET = new Identifier(MOD_ID, "sync_key_blocks");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));

        ConfigManager.loadConfig();

        ServerPlayNetworking.registerGlobalReceiver(KEY_BLOCK_SYNC_PACKET, (server, player, handler, buf, responseSender) -> {
            PacketByteBuf syncBuf = new PacketByteBuf(Unpooled.buffer());

            if (player.hasPermissionLevel(2)) {
                syncBuf.writeBoolean(false);
                syncBuf.writeInt(0);
            } else {
                syncBuf.writeBoolean(ConfigManager.isLocked);
                syncBuf.writeInt(ConfigManager.blockedKeys.size());
                ConfigManager.blockedKeys.forEach(syncBuf::writeString);
            }

            ServerPlayNetworking.send(player, KEY_BLOCK_SYNC_PACKET, syncBuf);
        });
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("keybindinglocker")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("lock").executes(context -> toggleLock(true, context)))
                        .then(CommandManager.literal("unlock").executes(context -> toggleLock(false, context)))
                        .then(CommandManager.literal("reload").executes(context -> reloadConfig(context)))
        );
    }

    private int reloadConfig(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        ConfigManager.loadConfig();

        source.getServer().getPlayerManager().getPlayerList().forEach(player -> {
            if (!player.hasPermissionLevel(2)) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBoolean(ConfigManager.isLocked);
                buf.writeInt(ConfigManager.blockedKeys.size());
                ConfigManager.blockedKeys.forEach(buf::writeString);

                ServerPlayNetworking.send(player, KEY_BLOCK_SYNC_PACKET, buf);
            }
        });

        source.sendFeedback(() -> Text.literal("KeybindingLocker reloaded successfully."), true);
        return 1;
    }

    private int toggleLock(boolean lockState, CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ConfigManager.isLocked = lockState;
        ConfigManager.saveConfig();

        source.sendFeedback(() -> Text.literal("Keys " + (lockState ? "locked" : "unlocked") + " successfully."), true);

        source.getServer().getPlayerManager().getPlayerList().forEach(player -> {
            if (!player.hasPermissionLevel(2)) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBoolean(ConfigManager.isLocked);
                buf.writeInt(ConfigManager.blockedKeys.size());
                ConfigManager.blockedKeys.forEach(buf::writeString);

                ServerPlayNetworking.send(player, KEY_BLOCK_SYNC_PACKET, buf);
            }
        });

        return 1;
    }

}
