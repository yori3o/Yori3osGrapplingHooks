package com.yori3o.yo_hooks.common;


import com.yori3o.yo_hooks.common.client.render.HookRenderer;
import com.yori3o.yo_hooks.common.client.vr.HandInteractModule;
import com.yori3o.yo_hooks.common.client.vr.HandTracker;
import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;
import com.yori3o.yo_hooks.impl.PlatformEntityRendererRegistry;
import com.yori3o.yo_hooks.impl.PlatformKeyMappingRegistry;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.platform.InputConstants;

import java.util.function.Supplier;

import org.vivecraft.api.client.VRClientAPI;



public class YoHooksClient {
    

    public static final KeyMapping JUMP = new KeyMapping(
        "key.yo_hooks.jump",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_SPACE,
        "category.yo_hooks"
    );
    public static final KeyMapping CLIMB = new KeyMapping(
        "key.yo_hooks.climb",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        "category.yo_hooks"
    );
    public static final KeyMapping CLIMB_DOWN = new KeyMapping(
        "key.yo_hooks.climb_down",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        "category.yo_hooks"
    );
    public static final KeyMapping PREVENT_USE = new KeyMapping(
        "key.yo_hooks.prevent_use",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        "category.yo_hooks"
    );


    public static void initClient() {
        PlatformKeyMappingRegistry.registerKeyMapping(JUMP);
        PlatformKeyMappingRegistry.registerKeyMapping(CLIMB);
        PlatformKeyMappingRegistry.registerKeyMapping(CLIMB_DOWN);
        PlatformKeyMappingRegistry.registerKeyMapping(PREVENT_USE);

        if (PlatformUtil.isModLoaded("vivecraft")) {
            VRClientAPI.instance().addClientRegistrationHandler(event -> {
                event.registerInteractModules(HandInteractModule.INSTANCE);
                event.registerTrackers(HandTracker.INSTANCE);
            });
        }

        ConfigManager.loadClient();

        PlatformEntityRendererRegistry.registerEntityRenderer(EntityRegistry.HOOK_ENTITY.get(), HookRenderer::new);

        registerItemProperty();
    }

    private static void registerItemProperty() {
        for (Supplier<HookItem> supplier : ItemRegistry.ALL_HOOKS.values()) {
    
            ItemProperties.register(
                supplier.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player)) {
                        return 0.0F;
                    }
                    Player player = (Player) entity;
                    HookEntity hook = ((PlayerWithHookData) player).getHook();
                    ItemStack mainHandItem = player.getMainHandItem();

                    boolean flag = mainHandItem == stack;
                    boolean flag1 = player.getOffhandItem() == stack;
                    if (mainHandItem.getItem() instanceof HookItem) {
                        flag1 = false;
                    }

                    boolean hookIsActive = hook != null && !hook.isRemoved();
                    if ((flag || flag1) && hookIsActive) {
                        return 1.0F;
                    } else {
                        return 0.0F;
                    }
                }
            );
        }
    }
    
}
