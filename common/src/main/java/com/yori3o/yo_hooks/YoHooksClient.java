package com.yori3o.yo_hooks;

import com.mojang.blaze3d.platform.InputConstants;
import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.network.JumpClientSend;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;

public class YoHooksClient {

    public static final KeyMapping JUMP = new KeyMapping(
    "key.yo_hooks.jump", // The translation key of the name shown in the Controls screen
    InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
    InputConstants.KEY_SPACE, // The default keycode
    "category.yo_hooks" // The category translation key used to categorize in the Controls screen 
    );

    public static void OnlyClientInit() {
        KeyMappingRegistry.register(JUMP);

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            // ❗️ 1. Получаем текущего игрока
            PlayerWithHookData clientPlayer = (PlayerWithHookData) minecraft.player;
            
            // Проверка, что игрок существует (чтобы избежать NPE при загрузке)
            if (clientPlayer == null) {
                return;
            }
            
            // ❗️ 2. Проверка активного крюка
            // Вам нужно иметь доступ к вашему полю hookEntity, которое есть у игрока.
            // Если hookEntity хранится в вашем PlayerMixin, вам понадобится каст.
            // Предположим, что у игрока есть метод getHookEntity() или доступ через Mixin-интерфейс.
            
            // Пример с кастом (если у вас есть интерфейс/Mixin)
            HookEntity hook = clientPlayer.getHook(); // Замените на ваш фактический каст/метод!
            
            
            // Проверяем: 1) Нажатие кнопки И 2) Активный крюк
            while (JUMP.consumeClick() && hook != null && hook.isInBlock()) { 
                JumpClientSend.PlayerJumpedFromHook();
            }
        });
    }
    
}
