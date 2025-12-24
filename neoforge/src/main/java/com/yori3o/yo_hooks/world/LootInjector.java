package com.yori3o.yo_hooks.world;

import com.yori3o.yo_hooks.init.ItemRegistry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
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
                                .setWeight(3)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.2F, 0.5F)))
                        )
                        .add(LootItem.lootTableItem(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get())
                                .setWeight(1)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1F, 0.4F)))
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(3))
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
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.2F, 0.5F)))
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(1))
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
                                //.apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.5F, 0.9F)))
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(1))
                        .build();

                event.getTable().addPool(pool);
            }
        };
    }
