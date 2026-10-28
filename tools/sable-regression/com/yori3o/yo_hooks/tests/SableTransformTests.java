package com.yori3o.yo_hooks.tests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yori3o.yo_hooks.common.compat.sable.SableRenderMath;
import dev.ryanhcode.sable.companion.math.Pose3d;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.Unpooled;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import java.util.Random;

public final class SableTransformTests {
    private static void close(Matrix4f expected, Matrix4f actual) {
        float[] a = expected.get(new float[16]), b = actual.get(new float[16]);
        for (int i = 0; i < 16; i++) {
            if (Math.abs(a[i] - b[i]) > 0.00015f) throw new AssertionError("Render transform mismatch at " + i + ": " + a[i] + " vs " + b[i]);
        }
    }
    private static Quaterniond rotation(Random random) {
        return new Quaterniond().rotationXYZ(random.nextDouble() * 6, random.nextDouble() * 6, random.nextDouble() * 6);
    }
    public static void main(String[] args) {
        Random random = new Random(154237);
        int cases = 0;
        for (int mode = 0; mode < 4; mode++) for (int i = 0; i < 250; i++) {
            Quaterniond containing = (mode & 1) != 0 ? rotation(random) : null;
            Quaterniond entity = (mode & 2) != 0 ? rotation(random) : null;
            Vec3 eye = new Vec3(0, 0.2125, 0);
            Vec3 interpolation = new Vec3(random.nextDouble() * .2, random.nextDouble() * .2, random.nextDouble() * .2);
            PoseStack stack = new PoseStack();
            // Arbitrary caller/camera transform and dispatcher origin must remain intact.
            stack.mulPose(new Quaternionf(rotation(random)));
            stack.translate(12.25, 63.5, -4.75);
            Matrix4f expected = new Matrix4f(stack.last().pose());
            if (containing != null) stack.mulPose(new Quaternionf(containing));
            if (entity != null) {
                stack.translate(interpolation.x, interpolation.y, interpolation.z);
                stack.translate(eye.x, eye.y, eye.z);
                stack.mulPose(new Quaternionf(entity));
                stack.translate(-eye.x, -eye.y, -eye.z);
            }
            SableRenderMath.undoEntityTransforms(stack, containing, entity, eye, interpolation);
            close(expected, stack.last().pose());
            // World-space anchor correction and hand endpoint must share the same basis.
            Vec3 anchorCorrection = new Vec3(.075, -.045, .025);
            stack.translate(anchorCorrection.x, anchorCorrection.y, anchorCorrection.z);
            expected.translate((float)anchorCorrection.x, (float)anchorCorrection.y, (float)anchorCorrection.z);
            close(expected, stack.last().pose());
            cases++;
        }
        // Demonstrate why the former rotation-only correction leaves a visible constant offset.
        Quaternionf turn = new Quaternionf().rotateZ((float)Math.PI / 2);
        Matrix4f wrong = new Matrix4f().translate(0, .2125f, 0).rotate(turn)
            .translate(0, -.2125f, 0).rotate(new Quaternionf(turn).invert());
        if (wrong.getTranslation(new Vector3f()).length() < .2f) throw new AssertionError("Old pivot-offset reproduction failed");

        Vec3 plotHit = new Vec3(30000000.0625, 72.125, -29999999.9375);
        for (int i = 0; i < 250; i++) {
            Pose3d pose = new Pose3d(new Vector3d(10, 65, -4), rotation(random),
                new Vector3d(30000000, 70, -30000000), new Vector3d(.5 + random.nextDouble() * 3));
            Vec3 world = pose.transformPosition(plotHit);
            if (pose.transformPositionInverse(world).distanceTo(plotHit) > 1e-7) throw new AssertionError("Scaled plot point changed");
        }
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", plotHit.x); tag.putDouble("y", plotHit.y); tag.putDouble("z", plotHit.z);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        try {
            buf.writeNbt(tag);
            CompoundTag received = buf.readNbt();
            Vec3 point = new Vec3(received.getDouble("x"), received.getDouble("y"), received.getDouble("z"));
            if (!point.equals(plotHit)) throw new AssertionError("Network attachment lost sub-block precision");
        } finally { buf.release(); }
        System.out.println("PASS: " + cases + " renderer transforms, pivot-offset reproduction, 250 scaled poses, exact attachment NBT round trip");
    }
}
