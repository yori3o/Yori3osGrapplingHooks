package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.init.ItemRegistry;


import net.minecraft.resources.ResourceLocation;
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

    private static final ResourceLocation BASTION_TREASURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_treasure");
    private static final ResourceLocation MINESHAFT =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft");
    private static final ResourceLocation JUNGLE_TEMPLE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/jungle_temple");
    private static final ResourceLocation SPAWN_BONUS_CHEST =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/spawn_bonus_chest");
    private static final ResourceLocation RUINED_PORTAL =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/ruined_portal");
    private static final ResourceLocation BASTION_OTHER =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_other");
    private static final ResourceLocation END_CITY_TREASURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure");

    public static void register() {
        //LootTableEvents.MODIFY.register((resourceManager, lootManager, key, tableBuilder, source) -> {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (key.location().equals(BASTION_TREASURE)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.NETHERITE_GRAPPLING_HOOK.get())
                                .setWeight(4)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1F, 0.5F)))
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(3))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.location().equals(MINESHAFT)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.IRON_GRAPPLING_HOOK.get())
                                .setWeight(6)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.2F, 0.5F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get())
                                .setWeight(3)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.35F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_HOOK_HEAD.get())
                                .setWeight(1)
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(6))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.location().equals(JUNGLE_TEMPLE)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK.get())
                                .setWeight(2)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.4F, 0.8F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.IRON_GRAPPLING_HOOK.get())
                                .setWeight(1)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3F, 0.6F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_HOOK_HEAD.get())
                                .setWeight(1)
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.location().equals(SPAWN_BONUS_CHEST)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK.get())
                                .setWeight(1)
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.IRON_GRAPPLING_HOOK.get())
                                .setWeight(1)
                        )
                        .build();

                tableBuilder.pool(pool);
            } else if (key.location().equals(RUINED_PORTAL)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK.get())
                                .setWeight(1)
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(1))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.location().equals(BASTION_OTHER)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK.get())
                                .setWeight(1)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.4F, 0.9F)))
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                        .build();

                tableBuilder.pool(pool);
            } else if (key.location().equals(END_CITY_TREASURE)) {
                /*Holder<EnchantmentProvider> provider =
                        registries
                                .lookup(Registries.ENCHANTMENT_PROVIDER)
                                .orElseThrow()
                                .getOrThrow(
                                        ResourceKey.create(
                                                Registries.ENCHANTMENT_PROVIDER,
                                                ResourceLocation.fromNamespaceAndPath("minecraft", "on_random_loot")
                                        )
                                );*/


                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get())
                                .setWeight(1) // UniformGenerator.between(20, 39)
                                //.apply(EnchantWithLevelsFunction.enchantWithLevels((Provider)provider, UniformGenerator.between(20, 39)))
                                //.apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(20, 39)))
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get())
                                .setWeight(1) // UniformGenerator.between(20, 39)
                                //.apply(EnchantWithLevelsFunction.enchantWithLevels((Provider)provider, UniformGenerator.between(20, 39)))
                                //.apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(20, 39)))
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
