package com.yori3o.yo_hooks.mixin;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {

    @Shadow private InputConstants.Key key;

    private static final Map<InputConstants.Key, KeyMapping> YO_MAP = new HashMap<>();


    /*
        This mixin redirects yo hooks keybinds not to the vanilla map, but to a custom one.
        This is necessary so that they can be used simultaneously, even if the key is occupied (space, right-click, etc.)
    */
    @Redirect(
        method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1)
    )
    private Object redirectMapPut(Map<InputConstants.Key, KeyMapping> map, Object key, Object value) {
        
        KeyMapping mapping = (KeyMapping) value;
        InputConstants.Key inputKey = (InputConstants.Key) key;

        if (mapping.getName().startsWith("key.yo_hooks.")) {
            return YO_MAP.put(inputKey, mapping);
        }

        return map.put(inputKey, mapping);
    }


    /*
        This mixin activates custom keybinds from a custom map.
    */
    @Inject(method = "set", at = @At("TAIL"))
    private static void yo$afterSet(InputConstants.Key key, boolean down, CallbackInfo ci) {
        
        KeyMapping km = YO_MAP.get(key);
        if (km != null) {
            km.setDown(down);
        }
    }


    /*
        This 2 mixins is needed for the reload to work correctly, i.e. when the player changes the key to another.
    */
    @Redirect(
        method = "resetMapping",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private static Object redirectResetPut(Map<InputConstants.Key, KeyMapping> map, Object key, Object value) {
        
        KeyMapping mapping = (KeyMapping) value;
        InputConstants.Key inputKey = (InputConstants.Key) key;

        if (mapping.getName().startsWith("key.yo_hooks.")) {
            return YO_MAP.put(inputKey, mapping);
        }
        return map.put(inputKey, mapping);
    }

    @Inject(
        method = "resetMapping",
        at = @At("HEAD")
    )
    private static void onResetStart(CallbackInfo ci) {
        
        YO_MAP.clear();
    }
    

}
