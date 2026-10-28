package com.yori3o.yo_hooks.impl;
import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.hookregistry.LootTableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
//? if fabric {
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
//? } else {
/*import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
*///? }

//? if neoforge {
/*@EventBusSubscriber(modid = YoHooks.MOD_ID)
*///? }
public final class LootInjector {
    //? if fabric {
    public static void register() {
        LootTableEvents.MODIFY.register((key, table, source, registries) -> {
            for (var definition : LootTableRegistry.lootTables) {
                if (!key.identifier().equals(Identifier.parse(definition.lootTableForInject))) continue;
                var nestedKey = ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse(definition.lootTable));
                //? if >=26.3 {
                var entry = NestedLootTable.lootTableReference(registries.lookupOrThrow(Registries.LOOT_TABLE).getOrThrow(nestedKey));
                //? } else {
                /*var entry = NestedLootTable.lootTableReference(nestedKey);
                *///? }
                table.pool(LootPool.lootPool().add(entry).build());
            }
        });
    }
    //? } else {
    /*@SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        for (var definition : LootTableRegistry.lootTables) {
            if (!event.getName().equals(Identifier.parse(definition.lootTableForInject))) continue;
            var nestedKey = ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse(definition.lootTable));
            //? if >=26.3 {
            var entry = NestedLootTable.lootTableReference(event.getRegistries().lookupOrThrow(Registries.LOOT_TABLE).getOrThrow(nestedKey));
            //? } else {
            /^var entry = NestedLootTable.lootTableReference(nestedKey);
            ^///? }
            event.getTable().addPool(LootPool.lootPool().add(entry).build());
        }
    }
    *///? }
}
