package com.yori3o.yo_hooks.common.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Calls vanilla {@code RedStoneOreBlock.interact} without a mixin invoker, so a failed
 * accessor mixin cannot leave an invalid class that crashes the server on first hook hit.
 */
public final class RedstoneOreInteract {

    private static volatile MethodHandle interactHandle;
    private static volatile boolean resolveAttempted;

    private RedstoneOreInteract() {}

    public static void invoke(BlockState state, Level level, BlockPos pos) {
        if (!resolveAttempted) {
            resolve();
        }
        MethodHandle h = interactHandle;
        if (h == null) {
            return;
        }
        try {
            h.invoke(state, level, pos);
        } catch (Throwable ignored) {
            // If reflection breaks at runtime, skip lighting the ore rather than crashing.
        }
    }

    private static synchronized void resolve() {
        if (resolveAttempted) {
            return;
        }
        resolveAttempted = true;
        try {
            Method m = RedStoneOreBlock.class.getDeclaredMethod(
                    "interact", BlockState.class, Level.class, BlockPos.class);
            m.setAccessible(true);
            interactHandle = MethodHandles.lookup().unreflect(m);
        } catch (ReflectiveOperationException ignored) {
            interactHandle = null;
        }
    }
}
