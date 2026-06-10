package com.yori3o.yo_hooks.common.init;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.impl.PlatformItemRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;



public class ItemRegistry {


    public static final Map<String, Supplier<Item>> HOOK_HEADS = new HashMap<>();

    public static final List<Supplier<? extends Item>> ALL_ITEMS = new ArrayList<>();
    
    public static final Map<String, Supplier<HookItem>> ALL_HOOKS = new LinkedHashMap<>();

    private static Supplier<HookItem> itemForIcon = null;


    public static void registerHook(HookDefinition def) {
        String material = def.id;
        int durability = def.getDurability();
        boolean fireResistant = def.fireResistant;
        String repairItemsTag = def.repairItemsTag;

        String hookId = material + "_grappling_hook";
        String hookHeadId = material + "_hook_head";

        Item.Properties hookProperties = new Item.Properties().stacksTo(1).enchantable(def.enchantability)
                .setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", hookId)));
        Item.Properties hookHeadProperties = new Item.Properties().stacksTo(64)
                .setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("yo_hooks", hookHeadId)));

        if (durability > 0) {
            hookProperties = hookProperties.durability(durability);
        }

        if (fireResistant) {
            hookHeadProperties = hookHeadProperties.fireResistant();
            hookProperties = hookProperties.fireResistant();
        }

        TagKey<Item> tag = null;
        if (!repairItemsTag.isEmpty()) {
            tag = TagKey.create(Registries.ITEM, Identifier.parse(repairItemsTag));

            hookHeadProperties = hookHeadProperties.repairable(tag);
            hookProperties = hookProperties.repairable(tag);
        }

        final Item.Properties hookHeadPropertiesFinal = hookHeadProperties;
        final Item.Properties hookPropertiesFinal = hookProperties;

        Supplier<HookItem> hook = PlatformItemRegistry.registerItem(Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, hookId), () -> new HookItem(
            hookPropertiesFinal,
            def
        ));
        Supplier<Item> hookHead = PlatformItemRegistry.registerItem(Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, hookHeadId), () -> new Item(
            hookHeadPropertiesFinal
        ));

        ALL_ITEMS.add(hook);
        ALL_ITEMS.add(hookHead);

        itemForIcon = hook;
        
        ALL_HOOKS.put(material, hook);

        HOOK_HEADS.put(material, hookHead);
    }


    public static Item getItemForIcon() {
        return itemForIcon.get();
    }

}
