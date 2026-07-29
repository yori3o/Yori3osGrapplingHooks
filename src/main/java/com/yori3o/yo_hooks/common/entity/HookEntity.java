package com.yori3o.yo_hooks.common.entity;


import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.compat.sable.HookWithSableData;
import com.yori3o.yo_hooks.common.compat.sable.SableCompat;
import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.sound.SoundRegistry;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.init.TagRegistry;
import com.yori3o.yo_hooks.common.util.LoggerUtil;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;



public class HookEntity extends ThrowableProjectile {


    private static final EntityDataAccessor<Boolean> IN_BLOCK =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IN_SABLE_BLOCK;
    private static final EntityDataAccessor<Float> LENGTH =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HOOK_RANGE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> HOOK_ITEM_MATERIAL = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<BlockPos> BLOCK_POS = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> GENTLE_TOUCH =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);        
    private static final EntityDataAccessor<String> PLAYER_UUID = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> AGILITY_LEVEL = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);

    static {
        if (Compats.isSableLoaded) {
            IN_SABLE_BLOCK = SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);
        } else {
            IN_SABLE_BLOCK = null;
        }
    }
            
    public int damageOnHit = 1;
    private boolean hookedOnFallingBlock = false;


    public HookEntity(EntityType<? extends HookEntity> type, Level level) {
        super((EntityType<? extends ThrowableProjectile>) type, level);
        this.noCulling = true;
    }

    public HookEntity(Level level, LivingEntity owner, int hookRange, ItemStack itemStack, int agilityLevel, boolean gentleTouch, int damageOnHit) {
        this(EntityRegistry.HOOK_ENTITY.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY(), owner.getZ());
        this.setMaxRange(hookRange);
        this.setHookItemMaterial(((HookItem)(itemStack.getItem())).hookDefinition.id);
        this.setPlayerUUID(owner.getUUID().toString());
        this.setAgilityLevel(agilityLevel); 
        this.setGentleTouch(gentleTouch);
        this.damageOnHit = damageOnHit;
        this.setDeltaMovement(owner.getLookAngle().scale(PhysicVariables.hookSpeed + (agilityLevel * 0.25)));
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(IN_BLOCK, false);
        if (Compats.isSableLoaded) builder.define(IN_SABLE_BLOCK, false);
        builder.define(LENGTH, 0.0F);
        builder.define(HOOK_RANGE, 0);
        builder.define(HOOK_ITEM_MATERIAL, "");
        builder.define(BLOCK_POS, new BlockPos(0, -99999, 0));
        builder.define(PLAYER_UUID, "");
        builder.define(GENTLE_TOUCH, false);
        builder.define(AGILITY_LEVEL, 0);
        LoggerUtil.info("defineSynchedData");
    }

    
    @Override
    public void tick() {
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

LoggerUtil.info(level().isClientSide + " " + hit.getType());
        super.tick();
        if (this.isRemoved()) return;

        LoggerUtil.info(level().isClientSide + " " + tickCount + " " + getHookItemMaterial());

        Player owner = this.getPlayerOwner();   

        // This assigns the hook to the owner when re-entering the world
        if (owner == null) {
            String uuid = this.getPlayerUUID();
            if (uuid.isEmpty()) {
                this.discard();
                return;
            }
            Player player = level().getPlayerByUUID(UUID.fromString(uuid));
            if (player != null) {
                setOwner(player);
                owner = player;
            } else {
                return;
            }
        }

        if (!level().isClientSide()) {

            if (Compats.isSableLoaded && this.isInSableBlock()) {
                HookWithSableData data = SableCompat.hooks.get(this);
                if (data == null) {
                    if (!SableCompat.onHookHitBlock(this, this.position())) {
                        ((PlayerWithHookData) owner).setHook(null);
                        this.discard();
                        return;
                    }
                }
            }
            
            if (this.discardIfInvalid(owner)) {
                return;
            }

            if (this.entityData.get(BLOCK_POS).getY() != -99999) {
                if (this.level().getBlockState(this.entityData.get(BLOCK_POS)).isAir() && this.isNoGravity()) {
                    this.discard();
                    if (hookedOnFallingBlock) {
                        ((PlayerWithHookData) owner).setSuddenFall(true);
                        hookedOnFallingBlock = false;
                    }
                    return;
                }
            }

            if (((PlayerWithHookData) owner).getHook() == null) {
                ((PlayerWithHookData) owner).setHook(this);
            }

            if (this.random.nextFloat() > 0.9955) {
                this.level().playSound(null,
                    this,
                    SoundRegistry.getAmbientSound(getHookItemMaterial()),
                    SoundSource.AMBIENT,
                    1.0f, 1.0f
                );
            }
        }
    }

    private boolean discardIfInvalid(Player player) {
        if (!player.isAlive() || player.isRemoved() 
                || !((player.getMainHandItem().getItem() instanceof HookItem) 
                || (player.getOffhandItem().getItem() instanceof HookItem)) 
                || this.distanceTo(player) > getMaxRange()) {
            ((PlayerWithHookData) player).setHook(null);
            this.discard();
            return true;
        }
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        
        if (entity == this.getOwner()) return false;
        if (!entity.isAlive()) return false;
        if (entity.getFirstPassenger() == this.getOwner()) return false;

        return entity instanceof LivingEntity;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide()) {
            Entity target = result.getEntity();

            if (target instanceof LivingEntity living) {
                Entity owner = this.getOwner();

                DamageSource source = this.damageSources().thrown(this, owner);

                living.hurt(source, damageOnHit);

                this.setDeltaMovement(this.getDeltaMovement().scale(0.25f));
            }
        }
    }

    
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        LoggerUtil.info("onHookHitBlockl 0");

        if (Compats.isSableLoaded) {
            LoggerUtil.info("onHookHitBlockl -1");
            if (SableCompat.onHookHitBlock(this, result.getLocation())) {
                this.setInSableBlock(true);
            }
        }

        if (!level().isClientSide()) {

            setBlockPos(result.getBlockPos());

            Player player = this.getPlayerOwner();
            if (player == null) return;

            Level level = this.level();

            BlockPos pos = this.entityData.get(BLOCK_POS);
            BlockState bs = level.getBlockState(pos);

            if (!ConfigManager.server().blocksBlacklist.isEmpty()) {
                boolean isThisBlockBanned = (ConfigManager.server().blocksBlacklist.contains(bs.getBlockHolder().getRegisteredName()));
                if (ConfigManager.server().whitelistMode) isThisBlockBanned = !isThisBlockBanned;
                if (isThisBlockBanned) {
                    this.discard();
                    ((PlayerWithHookData) player).setHook(null);
                    return;
                }
            }

            if (!ConfigManager.common().funnyMode) {
                if (!player.isCreative()) {
                    ItemStack stack = player.getMainHandItem();
                    EquipmentSlot hand = EquipmentSlot.MAINHAND;
                    if (!(stack.getItem() instanceof HookItem)) {
                        stack = player.getOffhandItem();
                        hand = EquipmentSlot.OFFHAND;
                    }
                    stack.hurtAndBreak(1, player, hand);
                }
            }
            if (ConfigManager.server().breakingFragileBlocks) {
                if (!this.isGentleTouch()) {
                    if (bs.is(TagRegistry.FRAGILE_BLOCKS)) {
                        level.destroyBlock(pos, true);
                        this.discard();
                        ((PlayerWithHookData) player).setHook(null);
                        ((PlayerWithHookData) player).setSuddenFall(true);
                        return;
                    }
                    if (bs.getBlock() instanceof FallingBlock) {
                        level.scheduleTick(pos, bs.getBlock(), 1);
                        hookedOnFallingBlock = true;
                    } else if (bs.getBlock() instanceof RedStoneOreBlock) {
                        RedStoneOreBlock.interact(bs, level, pos);
                    }
                }
            }

            level.playSound(null,
                getX(), getY(), getZ(),
                bs.getSoundType().getHitSound(),
                SoundSource.PLAYERS,
                0.22f, 1.15f
            );
            level.playSound(null,
                getX(), getY(), getZ(),
                SoundRegistry.getHitSound(getHookItemMaterial()),
                SoundSource.PLAYERS,
                0.26f, 1f
            );

            this.setDeltaMovement(Vec3.ZERO);
            this.setInBlock(true);
            this.setPos(result.getLocation());
            this.setNoGravity(true);
            
            if (player != null) {
                double dist = player.getEyePosition().subtract(result.getLocation()).length();
                this.setLength((float)dist);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("in_block", this.isInBlock());
        if (Compats.isSableLoaded) tag.putBoolean("in_sable_block", this.isInSableBlock());
        tag.putFloat("length", this.getLength());
        tag.putInt("hook_range", this.getMaxRange());
        tag.putString("hook_item_material", this.getHookItemMaterial());
        tag.putInt("hook_pos_x", this.getPosX());
        tag.putInt("hook_pos_y", this.getPosY());
        tag.putInt("hook_pos_z", this.getPosZ());
        tag.putString("player_uuid", this.getPlayerUUID());
        tag.putInt("agility_level", this.getAgilityLevel());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setInBlock(tag.getBoolean("in_block"));
        if (Compats.isSableLoaded) this.setInSableBlock(tag.getBoolean("in_sable_block"));
        this.setLength(tag.getFloat("length"));
        this.setMaxRange(tag.getInt("hook_range"));
        this.setHookItemMaterial(tag.getString("hook_item_material"));
        this.setBlockPos(new BlockPos(tag.getInt("hook_pos_x"), tag.getInt("hook_pos_y"), tag.getInt("hook_pos_z")));
        this.setPlayerUUID(tag.getString("player_uuid"));
        this.setAgilityLevel(tag.getInt("agility_level"));
    }

    // --- in block flag ---
    private void setInBlock(boolean inBlock) {
        this.entityData.set(IN_BLOCK, inBlock);
    }
    public boolean isInBlock() {
        return this.entityData.get(IN_BLOCK);
    }


    // --- in sable block flag ---
    private void setInSableBlock(boolean inBlock) {
        this.entityData.set(IN_SABLE_BLOCK, inBlock);
    }
    public boolean isInSableBlock() {
        return this.entityData.get(IN_SABLE_BLOCK);
    }


    // --- length ---
    public void setLength(float length) {
        this.entityData.set(LENGTH, length);
    }
    public float getLength() {
        return this.entityData.get(LENGTH);
    }


    // --- max range ---
    private void setMaxRange(int range) {
        this.entityData.set(HOOK_RANGE, range);
    }
    public int getMaxRange() {
        return this.entityData.get(HOOK_RANGE);
    }


    // --- Block Pos ---
    public int getPosX() {
        return this.entityData.get(BLOCK_POS).getX();
    }
    public int getPosY() {
        return this.entityData.get(BLOCK_POS).getY();
    }
    public int getPosZ() {
        return this.entityData.get(BLOCK_POS).getZ();
    }
    public BlockPos getBlockPos() {
        return this.entityData.get(BLOCK_POS);
    }
    private void setBlockPos(BlockPos bp) {
        this.entityData.set(BLOCK_POS, bp);
    }


    // --- hook item ---
    public String getHookItemMaterial() {
        return this.entityData.get(HOOK_ITEM_MATERIAL);
    }

    public ItemStack getHeadItem() {
        String hookMaterial = this.entityData.get(HOOK_ITEM_MATERIAL);
        if (hookMaterial.isEmpty()) return new ItemStack(Items.DIRT);
        Item i = ItemRegistry.HOOK_HEADS.get(hookMaterial).get();
        if (i != null) {
            return new ItemStack(i);
        } else {
            return new ItemStack(Items.DIRT); 
        }
    }

    private void setHookItemMaterial(String id) {
        this.entityData.set(HOOK_ITEM_MATERIAL, id);
    }



    // --- player uuid ---
    private void setPlayerUUID(String UUID) {
        this.entityData.set(PLAYER_UUID, UUID);
    }
    public String getPlayerUUID() {
        return this.entityData.get(PLAYER_UUID);
    }


    // --- gentle touch flag ---
    private void setGentleTouch(boolean gentle_touch) {
        this.entityData.set(GENTLE_TOUCH, gentle_touch);
    }
    public boolean isGentleTouch() {
        return this.entityData.get(GENTLE_TOUCH);
    }



    // --- agility enchantment level ---
    private void setAgilityLevel(int level) {
        this.entityData.set(AGILITY_LEVEL, level);
    }
    public int getAgilityLevel() {
        return this.entityData.get(AGILITY_LEVEL);
    }



    @Override
public void recreateFromPacket(ClientboundAddEntityPacket packet) {
    super.recreateFromPacket(packet);
    LoggerUtil.info("recreate, packet:");
    LoggerUtil.info(packet.toString());
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
        return (entity instanceof Player player) ? player : null;
    }
    
    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    @Override
    public boolean canUsePortal(boolean ignorePassenger) {
        return false;
    }
}