//? if fabric && <26.1 {
/*package com.yori3o.yo_hooks.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Shadow private int clickCount;
    @Unique private static final Map<InputConstants.Key, List<KeyMapping>> yo_hooks$bindings = new HashMap<>();

    @Unique
    private static Object yo_hooks$register(Map<Object, Object> map, Object key, Object value, Operation<Object> original) {
        KeyMapping mapping = (KeyMapping) value;
        if (!mapping.getName().startsWith("key.yo_hooks.")) return original.call(map, key, value);
        yo_hooks$bindings.computeIfAbsent((InputConstants.Key) key, ignored -> new ArrayList<>()).add(mapping);
        return null;
    }

    // Keep all mappings in vanilla's name registry, but don't let mod actions replace
    // vanilla's one-binding-per-key dispatch (Space, use, movement, etc.).
    @WrapOperation(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    private Object yo_hooks$registerInitial(Map<Object, Object> map, Object key, Object value, Operation<Object> original) {
        return yo_hooks$register(map, key, value, original);
    }

    @Inject(method = "resetMapping", at = @At("HEAD"))
    private static void yo_hooks$clearBindings(CallbackInfo ci) {
        yo_hooks$bindings.clear();
    }

    @WrapOperation(method = "resetMapping",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object yo_hooks$registerRebound(Map<Object, Object> map, Object key, Object value, Operation<Object> original) {
        return yo_hooks$register(map, key, value, original);
    }

    @Inject(method = "set", at = @At("TAIL"))
    private static void yo_hooks$setBindings(InputConstants.Key key, boolean down, CallbackInfo ci) {
        for (KeyMapping mapping : yo_hooks$bindings.getOrDefault(key, List.of())) mapping.setDown(down);
    }

    @Inject(method = "click", at = @At("TAIL"))
    private static void yo_hooks$clickBindings(InputConstants.Key key, CallbackInfo ci) {
        for (KeyMapping mapping : yo_hooks$bindings.getOrDefault(key, List.of())) {
            ((KeyMappingMixin) (Object) mapping).clickCount++;
        }
    }
}
*///? }
