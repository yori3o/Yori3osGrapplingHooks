package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.init.ItemRegistry;


import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;

import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;


/*import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentProviderTypes;
import net.minecraft.client.particle.FlyTowardsPositionParticle.EnchantProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;*/



public class LootInjector {

    private static final Identifier BASTION_TREASURE =
            Identifier.fromNamespaceAndPath("minecraft", "chests/bastion_treasure");
    private static final Identifier MINESHAFT =
            Identifier.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft");
    private static final Identifier JUNGLE_TEMPLE =
            Identifier.fromNamespaceAndPath("minecraft", "chests/jungle_temple");
    private static final Identifier SPAWN_BONUS_CHEST =
            Identifier.fromNamespaceAndPath("minecraft", "chests/spawn_bonus_chest");
    private static final Identifier RUINED_PORTAL =
            Identifier.fromNamespaceAndPath("minecraft", "chests/ruined_portal");
    private static final Identifier BASTION_OTHER =
            Identifier.fromNamespaceAndPath("minecraft", "chests/bastion_other");
    private static final Identifier END_CITY_TREASURE =
            Identifier.fromNamespaceAndPath("minecraft", "chests/end_city_treasure");

    public static void register() {
        //LootTableEvents.MODIFY.register((resourceManager, lootManager, key, tableBuilder, source) -> {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (key.identifier().equals(BASTION_TREASURE)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.NETHERITE_GRAPPLING_HOOK)
                                .setWeight(4)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1F, 0.5F)))
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(3))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.identifier().equals(MINESHAFT)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.IRON_GRAPPLING_HOOK)
                                .setWeight(6)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.2F, 0.5F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK)
                                .setWeight(3)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.35F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_HOOK_HEAD)
                                .setWeight(1)
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(6))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.identifier().equals(JUNGLE_TEMPLE)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK)
                                .setWeight(2)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.4F, 0.8F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.IRON_GRAPPLING_HOOK)
                                .setWeight(1)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3F, 0.6F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_HOOK_HEAD)
                                .setWeight(1)
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.identifier().equals(SPAWN_BONUS_CHEST)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK)
                                .setWeight(1)
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.IRON_GRAPPLING_HOOK)
                                .setWeight(1)
                        )
                        .build();

                tableBuilder.pool(pool);
            } else if (key.identifier().equals(RUINED_PORTAL)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK)
                                .setWeight(1)
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(1))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.identifier().equals(BASTION_OTHER)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK)
                                .setWeight(1)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.4F, 0.9F)))
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.identifier().equals(END_CITY_TREASURE)) {
                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK)
                                .setWeight(1)
                                //.apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(20, 39)))
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK)
                                .setWeight(1)
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(18))
                        .build();

                tableBuilder.pool(pool);
            }
        });
    }
    
}
