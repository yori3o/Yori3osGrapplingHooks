package com.yori3o.yo_hooks.common.init;

import java.util.HashMap;
import java.util.Map;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.item.HookItem;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemRegistry {

    public static final Map<String, Item> hookHeads = new HashMap<>();

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(YoHooks.MOD_ID, Registries.ITEM);

    

    public static final RegistrySupplier<Item> IRON_GRAPPLING_HOOK = ITEMS.register(
            "iron_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(DynamicConfigHandler.ironHookDurabitility), DynamicConfigHandler.ironHookLength, Items.IRON_INGOT, "iron_hook_head", 6, 1)
    );
    public static final RegistrySupplier<Item> GOLD_GRAPPLING_HOOK = ITEMS.register(
            "gold_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(DynamicConfigHandler.goldHookDurabitility), DynamicConfigHandler.goldHookLength, Items.GOLD_INGOT, "gold_hook_head", 20, 1)
    );
    public static final RegistrySupplier<Item> DIAMOND_GRAPPLING_HOOK = ITEMS.register(
            "diamond_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(DynamicConfigHandler.diamondHookDurabitility), DynamicConfigHandler.diamondHookLength, Items.DIAMOND, "diamond_hook_head", 12, 2)
    );
    public static final RegistrySupplier<Item> NETHERITE_GRAPPLING_HOOK = ITEMS.register(
            "netherite_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(DynamicConfigHandler.netheriteHookDurabitility).fireResistant(), DynamicConfigHandler.netheriteHookLength, Items.NETHERITE_INGOT, "netherite_hook_head", 15, 2)
    );
    public static final RegistrySupplier<Item> IRON_HOOK_HEAD = ITEMS.register(
            "iron_hook_head",
            () -> new Item(new Item.Properties().stacksTo(64))
    );
    public static final RegistrySupplier<Item> GOLD_HOOK_HEAD = ITEMS.register(
            "gold_hook_head",
            () -> new Item(new Item.Properties().stacksTo(64))
    );
    public static final RegistrySupplier<Item> DIAMOND_HOOK_HEAD = ITEMS.register(
            "diamond_hook_head",
            () -> new Item(new Item.Properties().stacksTo(64))
    );
    public static final RegistrySupplier<Item> NETHERITE_HOOK_HEAD = ITEMS.register(
            "netherite_hook_head",
            () -> new Item(new Item.Properties().stacksTo(64).fireResistant())
    );
    

    public static void register() {
        ITEMS.register();
    }

    public static void registerHookHeads() {
        for (RegistrySupplier<Item> itemSupplier : ITEMS) {
            Item item = itemSupplier.get();
            if (!(item instanceof HookItem)) {
                hookHeads.put(itemSupplier.getId().getPath(), item);
            }
        }
    }
}
