package com.yori3o.yo_hooks.common.entity;


import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.sound.SoundRegistry;
import com.yori3o.yo_hooks.common.util.HookStackState;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.init.TagRegistry;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.interfaces.PlayerWithHookData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
//? if >=26.1 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//? } else {
/*import net.minecraft.nbt.CompoundTag;
*///? }
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;



public class HookEntity extends ThrowableProjectile {


    private static final EntityDataAccessor<Boolean> IN_BLOCK =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);
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
            
//? if <26.1 {
/*private static final EntityDataAccessor<Boolean> IN_SABLE_BLOCK = SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<net.minecraft.nbt.CompoundTag> SABLE_ATTACH_POS =
        SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public boolean isInSableBlock() { return entityData.get(IN_SABLE_BLOCK); }
    private void setInSableBlock(boolean value) { entityData.set(IN_SABLE_BLOCK, value); }
    public void setSableAttachPos(Vec3 pos) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putDouble("x", pos.x); tag.putDouble("y", pos.y); tag.putDouble("z", pos.z);
        entityData.set(SABLE_ATTACH_POS, tag);
    }
    @Nullable
    public Vec3 getSableAttachPos() {
        var tag = entityData.get(SABLE_ATTACH_POS);
        return tag.isEmpty() ? null : new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
    }
*///? }

    public int damageOnHit = 1;
    private boolean hookedOnFallingBlock = false;
    private boolean clientOwnerLinked;
    private long lastClimbTick = Long.MIN_VALUE;
    private long lastClimbSoundTick = Long.MIN_VALUE;

    public boolean acceptClimb(long tick) {
        if (lastClimbTick == tick) return false;
        lastClimbTick = tick;
        return true;
    }

    public boolean acceptClimbSound(long tick) {
        if (lastClimbSoundTick != Long.MIN_VALUE && tick - lastClimbSoundTick < 39) return false;
        lastClimbSoundTick = tick;
        return true;
    }

    public ItemStack getHeldStack(Player player) {
        InteractionHand hand = getHoldingHand(player);
        return hand == null ? ItemStack.EMPTY : player.getItemInHand(hand);
    }

    @Nullable
    public InteractionHand getHoldingHand(Player player) {
        if (HookStackState.belongsTo(player.getMainHandItem(), this)) return InteractionHand.MAIN_HAND;
        if (HookStackState.belongsTo(player.getOffhandItem(), this)) return InteractionHand.OFF_HAND;
        return null;
    }

    public boolean isControlledBy(Player player) {
        return !isRemoved() && player.isAlive() && !player.isRemoved()
            && player.level() == level() && getPlayerOwner() == player
            && ((PlayerWithHookData) player).yo_hooks$getHook() == this
            && !getHeldStack(player).isEmpty() && distanceToPlayer(player) <= getMaxRange();
    }


    public HookEntity(EntityType<? extends HookEntity> type, Level level) {
        super((EntityType<? extends ThrowableProjectile>) type, level);
        //? if <26.1 {
        /*this.noCulling = true;
        *///? }
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
//? if <26.1 {
/*builder.define(IN_SABLE_BLOCK, false);
        builder.define(SABLE_ATTACH_POS, new net.minecraft.nbt.CompoundTag());
*///? }

        builder.define(LENGTH, 0.0F);
        builder.define(HOOK_RANGE, 0);
        builder.define(HOOK_ITEM_MATERIAL, "");
        builder.define(BLOCK_POS, new BlockPos(0, -99999, 0));
        builder.define(PLAYER_UUID, "");
        builder.define(GENTLE_TOUCH, false);
        builder.define(AGILITY_LEVEL, 0);
    }

    
    @Override
    public void tick() {
        if (this.isRemoved()) return;
        Player owner = this.getPlayerOwner();
        if (owner == null) {
            // Spawn and metadata packets may arrive on different client ticks.
            // Never remove a client entity just because its owner is not available yet.
            String uuid = getPlayerUUID();
            if (!uuid.isEmpty()) {
                try { owner = level().getPlayerByUUID(UUID.fromString(uuid)); }
                catch (IllegalArgumentException ignored) { }
            }
            if (owner != null) setOwner(owner);
            else {
                if (!level().isClientSide()) discard();
                return;
            }
        }
        if (!level().isClientSide()) {
            PlayerWithHookData data = (PlayerWithHookData) owner;
            if (data.yo_hooks$getHook() == null && !getHeldStack(owner).isEmpty()) data.yo_hooks$setHook(this);
            if (discardIfInvalid(owner)) return;
        } else if (!clientOwnerLinked) {
            // getOwner() can resolve a previously missing entity without calling setOwner().
            setOwner(owner);
        }
        // Validate before collision callbacks can damage an item or change the world.
        super.tick();
        if (this.isRemoved()) return;
//? if <26.1 {
/*if (com.yori3o.yo_hooks.common.compat.Compats.isSableLoaded && isInSableBlock()) {
            com.yori3o.yo_hooks.common.compat.sable.SableCompat.resolveAttachment(this);
        }
*///? }


        if (!level().isClientSide()) {
            if (this.entityData.get(BLOCK_POS).getY() != -99999) {
                if (this.level().getBlockState(this.entityData.get(BLOCK_POS)).isAir() && this.isNoGravity()) {
                    this.discard();
                    if (hookedOnFallingBlock) {
                        ((PlayerWithHookData) owner).yo_hooks$setSuddenFall(true);
                        hookedOnFallingBlock = false;
                    }
                    return;
                }
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
        boolean outOfRange = this.distanceToPlayer(player) > getMaxRange();
        if (isControlledBy(player)) return false;
        if (outOfRange) playBreakSound();
        this.discard();

        return true;
    }

    public double distanceToPlayer(Player player) {
        //? if <26.1 {
        /*if (com.yori3o.yo_hooks.common.compat.Compats.isSableLoaded && isInSableBlock()) {
            Vec3 position = com.yori3o.yo_hooks.common.compat.sable.SableCompat.getHookPos(position(), this);
            return position == null ? 0 : position.distanceTo(player.position());
        }
        *///? }
        return distanceTo(player);
    }

    private void playBreakSound() {
        this.level().playSound(null,
            getPlayerOwner(),
            SoundRegistry.getBreakingSound(getHookItemMaterial()),
            SoundSource.PLAYERS,
            1.0f, 1.0f
        );
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

        return entity instanceof LivingEntity && super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide() && !isInBlock()) {
            Entity target = result.getEntity();

            if (target instanceof LivingEntity living) {
                Entity owner = this.getOwner();

                DamageSource source = this.damageSources().thrown(this, owner);

                //? if >=26.1 {
living.hurtServer((ServerLevel)this.level(), source, damageOnHit);
//? } else {
/*living.hurt(source, damageOnHit);
*///? }

                this.setDeltaMovement(this.getDeltaMovement().scale(0.25f));
            }
        }
    }

    
    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (isInBlock() || isRemoved()) return;
        super.onHitBlock(result);

        if (!level().isClientSide()) {

//? if <26.1 {
/*if (com.yori3o.yo_hooks.common.compat.Compats.isSableLoaded) {
                setInSableBlock(com.yori3o.yo_hooks.common.compat.sable.SableCompat.onHookHitBlock(this, result));
            }
*///? }

            setBlockPos(result.getBlockPos());

            Player player = this.getPlayerOwner();
            if (player == null) return;

            Level level = this.level();

            BlockPos pos = this.entityData.get(BLOCK_POS);
            BlockState bs = level.getBlockState(pos);

            if (!ConfigManager.server().blocksBlacklist.isEmpty()) {
                boolean isThisBlockBanned = (ConfigManager.server().blocksBlacklist.contains(BuiltInRegistries.BLOCK.getKey(bs.getBlock()).toString()));
                if (ConfigManager.server().whitelistMode) isThisBlockBanned = !isThisBlockBanned;
                if (isThisBlockBanned) {
                    this.discard();
                    return;
                }
            }

            if (!ConfigManager.common().funnyMode) {
                if (!player.isCreative()) {
                    ItemStack stack = getHeldStack(player);
                    EquipmentSlot hand = stack == player.getMainHandItem() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    if (stack.isEmpty()) { discard(); return; }
                    stack.hurtAndBreak(1, player, hand);
                    if (stack.isEmpty()) { discard(); return; }
                }
            }
            if (ConfigManager.server().breakingFragileBlocks && player.mayBuild() && level.mayInteract(player, pos)) {
                if (!this.isGentleTouch()) {
                    if (bs.is(TagRegistry.FRAGILE_BLOCKS)) {
                        level.destroyBlock(pos, true);
                        this.discard();
                        ((PlayerWithHookData) player).yo_hooks$setSuddenFall(true);
                        return;
                    }
                    if (bs.getBlock() instanceof FallingBlock) {
                        level.scheduleTick(pos, bs.getBlock(), 1);
                        hookedOnFallingBlock = true;
                    } else if (bs.getBlock() instanceof RedStoneOreBlock) {
                        com.yori3o.yo_hooks.common.mixin.RedStoneOreBlockAccess.yo_hooks$interact(bs, level, pos);
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
//? if <26.1 {
/*if (com.yori3o.yo_hooks.common.compat.Compats.isSableLoaded) {
                    dist = com.yori3o.yo_hooks.common.compat.sable.SableCompat.calculateDist(this, player, result);
                }
*///? }

                this.setLength((float)dist);
            }
        }
    }

//? if >=26.1 {
@Override
    protected void addAdditionalSaveData(ValueOutput tag)
//? } else {
/*@Override
    protected void addAdditionalSaveData(CompoundTag tag)
*///? }
    {
//? if <26.1 {
/*tag.putBoolean("in_sable_block", this.isInSableBlock());
        tag.put("sable_attach_pos", entityData.get(SABLE_ATTACH_POS).copy());
*///? }

        super.addAdditionalSaveData(tag);
        tag.putBoolean("gentle_touch", isGentleTouch());
        tag.putInt("damage_on_hit", damageOnHit);
        tag.putBoolean("falling_block", hookedOnFallingBlock);
        tag.putBoolean("in_block", this.isInBlock());
        tag.putFloat("length", this.getLength());
        tag.putInt("hook_range", this.getMaxRange());
        tag.putString("hook_item_material", this.getHookItemMaterial());
        tag.putInt("hook_pos_x", this.getPosX());
        tag.putInt("hook_pos_y", this.getPosY());
        tag.putInt("hook_pos_z", this.getPosZ());
        tag.putString("player_uuid", this.getPlayerUUID());
        tag.putInt("agility_level", this.getAgilityLevel());
    }

//? if >=26.1 {
@Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        setGentleTouch(tag.getBooleanOr("gentle_touch", false));
        damageOnHit = tag.getIntOr("damage_on_hit", 1);
        hookedOnFallingBlock = tag.getBooleanOr("falling_block", false);
        this.setInBlock(tag.getBooleanOr("in_block", false));
        setNoGravity(isInBlock());
        this.setLength(tag.getFloatOr("length", 0f));
        this.setMaxRange(tag.getIntOr("hook_range", 0));
        this.setHookItemMaterial(tag.getStringOr("hook_item_material", ""));
        this.setBlockPos(new BlockPos(tag.getIntOr("hook_pos_x", 0), tag.getIntOr("hook_pos_y", 0), tag.getIntOr("hook_pos_z", 0)));
        this.setPlayerUUID(tag.getStringOr("player_uuid", ""));
        this.setAgilityLevel(tag.getIntOr("agility_level", 0));
    }
//? } else {
/*@Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setGentleTouch(tag.getBoolean("gentle_touch"));
        damageOnHit = tag.contains("damage_on_hit") ? tag.getInt("damage_on_hit") : 1;
        hookedOnFallingBlock = tag.getBoolean("falling_block");
        this.setInSableBlock(tag.getBoolean("in_sable_block"));
        entityData.set(SABLE_ATTACH_POS, tag.getCompound("sable_attach_pos").copy());
        if (isInSableBlock() && getSableAttachPos() == null) setSableAttachPos(position());
        this.setInBlock(tag.getBoolean("in_block"));
        setNoGravity(isInBlock());
        this.setLength(tag.getFloat("length"));
        this.setMaxRange(tag.getInt("hook_range"));
        this.setHookItemMaterial(tag.getString("hook_item_material"));
        this.setBlockPos(new BlockPos(tag.getInt("hook_pos_x"), tag.getInt("hook_pos_y"), tag.getInt("hook_pos_z")));
        this.setPlayerUUID(tag.getString("player_uuid"));
        this.setAgilityLevel(tag.getInt("agility_level"));
    }
*///? }

    // --- in block flag ---
    private void setInBlock(boolean inBlock) {
        this.entityData.set(IN_BLOCK, inBlock);
    }
    public boolean isInBlock() {
        return this.entityData.get(IN_BLOCK);
    }


    // --- length ---
    public void setLength(float length) {
        if (Float.isFinite(length)) this.entityData.set(LENGTH, Math.max(0.4f, length));
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
        var supplier = ItemRegistry.HOOK_HEADS.get(hookMaterial);
        Item i = supplier == null ? null : supplier.get();
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
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public void remove(RemovalReason reason) {
        Player player = getPlayerOwner();
        if (player instanceof PlayerWithHookData data) {
            // An old despawn packet must not detach a more recent cast.
            if (data.yo_hooks$getHook() == this) data.yo_hooks$setHook(null);
            if (!level().isClientSide() && reason.shouldDestroy()) HookStackState.clearCast(player, this);
        }
        super.remove(reason);
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        Player previous = getPlayerOwner();
        if (previous != entity && previous instanceof PlayerWithHookData data && data.yo_hooks$getHook() == this) {
            data.yo_hooks$setHook(null);
        }
        super.setOwner(entity);
        if (previous != entity) clientOwnerLinked = false;
        if (entity instanceof PlayerWithHookData data) {
            HookEntity current = data.yo_hooks$getHook();
            if (current == null || current.isRemoved() || current == this
                || (level().isClientSide() && !getHeldStack((Player) entity).isEmpty())) {
                data.yo_hooks$setHook(this);
                clientOwnerLinked = true;
            }
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
