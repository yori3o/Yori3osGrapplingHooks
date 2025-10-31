package com.yori3o.yo_hooks.register;

import com.yori3o.yo_hooks.YoHooks;
import com.yori3o.yo_hooks.item.HookItem;
import com.yori3o.yo_hooks.utils.HooksAttributes;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ModItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(YoHooks.MOD_ID, Registries.ITEM);

    

    public static final RegistrySupplier<Item> IRON_GRAPPLING_HOOK = ITEMS.register(
            "iron_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(HooksAttributes.ironHookDurabitility), HooksAttributes.ironHookLength, Items.IRON_INGOT)
    );
    public static final RegistrySupplier<Item> GOLD_GRAPPLING_HOOK = ITEMS.register(
            "gold_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(HooksAttributes.goldHookDurabitility), HooksAttributes.goldHookLength, Items.GOLD_INGOT)
    );
    public static final RegistrySupplier<Item> DIAMOND_GRAPPLING_HOOK = ITEMS.register(
            "diamond_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(HooksAttributes.diamondHookDurabitility), HooksAttributes.diamondHookLength, Items.DIAMOND)
    );
    public static final RegistrySupplier<Item> NETHERITE_GRAPPLING_HOOK = ITEMS.register(
            "netherite_grappling_hook",
            () -> new HookItem(new Item.Properties().stacksTo(1).durability(HooksAttributes.netheriteHookDurabitility).fireResistant(), HooksAttributes.netheriteHookLength, Items.NETHERITE_INGOT)
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
            () -> new Item(new Item.Properties().stacksTo(64))
    );
    

    public static void register() {
        ITEMS.register();
    }
}
