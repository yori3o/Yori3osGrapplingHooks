package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.hookregistry.LootTableDefinition;
import com.yori3o.yo_hooks.common.hookregistry.LootTableRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;



@EventBusSubscriber
public class LootInjector {


    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        ResourceLocation key = event.getName();

        for (LootTableDefinition lootTable : LootTableRegistry.lootTables) {
            if (key.equals(ResourceLocation.parse(lootTable.lootTableForInject))) {

                LootPool.Builder pool = LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lootTable.lootTable))));

                event.getTable().addPool(pool.build());
            }
        }
    }

}
