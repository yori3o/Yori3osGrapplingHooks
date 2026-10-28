package com.yori3o.yo_hooks.common.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RedStoneOreBlock.class)
public interface RedStoneOreBlockAccess {
    @Invoker("interact")
    static void yo_hooks$interact(BlockState state, Level level, BlockPos pos) {
        throw new AssertionError("Mixin invoker was not applied");
    }
}
