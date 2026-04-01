package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.util.HookActiveProperty;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(ConditionalItemModelProperties.class)
public class HookActivePropertyMixin {


    @Shadow
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ConditionalItemModelProperty>> ID_MAPPER;

    private static final Identifier HOOK_ACTIVE_ID = Identifier.fromNamespaceAndPath("yo_hooks", "hook_active");

    
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void yo_hooks$registerHookProperty(CallbackInfo ci) {
        ID_MAPPER.put(HOOK_ACTIVE_ID, HookActiveProperty.MAP_CODEC);
    }
}