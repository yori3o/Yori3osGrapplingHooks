package com.yori3o.yo_hooks.common.init;

import java.util.HashMap;
import java.util.Map;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.item.HookItem;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemRegistry {

    public static final Map<String, Item> hookHeads = new HashMap<>();


    

    public static final Item IRON_GRAPPLING_HOOK =  register(
            "iron_grappling_hook",
            new HookItem(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "iron_grappling_hook"))).repairable(Items.IRON_INGOT).stacksTo(1).durability(DynamicConfigHandler.ironHookDurabitility).enchantable(6), DynamicConfigHandler.ironHookLength, "iron_hook_head", 1)
    );
    public static final Item GOLD_GRAPPLING_HOOK =  register(
            "gold_grappling_hook",
            new HookItem(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "gold_grappling_hook"))).repairable( Items.GOLD_INGOT).stacksTo(1).durability(DynamicConfigHandler.goldHookDurabitility).enchantable(20), DynamicConfigHandler.goldHookLength, "gold_hook_head", 1)
    );
    public static final Item DIAMOND_GRAPPLING_HOOK =  register(
            "diamond_grappling_hook",
            new HookItem(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "diamond_grappling_hook"))).repairable( Items.DIAMOND).stacksTo(1).durability(DynamicConfigHandler.diamondHookDurabitility).enchantable(12), DynamicConfigHandler.diamondHookLength, "diamond_hook_head", 2)
    );
    public static final Item NETHERITE_GRAPPLING_HOOK =  register(
            "netherite_grappling_hook",
            new HookItem(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "netherite_grappling_hook"))).repairable( Items.NETHERITE_INGOT).stacksTo(1).durability(DynamicConfigHandler.netheriteHookDurabitility).fireResistant().enchantable(15), DynamicConfigHandler.netheriteHookLength, "netherite_hook_head", 2)
    );
    public static final Item COPPER_GRAPPLING_HOOK =  register(
            "copper_grappling_hook",
            new HookItem(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "copper_grappling_hook"))).repairable( Items.COPPER_INGOT).stacksTo(1).durability(DynamicConfigHandler.copperHookDurabitility).enchantable(4), DynamicConfigHandler.copperHookLength, "copper_hook_head", 1)
    );

    public static final Item IRON_HOOK_HEAD =  register(
            "iron_hook_head",
            new Item(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "iron_hook_head"))).stacksTo(64))
    );
    public static final Item GOLD_HOOK_HEAD =  register(
            "gold_hook_head",
            new Item(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "gold_hook_head"))).stacksTo(64))
    );
    public static final Item DIAMOND_HOOK_HEAD =  register(
            "diamond_hook_head",
            new Item(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "diamond_hook_head"))).stacksTo(64))
    );
    public static final Item NETHERITE_HOOK_HEAD =  register(
            "netherite_hook_head",
            new Item(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "netherite_hook_head"))).stacksTo(64).fireResistant())
    );
    public static final Item COPPER_HOOK_HEAD =  register(
            "copper_hook_head",
            new Item(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", "copper_hook_head"))).stacksTo(64))
    );
    

    private static Item register(String name, Item item) {
        Identifier id = Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, name);
        Item registered = Registry.register(BuiltInRegistries.ITEM, id, item);

        // аналог твоего registerHookHeads
        if (!(registered instanceof HookItem)) {
            hookHeads.put(name, registered);
        }

        return registered;
    }
    

    public static void register() {
        // Пусто, но метод оставляют для порядка
    }
}
