package com.yori3o.yo_hooks.common.network;

import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.init.ComponentRegistry;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.util.HookStackState;
import com.yori3o.yo_hooks.common.util.interfaces.PlayerWithHookData;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;

public class HookRegressionTests {
    private static PlayerWithHookData data(Player p) { return (PlayerWithHookData) p; }
    private static ItemStack stack() { return new ItemStack(ItemRegistry.ALL_HOOKS.get("iron").get()); }
    private static Player player(GameTestHelper h) {
        Player p = h.makeMockPlayer(GameType.SURVIVAL);
        p.setPos(h.absoluteVec(new Vec3(1, 2, 1)));
        return p;
    }
    private static HookEntity use(Player p) {
        ((HookItem) p.getMainHandItem().getItem()).use(p.level(), p, InteractionHand.MAIN_HAND);
        return data(p).yo_hooks$getHook();
    }
    private static final class TestHook extends HookEntity {
        TestHook(Player p, ItemStack stack) { super(p.level(), p, 30, stack, 2, true, 7); }
        void anchor(GameTestHelper h) {
            BlockPos pos = h.absolutePos(new BlockPos(1, 4, 1));
            h.getLevel().setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
            onHitBlock(new BlockHitResult(Vec3.atCenterOf(pos), Direction.DOWN, pos, false));
        }
        TagValueOutput saveData() {
            var out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level().registryAccess());
            addAdditionalSaveData(out);
            return out;
        }
        void readData(net.minecraft.nbt.CompoundTag tag) {
            readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, level().registryAccess(), tag));
        }
    }
    private static TestHook attached(GameTestHelper h, Player p) {
        ItemStack item = stack();
        p.setItemInHand(InteractionHand.MAIN_HAND, item);
        TestHook hook = new TestHook(p, item);
        HookStackState.activate(p, item, hook);
        hook.anchor(h);
        hook.setLength(10);
        return hook;
    }

    @GameTest public void rapidSwitching(GameTestHelper h) {
        Player p = player(h);
        ItemStack a = stack(), b = stack();
        p.getInventory().setItem(1, b);
        p.setItemInHand(InteractionHand.MAIN_HAND, a);
        HookEntity old = use(p);
        h.assertTrue(old != null && HookStackState.isActive(a, p), "First cast must activate its stack");
        p.getInventory().setItem(1, a);
        p.setItemInHand(InteractionHand.MAIN_HAND, b);
        use(p); // retract A through B, before A's next tick
        HookEntity fresh = use(p);
        h.assertTrue(fresh != null && fresh != old, "Second cast must exist");
        h.assertTrue(!HookStackState.isActive(a, p) && HookStackState.isActive(b, p), "Only B may render active");
        h.assertTrue(!a.getOrDefault(ComponentRegistry.HOOK_ACTIVE.get(), false), "A's boolean must be cleared");
        fresh.discard();
        h.succeed();
    }

    @GameTest public void oldRemovalCannotDetachNewCast(GameTestHelper h) {
        Player p = player(h);
        p.setItemInHand(InteractionHand.MAIN_HAND, stack());
        HookEntity old = use(p);
        // Model an old entity outliving a replaced player reference / late despawn.
        data(p).yo_hooks$setHook(null);
        HookEntity fresh = use(p);
        old.discard();
        h.assertTrue(data(p).yo_hooks$getHook() == fresh, "Old removal detached new cast");
        h.assertTrue(HookStackState.isActive(p.getMainHandItem(), p), "Old removal cleared new stack");
        fresh.discard();
        h.succeed();
    }

    @GameTest public void stalePacketsAndForeignPlayer(GameTestHelper h) {
        Player p = player(h);
        TestHook hook = attached(h, p);
        UUID stale = UUID.randomUUID();
        ServerReceiver.jump(p, new PlayerJumpPayload(stale, true));
        ServerReceiver.climb(p, new PlayerClimbPayload(stale, true, true));
        h.assertTrue(!hook.isRemoved() && hook.getLength() == 10, "Stale packet changed current cast");
        Player foreign = player(h);
        data(foreign).yo_hooks$setHook(hook);
        foreign.setItemInHand(InteractionHand.MAIN_HAND, p.getMainHandItem().copy());
        ServerReceiver.jump(foreign, new PlayerJumpPayload(hook.getUUID(), true));
        h.assertTrue(!hook.isRemoved(), "Foreign player removed another player's hook");
        hook.discard();
        h.succeed();
    }

    @GameTest public void climbRateAndBounds(GameTestHelper h) {
        Player p = player(h);
        TestHook hook = attached(h, p);
        var packet = new PlayerClimbPayload(hook.getUUID(), true, true);
        ServerReceiver.climb(p, packet);
        float length = hook.getLength();
        h.assertTrue(length < 10, "First climb must move rope");
        for (int i = 0; i < 100; i++) ServerReceiver.climb(p, packet);
        h.assertTrue(hook.getLength() == length, "Packet burst accelerated climbing");
        hook.setLength(Float.NaN);
        h.assertTrue(hook.getLength() == length, "NaN must not reach rope physics");
        hook.setLength(-100);
        h.assertTrue(hook.getLength() == 0.4f, "Rope length became negative");
        h.assertTrue(!hook.acceptClimbSound(p.level().getGameTime()), "Sound cooldown was bypassed");
        hook.discard();
        h.succeed();
    }

    @GameTest public void jumpCleansOriginalStack(GameTestHelper h) {
        Player p = player(h);
        TestHook hook = attached(h, p);
        ItemStack original = p.getMainHandItem();
        p.setItemInHand(InteractionHand.OFF_HAND, original);
        p.setItemInHand(InteractionHand.MAIN_HAND, stack());
        ServerReceiver.jump(p, new PlayerJumpPayload(hook.getUUID(), false));
        h.assertTrue(hook.isRemoved() && data(p).yo_hooks$getHook() == null, "Jump must detach hook");
        h.assertTrue(!original.getOrDefault(ComponentRegistry.HOOK_ACTIVE.get(), false), "Offhand original stayed active");
        h.assertTrue(!p.getMainHandItem().has(ComponentRegistry.HOOK_ACTIVE.get()), "Jump modified unrelated main-hand stack");
        h.succeed();
    }

    @GameTest public void ropeFollowsCastItemBetweenHands(GameTestHelper h) {
        Player p = player(h);
        ItemStack source = stack();
        p.setItemInHand(InteractionHand.OFF_HAND, source);
        TestHook hook = new TestHook(p, source);
        HookStackState.activate(p, source, hook);
        h.assertTrue(hook.getHoldingHand(p) == InteractionHand.OFF_HAND, "Offhand cast must use offhand");

        // Reproduce taking an identical, unused hook into the main hand after firing.
        ItemStack unused = stack();
        p.setItemInHand(InteractionHand.MAIN_HAND, unused);
        h.assertTrue(hook.getHoldingHand(p) == InteractionHand.OFF_HAND, "Unused main-hand hook stole rope origin");
        h.assertTrue(hook.getHeldStack(p) == source, "Rope must use the item bound to this cast");

        // A synchronization copy preserves the cast identity.
        p.setItemInHand(InteractionHand.OFF_HAND, source.copy());
        h.assertTrue(hook.getHoldingHand(p) == InteractionHand.OFF_HAND, "Stack sync changed rope hand");

        // Swapping hands moves the source item, so the rope must follow it.
        p.setItemInHand(InteractionHand.MAIN_HAND, source);
        p.setItemInHand(InteractionHand.OFF_HAND, unused);
        h.assertTrue(hook.getHoldingHand(p) == InteractionHand.MAIN_HAND, "Rope did not follow swapped source item");

        p.setItemInHand(InteractionHand.MAIN_HAND, stack());
        h.assertTrue(hook.getHoldingHand(p) == null, "Unbound items must not become rope origins");
        hook.discard(); h.succeed();
    }

    @GameTest public void collisionUsesOriginalHand(GameTestHelper h) {
        Player p = player(h);
        ItemStack main = stack(), off = stack();
        p.setItemInHand(InteractionHand.MAIN_HAND, main);
        p.setItemInHand(InteractionHand.OFF_HAND, off);
        TestHook hook = new TestHook(p, off);
        HookStackState.activate(p, off, hook);
        hook.anchor(h);
        h.assertTrue(main.getDamageValue() == 0 && off.getDamageValue() == 1, "Collision damaged wrong hook item");
        hook.anchor(h);
        h.assertTrue(off.getDamageValue() == 1, "Repeated collision consumed durability twice");
        hook.discard();
        h.succeed();
    }

    @GameTest public void savedProperties(GameTestHelper h) {
        Player p = player(h);
        TestHook old = attached(h, p);
        var saved = old.saveData().buildResult();
        TestHook restored = new TestHook(p, p.getMainHandItem());
        restored.readData(saved);
        h.assertTrue(restored.isGentleTouch() && restored.damageOnHit == 7, "Saved enchantment/damage was lost");
        h.assertTrue(restored.isInBlock() && restored.isNoGravity(), "Saved attachment lost gravity state");
        h.assertTrue(restored.getLength() == 10, "Saved rope length changed");
        old.discard(); restored.discard();
        h.succeed();
    }

    @GameTest public void switchedItemInvalidatesBeforeCollision(GameTestHelper h) {
        Player p = player(h);
        p.setItemInHand(InteractionHand.MAIN_HAND, stack());
        HookEntity hook = use(p);
        p.getInventory().setItem(1, p.getMainHandItem());
        p.setItemInHand(InteractionHand.MAIN_HAND, stack());
        hook.tick();
        h.assertTrue(hook.isRemoved(), "Different hook item kept old projectile alive");
        h.assertTrue(data(p).yo_hooks$getHook() == null, "Invalid hook remained attached");
        h.succeed();
    }

    @GameTest public void airbornePacketsIgnored(GameTestHelper h) {
        Player p = player(h);
        p.setItemInHand(InteractionHand.MAIN_HAND, stack());
        HookEntity hook = use(p);
        ServerReceiver.jump(p, new PlayerJumpPayload(hook.getUUID(), false));
        ServerReceiver.climb(p, new PlayerClimbPayload(hook.getUUID(), true, true));
        h.assertTrue(!hook.isRemoved() && hook.getLength() == 0, "Airborne hook accepted anchored actions");
        hook.discard(); h.succeed();
    }

    @GameTest public void reconcileStaleFlagsAndStackCopies(GameTestHelper h) {
        Player p = player(h);
        ItemStack a = stack(), b = stack();
        a.set(ComponentRegistry.HOOK_ACTIVE.get(), true);
        b.set(ComponentRegistry.HOOK_ACTIVE.get(), true);
        p.setItemInHand(InteractionHand.MAIN_HAND, a);
        p.getInventory().setItem(1, b);
        HookStackState.reconcile(p);
        h.assertTrue(!a.getOrDefault(ComponentRegistry.HOOK_ACTIVE.get(), false)
            && !b.getOrDefault(ComponentRegistry.HOOK_ACTIVE.get(), false), "Legacy stale flags survived reconciliation");
        HookEntity hook = use(p);
        ItemStack copied = a.copy();
        p.setItemInHand(InteractionHand.MAIN_HAND, copied);
        h.assertTrue(hook.getHeldStack(p) == copied, "Normal stack copy lost cast binding");
        b.set(ComponentRegistry.HOOK_ACTIVE.get(), true);
        HookStackState.reconcile(p);
        h.assertTrue(HookStackState.isActive(copied, p) && !HookStackState.isActive(b, p), "Reconcile did not isolate active item");
        hook.discard(); h.succeed();
    }

    @GameTest public void cancelUseExpiresAndConsumesClick(GameTestHelper h) {
        Player p = player(h);
        TestHook hook = attached(h, p);
        ServerReceiver.jump(p, new PlayerJumpPayload(hook.getUUID(), true));
        var result = ((HookItem) p.getMainHandItem().getItem()).use(p.level(), p, InteractionHand.MAIN_HAND);
        h.assertTrue(result.consumesAction(), "Cancelled use must not fall through to offhand");
        h.assertTrue(data(p).yo_hooks$getHook() == null, "Jump and use created another hook");
        data(p).yo_hooks$setUsingCancelAfterJump(true);
        p.tickCount += 3;
        HookEntity fresh = use(p);
        h.assertTrue(fresh != null, "Expired cancel flag ate a later click");
        fresh.discard(); h.succeed();
    }

    @GameTest public void ownerlessAndMalformedSavedHook(GameTestHelper h) {
        HookEntity orphan = new HookEntity(EntityRegistry.HOOK_ENTITY.get(), h.getLevel());
        orphan.tick();
        h.assertTrue(orphan.isRemoved(), "Ownerless server projectile survived");
        Player p = player(h);
        TestHook malformed = attached(h, p);
        var tag = malformed.saveData().buildResult();
        tag.remove("Owner");
        tag.putString("player_uuid", "not-a-uuid");
        malformed.setOwner(null);
        malformed.readData(tag);
        malformed.tick();
        h.assertTrue(malformed.isRemoved(), "Malformed owner UUID was not discarded");
        h.succeed();
    }

    @GameTest public void loweringClampsAtMaximum(GameTestHelper h) {
        Player p = player(h);
        TestHook hook = attached(h, p);
        hook.setLength(hook.getMaxRange() - 2.01f);
        ServerReceiver.climb(p, new PlayerClimbPayload(hook.getUUID(), false, false));
        h.assertTrue(hook.getLength() == hook.getMaxRange() - 2f, "Lowering exceeded maximum rope length");
        hook.discard();
        TestHook shortHook = attached(h, p);
        shortHook.setLength(0.41f);
        ServerReceiver.climb(p, new PlayerClimbPayload(shortHook.getUUID(), true, false));
        h.assertTrue(shortHook.getLength() == 0.4f, "Climbing exceeded minimum rope length");
        shortHook.discard(); h.succeed();
    }

    @GameTest public void codecsPreserveCastIdentity(GameTestHelper h) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        try {
            var jump = new PlayerJumpPayload(UUID.randomUUID(), true);
            PlayerJumpPayload.CODEC.encode(buf, jump);
            h.assertTrue(jump.equals(PlayerJumpPayload.CODEC.decode(buf)), "Jump codec lost cast identity");
            var climb = new PlayerClimbPayload(UUID.randomUUID(), false, true);
            PlayerClimbPayload.CODEC.encode(buf, climb);
            h.assertTrue(climb.equals(PlayerClimbPayload.CODEC.decode(buf)), "Climb codec lost cast identity");
        } finally { buf.release(); }
        h.succeed();
    }
}
