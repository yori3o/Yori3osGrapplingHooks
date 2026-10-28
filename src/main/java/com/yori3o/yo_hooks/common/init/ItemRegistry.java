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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ItemRegistry {
    public static final Map<String, Supplier<Item>> HOOK_HEADS = new HashMap<>();
    public static final List<Supplier<? extends Item>> ALL_ITEMS = new ArrayList<>();
    public static final Map<String, Supplier<HookItem>> ALL_HOOKS = new LinkedHashMap<>();
    private static Supplier<HookItem> itemForIcon;

    public static void registerHook(HookDefinition definition) {
        String hookId = definition.id + "_grappling_hook";
        String headId = definition.id + "_hook_head";
        // Construct properties inside the supplier: NeoForge opens the registries later.
        Supplier<HookItem> hook = PlatformItemRegistry.registerItem(id(hookId),
            () -> new HookItem(properties(definition, hookId, true), definition));
        Supplier<Item> head = PlatformItemRegistry.registerItem(id(headId),
            () -> new Item(properties(definition, headId, false)));
        ALL_ITEMS.add(hook);
        ALL_ITEMS.add(head);
        ALL_HOOKS.put(definition.id, hook);
        HOOK_HEADS.put(definition.id, head);
        itemForIcon = hook;
    }

    private static Item.Properties properties(HookDefinition definition, String name, boolean hook) {
        Item.Properties properties = new Item.Properties().stacksTo(hook ? 1 : 64);
        //? if >=26.1 {
        properties.setId(ResourceKey.create(Registries.ITEM, id(name)));
        if (hook && definition.enchantability > 0) properties.enchantable(definition.enchantability);
        if (!definition.repairItemsTag.isEmpty()) {
            properties.repairable(TagKey.create(Registries.ITEM, Identifier.parse(definition.repairItemsTag)));
        }
        //? }
        if (hook && definition.getDurability() > 0) properties.durability(definition.getDurability());
        if (definition.fireResistant) properties.fireResistant();
        return properties;
    }

    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, path); }
    public static Item getItemForIcon() { return itemForIcon.get(); }
}
