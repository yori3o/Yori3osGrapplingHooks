package com.yori3o.yo_hooks.common.compat.sable;

import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.phys.Vec3;

public final class HookWithSableData {
    public final SubLevel attachedShip;
    public final Vec3 localAttachPos;

    public HookWithSableData(SubLevel ship, Vec3 localAttachPos) {
        this.attachedShip = ship;
        this.localAttachPos = localAttachPos;
    }
}
