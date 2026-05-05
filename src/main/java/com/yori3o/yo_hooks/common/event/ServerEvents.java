package com.yori3o.yo_hooks.common.event;


import com.yori3o.yo_hooks.common.config.CommonConfig;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.network.ServerSender;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;



public class ServerEvents {


    public static void loadConfigOnServer(MinecraftServer server) {
        DynamicConfigHandler.loadServer();
        DynamicConfigHandler.loadCommon();
    }

    public static void sendConfigToNewPlayer(ServerPlayer serverPlayer) {
        CommonConfig cc = new CommonConfig();
        cc.load();

        Map<String, Integer> hookLengths = new HashMap<>();
        for (Supplier<HookItem> hook : ItemRegistry.ALL_HOOKS.values()) {
            hookLengths.put(hook.get().hookDefinition.id, hook.get().hookDefinition.length);
        }
        ServerSender.sendCommonConfig(serverPlayer, cc.get(), hookLengths);
    }

    public static void checkSuddenFall(Player player, DamageSource damageSource) {
        if (((PlayerWithHookData) player).isSuddenFall()) {
            if (damageSource.is(DamageTypes.FALL)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.getAdvancements().award(
                        serverPlayer.level().getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath("yo_hooks", "adventure/sudden_fall")), 
                        "fall_death"
                    );
                }
            }
        }
    }
    
}