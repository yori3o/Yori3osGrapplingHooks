package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.hookregistry.LootTableDefinition;
import com.yori3o.yo_hooks.common.hookregistry.LootTableRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;



public class LootInjector {

    
    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            for (LootTableDefinition lootTable : LootTableRegistry.lootTables) {
                if (key.identifier().equals(Identifier.parse(lootTable.lootTableForInject))) {

                    LootPool.Builder pool = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse(lootTable.lootTable))));

                    tableBuilder.pool(pool.build());     
                }
            }

        });
    }
}
