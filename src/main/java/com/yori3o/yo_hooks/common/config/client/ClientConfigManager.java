package com.yori3o.yo_hooks.common.config.client;


import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.config.categories.*;
import com.yori3o.yo_hooks.common.network.ServerSender;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;


/**
 * Importing _net.minecraft.client_ breaks dedicated servers, so some of the code had to be moved here.
 */
public class ClientConfigManager {

    public static void save() {
        ClientConfig.HANDLER.save();
        CommonConfig.HANDLER.save();
        ServerConfig.HANDLER.save();
        OverlapConfig.HANDLER.save();

        if (PlatformUtil.isModLoaded("vivecraft")) VrConfig.HANDLER.save();

        if (Minecraft.getInstance().isLocalServer()) {   

            PhysicVariables.updateCommonVariables(CommonConfig.HANDLER.instance().softHook, CommonConfig.HANDLER.instance().stiffness, CommonConfig.HANDLER.instance().climbSpeed);
            PhysicVariables.updateFunnyModeConfig(CommonConfig.HANDLER.instance().funnyMode);

            sendCommonConfigToAllPlayersInMultiplayer();
        }
    }


    public static void sendCommonConfigToAllPlayersInMultiplayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer().isPublished()) {
            MinecraftServer server = mc.getSingleplayerServer();
            if (server != null) {
                List<ServerPlayer> players = server.getPlayerList().getPlayers();
                ServerPlayer host = server.getPlayerList().getPlayer(mc.player.getUUID());
                for (ServerPlayer p : players) {
                    if (host != null && p.getUUID().equals(host.getUUID())) continue;
                    ServerSender.sendCommonConfig(p, ConfigManager.common(), null);
                }
            }
        }
    }

}
