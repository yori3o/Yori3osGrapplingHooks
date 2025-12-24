package com.yori3o.yo_hooks.network;

import com.yori3o.yo_hooks.YoHooks;
import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import dev.architectury.networking.NetworkManager;


public class ServerPacketReceiver {


    public static void registerPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ResourceLocation.fromNamespaceAndPath("yo_hooks", "player_jump"), (buf, context) -> {

            Player player = context.getPlayer();
            PlayerWithHookData hookDataPlayer = (PlayerWithHookData) player;
            
            if (hookDataPlayer == null) {
                return;
            }

            HookEntity hook = hookDataPlayer.getHook(); 
            
            context.queue(() -> {
                 
                
                if (hook != null && hook.isInBlock() && hookDataPlayer.isJumpAllowed()) {
                    context.getPlayer().getFoodData().addExhaustion(YoHooks.decreaseSatiety / 3f);

                    Level world = player.level();
                    
                    // --- ЛОГИКА ПРЫЖКА ---
                    // 4. Отцепляем крюк
                    hook.discard(); 
                    // 5. Применяем импульс
                    applyJumpImpulse(context.getPlayer()); 

                    world.playSound(null,
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.FISHING_BOBBER_RETRIEVE,
                            SoundSource.NEUTRAL,
                            1.0f,
                            0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
                    );
                }
            });
        });



        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ResourceLocation.fromNamespaceAndPath("yo_hooks", "check_config"), (buf, context) -> {

            Boolean softHook = buf.readBoolean();

            Player player = context.getPlayer();
            
            if (softHook != YoHooks.softHook) {
                if (YoHooks.softHook) {
                    ((ServerPlayer) player).connection.disconnect(Component.literal("\"yo_hooks_common.json\" doesn't match the server. Change \"softHook\" to true. You can change it without leaving the game."));
                } else {
                    ((ServerPlayer) player).connection.disconnect(Component.literal("\"yo_hooks_common.json\" doesn't match the server. Change \"softHook\" to false. You can change it without leaving the game."));
                }
            }
        });


        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ResourceLocation.fromNamespaceAndPath("yo_hooks", "climb"), (buf, context) -> {

            Boolean up = buf.readBoolean();

            Player player = context.getPlayer();
            PlayerWithHookData hookDataPlayer = (PlayerWithHookData) player;

            
            if (hookDataPlayer == null) {
                return;
            }

            HookEntity hook = hookDataPlayer.getHook(); 
            
            context.queue(() -> {

                if (up) {
                    if (hook.getLength() > 0.4)
                        hook.setLength(hook.getLength() - YoHooks.climbSpeed);
                } else {
                    if (hook.getLength() < hook.getMaxRange() - 2)
                        hook.setLength(hook.getLength() + (float)(YoHooks.climbSpeed * 1.5));
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
