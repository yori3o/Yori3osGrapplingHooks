package com.yori3o.yo_hooks.common.init;

import com.yori3o.yo_hooks.common.YoHooks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class TagRegistry {
    public static final TagKey<Block> FRAGILE_BLOCKS =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(YoHooks.MOD_ID, "fragile"));
}