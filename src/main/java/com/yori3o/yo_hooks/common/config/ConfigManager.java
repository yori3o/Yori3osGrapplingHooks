package com.yori3o.yo_hooks.common.config;

import java.util.List;

import com.yori3o.yo_hooks.common.network.ServerSender;
import com.yori3o.yo_hooks.common.util.PhysicVariables;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ConfigManager {
    public static ModConfig CONFIG = new ModConfig();

    public static void save(ModConfig config) {
        CONFIG = config;
        
        ClientConfig.Values clcv = DynamicConfigHandler.client();
        CommonConfig.Values ccv = DynamicConfigHandler.common();
        ServerConfig.Values scv = DynamicConfigHandler.server();
        OverlapConfig.Values ocv = DynamicConfigHandler.overlap();

        ccv.softHook = config.softHook;
        ccv.stiffness = config.stiffness;
        ccv.climbSpeed = config.climbSpeed;
        ccv.funnyMode = config.funnyMode;
        scv.decreaseSatiety = config.decreaseSatiety;
        scv.breakingFragileBlocks = config.breakingFragileBlocks;
        clcv.holdHookTightly = config.holdHookTightly;
        
        ocv.rangeOverlap = config.rangeOverlap;
        ocv.durabilityOverlap = config.durabilityOverlap;

        /*if (bool) {
            ServerConfig sc = new ServerConfig();
            ServerConfig.Values scv2 = sc.get();
            scv.blocksBlacklist = scv2.blocksBlacklist;
            scv.whitelistMode = scv2.whitelistMode;
        } else {*/
            scv.blocksBlacklist = config.blocksBlacklist;
            scv.whitelistMode = config.whitelistMode;
        //}
        

        DynamicConfigHandler.cc.save();
        DynamicConfigHandler.sc.save();
        DynamicConfigHandler.clc.save();
        DynamicConfigHandler.oc.save();

        if (Minecraft.getInstance().isLocalServer()) {   

            PhysicVariables.updateCommonVariables(config.softHook, config.stiffness, config.climbSpeed);
            PhysicVariables.updateFunnyModeConfig(config.funnyMode);

            sendCommonConfigToAllPlayersInMultiplayer();
        }
    }

    private static void sendCommonConfigToAllPlayersInMultiplayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer().isPublished()) {
            MinecraftServer server = mc.getSingleplayerServer();
            if (server != null) {
                List<ServerPlayer> players = server.getPlayerList().getPlayers();
                ServerPlayer host = server.getPlayerList().getPlayer(mc.player.getUUID());
                for (ServerPlayer p : players) {
                    if (host != null && p.getUUID().equals(host.getUUID())) continue;
                    ServerSender.sendCommonConfig(p, DynamicConfigHandler.cc.get(), null);
                }
            }
        }
    }
}