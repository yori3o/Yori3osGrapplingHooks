package com.yori3o.yo_hooks.common.mixin;

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
import com.yori3o.yo_hooks.common.utils.HookActiveProperty;

// Миксин нацелен на ванильный класс ConditionalItemModelProperties
@Mixin(ConditionalItemModelProperties.class)
public class HookActivePropertyMixin {

    // 1. Объявляем теневой (Shadow) доступ к приватному статическому полю ID_MAPPER
    // ВАЖНО: Убедитесь, что имя поля совпадает с обфускацией вашей версии (ID_MAPPER или a).
    // Так как вы предоставили деобфусцированный код, используем ID_MAPPER.
    @Shadow
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ConditionalItemModelProperty>> ID_MAPPER;

    // 2. Внедряем (Inject) наш код в конец статического метода bootstrap()
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void yo_hooks$registerHookProperty(CallbackInfo ci) {
        // Логика регистрации нашего свойства
        Identifier HOOK_ACTIVE_ID = Identifier.fromNamespaceAndPath("yo_hooks", "hook_active");
        
        // ВАЖНО: HookActiveProperty.MAP_CODEC должен быть доступен (public static final)
        ID_MAPPER.put(HOOK_ACTIVE_ID, HookActiveProperty.MAP_CODEC);
        
        // Опционально: вывод в консоль для проверки
        //LoggerUtil.LOGGER.info("YoHooks: Successfully registered conditional item property: " + HOOK_ACTIVE_ID);
    }
}