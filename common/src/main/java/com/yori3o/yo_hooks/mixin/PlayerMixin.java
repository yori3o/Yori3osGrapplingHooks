package com.yori3o.yo_hooks.mixin;

import com.yori3o.yo_hooks.config.ServerConfig;
import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements PlayerWithHookData {
    private HookEntity hookEntity;
    private Boolean isResetFallDistance = false;

    // Константы для настройки:
    /*private static final double SPRING_CONSTANT = 0.5; // Жесткость пружины
    private static final double DAMPING_CONSTANT = 1;  // Демпфирование (Трение)
    private static final double PUSH_FACTOR = 0.08; // Общий масштаб силы
    private static final double RADIAL_CONTROL = 0.33; // Коэффициент сдерживания радиальной скорости (для маятника)*/

    private static ServerConfig sc = new ServerConfig();

    private static final double SPRING_CONSTANT = sc.get().hooksStiffness;
    private static final double DAMPING_CONSTANT = sc.get().dampingForce;
    private static final double PUSH_FACTOR = sc.get().forceModifier;
    private static final double RADIAL_CONTROL = sc.get().radialControl;

    @Override
    public HookEntity getHook() {
        return this.hookEntity;
    }

    @Override
    public Boolean isResetFallDistance() {
        return this.isResetFallDistance;
    }

    @Override
    public void setHook(HookEntity value) {
        this.hookEntity = value;
    }

    /*public float getEntityJumpStrength(Player entity) {
        return 0.5f;
    }*/

    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        // === HOOK HANDLING ===
        if (this.hookEntity != null && this.hookEntity.isInBlock() && !player.level().isClientSide()) {

            // 1. Векторы и расстояния
            Vec3 hookPos = this.hookEntity.position();
            Vec3 playerEyePos = player.getEyePosition();
            
            Vec3 vecToHook = hookPos.subtract(playerEyePos); // Вектор от игрока к крюку (vec)
            double dist = vecToHook.length(); // Текущая длина (dist)
            float maxChainLength = this.hookEntity.getLength(); // Максимальная длина (length)

            // 2. Равновесная длина (L_равн)
            double equilibriumLength = maxChainLength; 
            
            // 3. Вычисляем только, если цепь натянута или игрок близко к максимальной длине
            if (dist > equilibriumLength * 0.95) { 
                
                Vec3 unitVector = vecToHook.normalize(); // Единичный вектор к крюку (û)
                Vec3 playerVelocity = player.getDeltaMovement(); // Текущая скорость игрока (v)
                
                // --- СИЛА УПРУГОСТИ (F_упруг) ---
                double stretch = dist - equilibriumLength;
                double springForceMagnitude = SPRING_CONSTANT * stretch; 
                
                // Если цепь сжимается (stretch < 0), силы нет.
                if (stretch < 0) { 
                    springForceMagnitude = 0; 
                }
                
                Vec3 springForce = unitVector.scale(springForceMagnitude);
                
                // --- СИЛА ДЕМПФИРОВАНИЯ (F_демпф) ---
                double velocityProjection = playerVelocity.dot(unitVector); // v · û
                double dampingMagnitude = -DAMPING_CONSTANT * velocityProjection;
                
                Vec3 dampingForce = unitVector.scale(dampingMagnitude);
                
                // --- ИТОГОВАЯ СИЛА (F_общ) ---
                Vec3 totalForce = springForce.add(dampingForce);
                
                // Новый блок применения силы (Заменяет player.setDeltaMovement):

                // 1. Расчеты V_рад (которая уводит от круга)
                double radialSpeed = playerVelocity.dot(unitVector); 
                Vec3 radialVelocity = unitVector.scale(radialSpeed);

                // 2. Сила, необходимая для остановки V_рад (Центростремительный импульс)
                // Мы должны применить силу, противоположную V_рад, и добавить F_общ.
                // (1 / PUSH_FACTOR) - это обратная величина для силы.
                Vec3 antiRadialImpulse = radialVelocity.scale(-1.0 * RADIAL_CONTROL); 

                // 3. Добавление F_общ (Пружина/Демпфер)
                Vec3 pushForce = totalForce.scale(PUSH_FACTOR);

                // 4. Итоговая сила (контроль V_рад + F_общ)
                // Применяем силы с помощью push (позволяя ванильной игре сохранить V_танг и гравитацию)
                Vec3 finalPush = pushForce.add(antiRadialImpulse);

                if (vecToHook.y > -0.66) {
                    player.push(finalPush.x, finalPush.y, finalPush.z);

                    player.hurtMarked = true; // Отмечаем игрока как измененного для синхронизации
                }

                // 5. Обнуление урона от падения

                if (vecToHook.y > -0.15) {
                    isResetFallDistance = true;
                    if (velocityProjection < 0) { // Игрок падает (velocityProjection < 0)
                        ((LivingEntity) player).resetFallDistance();
                    }
                } else {
                    isResetFallDistance = false;
                }
            }
        }
    }
}
