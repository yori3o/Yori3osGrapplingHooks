package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.util.LoggerUtil;
import com.yori3o.yo_hooks.common.util.interfaces.AvatarRendererAccess;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Unique;
import java.util.HashMap;
import java.util.Map;



@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> implements AvatarRendererAccess {

    @Unique
    private final Map<Integer, AvatarRenderState> yo_hooks$states = new HashMap<>();

    @Inject(
        method = "extractRenderState",
        at = @At("TAIL")
    )
    private void yo_hooks$captureState(
        AvatarlikeEntity entity,
        AvatarRenderState state,
        float partialTicks,
        CallbackInfo ci
    ) {
        this.yo_hooks$states.put(entity.getId(), state);
        LoggerUtil.info("dfdf "  + entity.getId());
    }

    @Override
    public AvatarRenderState yo_hooks$getState(int entityId) {
        return this.yo_hooks$states.get(entityId);
    }
}