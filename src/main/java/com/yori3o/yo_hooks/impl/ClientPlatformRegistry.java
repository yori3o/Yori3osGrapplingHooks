package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;

import java.util.function.Supplier;

import com.yori3o.yo_hooks.common.client.gui.ConfigScreen;
import com.yori3o.yo_hooks.common.client.render.HookRenderer;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientPlatformRegistry {

    
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.HOOK_ENTITY.get(), HookRenderer::new);
    }

    

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (mc, parent) -> new ConfigScreen(parent)
        );
  
        event.enqueueWork(() -> {
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
        });
    }
}
