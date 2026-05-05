package com.yori3o.yo_hooks.common.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


/**
 * This mixin fixes a bug where the hook would twitch when hitting an entity.
 */
@Mixin(RedStoneOreBlock.class)
public abstract class RedStoneOreBlockAccessor {

    @Invoker("interact")
    public static void interact(BlockState bs, Level l, BlockPos pos) {
        throw new AssertionError();
    }

}