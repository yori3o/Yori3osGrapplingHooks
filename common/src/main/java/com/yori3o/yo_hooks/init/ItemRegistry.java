package com.yori3o.yo_hooks.init;

import java.util.HashMap;
import java.util.Map;

import com.yori3o.yo_hooks.YoHooks;
import com.yori3o.yo_hooks.item.HookItem;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemRegistry {

    public static Map<String, Item> hookHeads = new HashMap<>();

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(YoHooks.MOD_ID, Registries.ITEM);

    

    public static final RegistrySupplier<Item> IRON_GRAPPLING_HOOK = ITEMS.register(
            "iron_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(YoHooks.ironHookDurabitility), YoHooks.ironHookLength, Items.IRON_INGOT, "iron_hook_head")
    );
    public static final RegistrySupplier<Item> GOLD_GRAPPLING_HOOK = ITEMS.register(
            "gold_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(YoHooks.goldHookDurabitility), YoHooks.goldHookLength, Items.GOLD_INGOT, "gold_hook_head")
    );
    public static final RegistrySupplier<Item> DIAMOND_GRAPPLING_HOOK = ITEMS.register(
            "diamond_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(YoHooks.diamondHookDurabitility), YoHooks.diamondHookLength, Items.DIAMOND, "diamond_hook_head")
    );
    public static final RegistrySupplier<Item> NETHERITE_GRAPPLING_HOOK = ITEMS.register(
            "netherite_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(YoHooks.netheriteHookDurabitility).fireResistant(), YoHooks.netheriteHookLength, Items.NETHERITE_INGOT, "netherite_hook_head")
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
