package com.yori3o.yo_hooks.common.utils;

import com.yori3o.yo_hooks.common.entity.HookEntity;

public interface PlayerWithHookData {

    HookEntity getHook();

    void setHook(HookEntity hookEntity);

    boolean isJumpAllowed();

    void setClimbing(boolean up, int agility_level);

    // This variable is a crutch for supporting the right mouse button and jumping.
    boolean isUsingCancelAfterJump();
    void setUsingCancelAfterJump(boolean bl);
}
