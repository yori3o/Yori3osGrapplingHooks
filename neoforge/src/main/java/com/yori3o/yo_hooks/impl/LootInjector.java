package com.yori3o.yo_hooks.impl;

import com.yori3o.yo_hooks.common.init.ItemRegistry;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/*import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.packs.VanillaAdvancementProvider;
import net.minecraft.data.tags.VanillaEnchantmentTagsProvider;
import net.minecraft.resources.ResourceKey;*/



@EventBusSubscriber
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


    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        ResourceLocation key = event.getName();

            if (key.equals(BASTION_TREASURE)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.NETHERITE_GRAPPLING_HOOK.get())
                                .setWeight(4)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1F, 0.5F)))
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(3))
                        .build();

                event.getTable().addPool(pool);
            } else if (key.equals(MINESHAFT)) {

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

                event.getTable().addPool(pool);
            } else if (key.equals(JUNGLE_TEMPLE)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK.get())
                                .setWeight(2)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.5F, 0.9F)))
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

                event.getTable().addPool(pool);
            } else if (key.equals(SPAWN_BONUS_CHEST)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK.get())
                                .setWeight(1)
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.IRON_GRAPPLING_HOOK.get())
                                .setWeight(1)
                        )
                        .build();

                event.getTable().addPool(pool);
            } else if (key.equals(RUINED_PORTAL)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK.get())
                                .setWeight(1)
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(1))
                        .build();

                event.getTable().addPool(pool);
            } else if (key.equals(BASTION_OTHER)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.GOLD_GRAPPLING_HOOK.get())
                                .setWeight(1)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.5F, 0.9F)))
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                        .build();

                event.getTable().addPool(pool);
            } else if (key.equals(END_CITY_TREASURE)) {

                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get())
                                .setWeight(1)
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get())
                                .setWeight(1)
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                                .apply(EnchantRandomlyFunction.randomEnchantment())
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(18))
                        .build();

                event.getTable().addPool(pool);
            }
        };
    }
