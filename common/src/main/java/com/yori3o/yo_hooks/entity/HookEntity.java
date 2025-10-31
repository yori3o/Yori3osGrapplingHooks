package com.yori3o.yo_hooks.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yori3o.yo_hooks.item.HookItem;
import com.yori3o.yo_hooks.register.ModEntities;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// ⭐ ИЗМЕНЕНИЕ: Наследуемся от ThrowableProjectile для корректного доступа к конструктору
public class HookEntity extends ThrowableProjectile {

    private static final EntityDataAccessor<Boolean> IN_BLOCK =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> LENGTH =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HOOK_RANGE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);

    //private int MAX_RANGE;
    //private static final double SPEED = 5.0D;

    // 1. КОНСТРУКТОР ДЛЯ РЕГИСТРАЦИИ
    // Вызывается при создании EntityType. Конструктор super(EntityType, Level) теперь должен быть доступен.
    public HookEntity(EntityType<? extends HookEntity> type, Level level) {
        super((EntityType<? extends ThrowableProjectile>) type, level); // каст к родительскому типу
        this.noCulling = true;
    }



    // 2. КОНСТРУКТОР ДЛЯ ЗАПУСКА СУЩНОСТЬЮ (например, игроком)
    public HookEntity(Level level, LivingEntity owner, int hookRange) {
        // Вызываем первый конструктор, передавая зарегистрированный EntityType твоего мода
        this(ModEntities.HOOK_ENTITY.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY(), owner.getZ());
        this.setDeltaMovement(owner.getLookAngle().scale(2.0D));
        //MAX_RANGE = hookRange;
        this.setMaxRange(hookRange);
        //LoggerUtil.LOGGER.info(MAX_RANGE);
    }

    public PoseStack.Pose armPose;

    public void setArmPose(PoseStack.Pose pose) {
        // Копируем матрицу, чтобы она не изменилась в следующем кадре
        this.armPose = pose; 
    }

    @Nullable
    public PoseStack.Pose getArmPose() {
        return this.armPose;
    }
    

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(IN_BLOCK, false);
        builder.define(LENGTH, 0.0F);
        builder.define(HOOK_RANGE, 0);
    }

    @Override
    public void tick() {
        super.tick();

        Player player = this.getPlayerOwner();
        // Проверка владельца и дистанции (твоя оригинальная логика)
        if (player == null || (!this.level().isClientSide && this.discardIfInvalid(player))) {
            this.discard();
            return;
        }
        
        // Если крюк зацепился, останавливаем его движение
        if (this.isInBlock()) {
            this.setDeltaMovement(Vec3.ZERO); 
            return;
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        this.checkInsideBlocks();
    }

    private boolean discardIfInvalid(Player player) {
        if (!player.isAlive() || player.isRemoved() || !((player.getMainHandItem().getItem() instanceof HookItem) || (player.getOffhandItem().getItem() instanceof HookItem)) || this.distanceToSqr(player) > getMaxRange()) { // можно использовать .getMainHandItem() для получения предмета в руке вместо .isHolding
            this.discard();
            return true;
        }
        return false;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.setDeltaMovement(Vec3.ZERO);
        this.setInBlock(true);
        this.setPos(result.getLocation()); // Устанавливаем точную позицию попадания

        Player player = this.getPlayerOwner();
        if (player != null) {
            double dist = player.getEyePosition().subtract(result.getLocation()).length();
            this.setLength(Math.max((float) dist * 0.5F - 3.0F, 1.5F));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("in_block", this.isInBlock());
        tag.putFloat("length", this.getLength());
        tag.putInt("hook_range", this.getMaxRange());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setInBlock(tag.getBoolean("in_block"));
        this.setLength(tag.getFloat("length"));
        this.setMaxRange(tag.getInt("hook_range"));
    }

    private void setInBlock(boolean inBlock) {
        this.entityData.set(IN_BLOCK, inBlock);
    }

    private void setLength(float length) {
        this.entityData.set(LENGTH, length);
    }

    private void setMaxRange(int MAX_RANGE) {
        this.entityData.set(HOOK_RANGE, MAX_RANGE);
    }

    public boolean isInBlock() {
        return this.entityData.get(IN_BLOCK);
    }

    public float getLength() {
        return this.entityData.get(LENGTH);
    }

    public int getMaxRange() {
        return this.entityData.get(HOOK_RANGE);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public void remove(RemovalReason reason) {
        this.setHookForPlayer(null);
        super.remove(reason);
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        this.setHookForPlayer(this);
    }

    private void setHookForPlayer(@Nullable HookEntity hookEntity) {
        Player player = this.getPlayerOwner();
        if (player instanceof PlayerWithHookData data) {
            data.setHook(hookEntity);
        }
    }

    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player) entity : null;
    }

    /*@Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(Entity serverEntity) {
        Entity owner = this.getOwner();
        // В 1.21.1 метод getAddEntityPacket требует параметра Entity, который можно получить из ServerLevel
        // Однако, для ClientboundAddEntityPacket (старый способ) или более новых методов,
        // тебе нужно будет адаптировать этот код под Architectury/1.21.1,
        // но для базовой совместимости оставим так, как было, заменив ServerEntity на Entity.
        return new ClientboundAddEntityPacket(this, owner == null ? this.getId() : owner.getId());
    }*/

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        if (this.getPlayerOwner() == null) {
            this.kill();
        }
    }
}