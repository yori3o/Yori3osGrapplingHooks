package com.yori3o.yo_hooks.common.entity;

import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.sounds.SoundRegistry;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.init.TagRegistry;
import com.yori3o.yo_hooks.common.utils.PhysicVariables;
import com.yori3o.yo_hooks.common.utils.PlayerWithHookData;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
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
    private static final EntityDataAccessor<Float> LENGTH =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HOOK_RANGE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> HEAD_ITEM_ID = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<BlockPos> BLOCK_POS = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> GENTLE_TOUCH =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);        
    private static final EntityDataAccessor<String> PLAYER_UUID = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> AGILITY_LEVEL = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DAMAGE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);


    // 1. КОНСТРУКТОР ДЛЯ РЕГИСТРАЦИИ
    public HookEntity(EntityType<? extends HookEntity> type, Level level) {
        super((EntityType<? extends ThrowableProjectile>) type, level); // каст к родительскому типу
        this.noCulling = true;
    }



    // 2. КОНСТРУКТОР ДЛЯ ЗАПУСКА СУЩНОСТЬЮ (например, игроком)
    public HookEntity(Level level, LivingEntity owner, int hookRange, ItemStack itemStack, int agility_level, boolean gentle_touch, int damageOnHit) {
        this(EntityRegistry.HOOK_ENTITY.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY(), owner.getZ());
        this.setMaxRange(hookRange);
        this.setHeadItem(((HookItem)(itemStack.getItem())).correspondingHead);
        this.setPlayerUUID(owner.getUUID().toString());
        this.setAgilityLevel(agility_level); 
        this.setGentleTouch(gentle_touch);
        this.setDamageOnhit(damageOnHit);
        this.setDeltaMovement(owner.getLookAngle().scale(PhysicVariables.hookSpeed + (agility_level * 0.25)));
    }
    

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(IN_BLOCK, false);
        builder.define(LENGTH, 0.0F);
        builder.define(HOOK_RANGE, 0);
        builder.define(HEAD_ITEM_ID, "");
        builder.define(BLOCK_POS, new BlockPos(0, -99999, 0));
        builder.define(PLAYER_UUID, "");
        builder.define(GENTLE_TOUCH, false);
        builder.define(AGILITY_LEVEL, 0);
        builder.define(DAMAGE, 0);
    }
    // FOR 1.20.1
    /*@Override
    protected void defineSynchedData() {
        this.entityData.define(IN_BLOCK, false);
        this.entityData.define(LENGTH, 0.0F);
        this.entityData.define(HOOK_RANGE, 0);
        this.entityData.define(HEAD_ITEM_ID, "");
        this.entityData.define(BLOCK_POS, new BlockPos(0, -99999, 0));
        this.entityData.define(PLAYER_UUID, "");
        this.entityData.define(GENTLE_TOUCH, false);
        this.entityData.define(AGILITY_LEVEL, 0);
        this.entityData.define(DAMAGE, 0);
    }*/


    @Override
    public void tick() {
        super.tick();
        if (this.isRemoved()) return;

        Player owner = this.getPlayerOwner();   

        // This assigns the hook to the owner when re-entering the world
        if (owner == null) {
            Player player = level().getPlayerByUUID(UUID.fromString(this.getPlayerUUID()));
            if (player != null) {
                setOwner(player);
                owner = player;
            } else {
                return;
            }
        }

        if (!level().isClientSide) {
            
            if (this.discardIfInvalid(owner)) {
                return;
            }

            if (this.entityData.get(BLOCK_POS).getY() != -99999) {
                if (this.level().getBlockState(this.entityData.get(BLOCK_POS)).isAir() && this.isNoGravity()) {
                    this.discard();
                    ((PlayerWithHookData) owner).setHook(null);
                    return;
                }
            }

            if (((PlayerWithHookData) owner).getHook() == null) {
                ((PlayerWithHookData) owner).setHook(this);
            }

            if (Math.random() > 0.9955) {
                this.level().playSound(null,
                    getX(), getY(), getZ(),
                    SoundRegistry.AMBIENT,
                    SoundSource.AMBIENT,
                    1.0f, 1.0f
                    //0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
                );
            }
            

            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }

            this.checkInsideBlocks();
        }
    }

    private boolean discardIfInvalid(Player player) {
        if (!player.isAlive() || player.isRemoved() || !((player.getMainHandItem().getItem() instanceof HookItem) || (player.getOffhandItem().getItem() instanceof HookItem)) || this.distanceTo(player) > getMaxRange()) {
            this.discard();
            return true;
        }
        return false;
    }

    /*@Override
    protected boolean canHitEntity(Entity entity) {
        return false;
    }*/

    @Override
    protected boolean canHitEntity(Entity entity) {
        // нельзя бить владельца и другие крюки
        if (entity == this.getOwner()) return false;
        if (!entity.isAlive()) return false;

        return entity instanceof LivingEntity;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();

        if (target instanceof LivingEntity living) {
            Entity owner = this.getOwner();

            DamageSource source = this.damageSources().thrown(this, owner);

            living.hurt(source, getDamageOnhit());
        }

        //super.onHitEntity(result)
    }



    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            setBlockPos(result.getBlockPos());

            Player player = this.getPlayerOwner();

            if (DynamicConfigHandler.breakingFragileBlocks) {
                if (!this.isGentleTouch()) {
                    BlockPos pos = this.entityData.get(BLOCK_POS);
                    BlockState bs = this.level().getBlockState(pos);

                    if (bs.is(TagRegistry.FRAGILE_BLOCKS)) {
                        this.level().destroyBlock(pos, true);
                        this.discard();
                        ((PlayerWithHookData) player).setHook(null);
                        return;
                    }
                    if (bs.getBlock() instanceof FallingBlock) {
                        this.level().scheduleTick(pos, bs.getBlock(), 1);
                    }
                }
            }

            this.setDeltaMovement(Vec3.ZERO);
            this.setInBlock(true);
            this.setPos(result.getLocation()); // Устанавливаем точную позицию попадания
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
        tag.putFloat("length", this.getLength());
        tag.putInt("hook_range", this.getMaxRange());
        tag.putString("head_item", this.getHeadItemId());
        tag.putInt("hook_pos_x", this.getPosX());
        tag.putInt("hook_pos_y", this.getPosY());
        tag.putInt("hook_pos_z", this.getPosZ());
        tag.putString("player_uuid", this.getPlayerUUID());
        tag.putInt("agility_level", this.getAgilityLevel());
        tag.putInt("damage_on_hit", this.getDamageOnhit());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setInBlock(tag.getBoolean("in_block"));
        this.setLength(tag.getFloat("length"));
        this.setMaxRange(tag.getInt("hook_range"));
        this.setHeadItem(tag.getString("head_item"));
        this.setBlockPos(new BlockPos(tag.getInt("hook_pos_x"), tag.getInt("hook_pos_y"), tag.getInt("hook_pos_z")));
        this.setPlayerUUID(tag.getString("player_uuid"));
        this.setAgilityLevel(tag.getInt("agility_level"));
        this.setDamageOnhit(tag.getInt("damage_on_hit"));
    }

    // --- in block flag ---
    private void setInBlock(boolean inBlock) {
        this.entityData.set(IN_BLOCK, inBlock);
    }
    public boolean isInBlock() {
        return this.entityData.get(IN_BLOCK);
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


    // --- hook head ---
    public String getHeadItemId() {
        return this.entityData.get(HEAD_ITEM_ID);
    }

    public ItemStack getHeadItem() {
        Item i = ItemRegistry.hookHeads.get(this.entityData.get(HEAD_ITEM_ID));
        if (i != null) {
            return new ItemStack(i);
        } else {
            return new ItemStack(Items.DIRT); 
        }
    }

    private void setHeadItem(String id) {
        this.entityData.set(HEAD_ITEM_ID, id);
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



    // --- amount damage on hit ---
    private void setDamageOnhit(int level) {
        this.entityData.set(DAMAGE, level);
    }
    public int getDamageOnhit() {
        return this.entityData.get(DAMAGE);
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
        if (entity == null) {
        }
        
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
        if (entity == null) {
        }
        
        return entity instanceof Player ? (Player) entity : null;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        //LoggerUtil.LOGGER.info(this.getPlayerOwner());
        /*if (this.getPlayerOwner() == null) {
            this.kill();
        }*/
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    /*private boolean isFragileBlock(BlockState bs) {
        return bs.is(Blocks.POINTED_DRIPSTONE)
            || bs.is(Blocks.ICE)
            || bs.is(Blocks.GLASS_PANE)
            || bs.is(Blocks.GLASS)
            || bs.getBlock() instanceof StainedGlassPaneBlock
            || bs.getBlock() instanceof StainedGlassBlock
            || bs.getBlock() instanceof TintedGlassBlock
            || bs.is(Blocks.CHORUS_PLANT)
            || bs.is(Blocks.DECORATED_POT)
            || bs.is(Blocks.LILY_PAD)
            || bs.is(Blocks.BIG_DRIPLEAF)
            || bs.is(Blocks.FLOWER_POT)
            || bs.getBlock() instanceof AmethystClusterBlock
            || bs.is(Blocks.TURTLE_EGG)
            || bs.is(Blocks.SNIFFER_EGG);
    }*/
}