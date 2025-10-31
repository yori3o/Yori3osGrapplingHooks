package com.yori3o.yo_hooks.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.yori3o.yo_hooks.config.ServerConfig;
import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import dev.architectury.networking.NetworkManager;


public class JumpServerPacket {

    static ServerConfig sc = new ServerConfig();

    private static final boolean isJumpAllowed_onConfig = sc.get().isJumpAllowed;
    private static final float decreaseSatiety = sc.get().decreaseSatiety;

    @SuppressWarnings("removal")
    public static void registerPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ResourceLocation.fromNamespaceAndPath("yo_hooks", "player_jumped_from_hook"), (buf, context) -> {

            //int data = buf.readInt();

            // ❗️ 1. Получаем игрока, отправившего пакет (это должен быть ServerPlayer)
            PlayerWithHookData serverPlayer = (PlayerWithHookData) context.getPlayer();
            
            if (serverPlayer == null) {
                return;
            }

            // ❗️ 2. Переключаемся на главный поток сервера для работы с игроком
            context.queue(() -> {
                
                // 3. Проверка и логика прыжка
                // Опять же, нужен доступ к this.hookEntity через Mixin/Accessor!
                // Замените на ваш фактический каст/метод!
                HookEntity hook = serverPlayer.getHook(); 
                Boolean allowed = serverPlayer.isResetFallDistance();
                
                if (hook != null && hook.isInBlock() && allowed && isJumpAllowed_onConfig) {
                    context.getPlayer().getFoodData().addExhaustion(decreaseSatiety / 3f);
                    
                    // --- ЛОГИКА ПРЫЖКА ---
                    // 4. Отцепляем крюк
                    hook.discard(); 
                    // 5. Применяем импульс
                    applyJumpImpulse(context.getPlayer()); 
                }
            });

        });
    }

    public static void applyJumpImpulse(Player player) {
        // ❗️ Настройте эти константы для желаемой дальности и высоты прыжка
        final double FORWARD_VELOCITY = 0.62; // Сила броска вперед
        final double UPWARD_VELOCITY = 0.42;   // Сила броска вверх
        
        Vec3 lookVector = player.getLookAngle(); // Вектор, куда смотрит игрок
        Vec3 currentMotion = player.getDeltaMovement();
        
        // Применяем горизонтальную скорость по направлению взгляда
        double newX = lookVector.x * FORWARD_VELOCITY;
        double newZ = lookVector.z * FORWARD_VELOCITY;
        
        // Применяем вертикальную скорость:
        // Мы берем максимальное из: текущая вертикальная скорость ИЛИ желаемый UPWARD_VELOCITY.
        // Это не даст вертикальной скорости быть отрицательной (падение), если игрок смотрит вверх.
        double newY = Math.max(currentMotion.y, UPWARD_VELOCITY); 
        
        player.setDeltaMovement(newX, newY, newZ);
        
        player.hurtMarked = true; // Отмечаем, что движение изменилось
        player.resetFallDistance(); // Сбрасываем урон от падения
    }
}
