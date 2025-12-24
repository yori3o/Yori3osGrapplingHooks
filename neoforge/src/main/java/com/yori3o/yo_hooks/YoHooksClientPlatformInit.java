package com.yori3o.yo_hooks;

import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.item.HookItem;
import com.yori3o.yo_hooks.init.EntitiyRegistry;
import com.yori3o.yo_hooks.init.ItemRegistry;
import com.yori3o.yo_hooks.render.entity.HookRenderer;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = "yo_hooks", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class YoHooksClientPlatformInit {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntitiyRegistry.HOOK_ENTITY.get(), HookRenderer::new);
    }

    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.insertAfter(new ItemStack(Items.IRON_HOE), new ItemStack(ItemRegistry.IRON_GRAPPLING_HOOK.get()), TabVisibility.PARENT_AND_SEARCH_TABS);//.accept(ItemRegistry.IRON_GRAPPLING_HOOK.get());
            event.insertAfter(new ItemStack(Items.GOLDEN_HOE), new ItemStack(ItemRegistry.GOLD_GRAPPLING_HOOK.get()), TabVisibility.PARENT_AND_SEARCH_TABS);//event.accept(ItemRegistry.GOLD_GRAPPLING_HOOK.get());
            event.insertAfter(new ItemStack(Items.DIAMOND_HOE), new ItemStack(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get()), TabVisibility.PARENT_AND_SEARCH_TABS);//event.accept(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get());
            event.insertAfter(new ItemStack(Items.NETHERITE_HOE), new ItemStack(ItemRegistry.NETHERITE_GRAPPLING_HOOK.get()), TabVisibility.PARENT_AND_SEARCH_TABS);//event.accept(ItemRegistry.NETHERITE_GRAPPLING_HOOK.get());
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.NETHERITE_HOOK_HEAD.get()), TabVisibility.PARENT_AND_SEARCH_TABS);//event.accept(ItemRegistry.IRON_HOOK_HEAD.get());
            event.insertAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.DIAMOND_HOOK_HEAD.get()), TabVisibility.PARENT_AND_SEARCH_TABS);//event.accept(ItemRegistry.GOLD_HOOK_HEAD.get());
            event.insertAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.GOLD_HOOK_HEAD.get()), TabVisibility.PARENT_AND_SEARCH_TABS);//event.accept(ItemRegistry.DIAMOND_HOOK_HEAD.get());
            event.insertAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.IRON_HOOK_HEAD.get()), TabVisibility.PARENT_AND_SEARCH_TABS);//event.accept(ItemRegistry.NETHERITE_HOOK_HEAD.get());
        }
    }

    /*public static void makeHooksItem() {
        ItemProperties.register(
            ItemRegistry.GRAPPLING_HOOK.get(),
            ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_extended"),
            (stack, level, entity, seed) -> {
                if (entity == null) {
                    return 0.0F;
                } else {
            boolean main = entity.getMainHandItem() == stack;
                    boolean off = entity.getOffhandItem() == stack;
                    if (entity.getUseItem().getItem() instanceof HookItem) {
                        off = false;
                    }
                    return (main || off)
                            && entity instanceof Player
                            && ((PlayerWithHookData) entity).getHook() != null ? 1.0F : 0.0F;
                }
            }
        );
    }*/
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                ItemRegistry.IRON_GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
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

            ItemProperties.register(
                ItemRegistry.GOLD_GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
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

            ItemProperties.register(
                ItemRegistry.DIAMOND_GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
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

            ItemProperties.register(
                ItemRegistry.NETHERITE_GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
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
        });
    }
}
