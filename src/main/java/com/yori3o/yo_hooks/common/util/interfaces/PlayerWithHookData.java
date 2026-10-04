package com.yori3o.yo_hooks.common.util.interfaces;


import com.yori3o.yo_hooks.common.entity.HookEntity;



public interface PlayerWithHookData {


    HookEntity yo_hooks$getHook();

    void yo_hooks$setHook(HookEntity hookEntity);

    boolean yo_hooks$isJumpAllowed();

    void yo_hooks$setClimbing(boolean up, int agilityLevel);
    
    void yo_hooks$setSuddenFall(boolean bool);
    boolean yo_hooks$isSuddenFall();

    // This variable is a crutch for supporting the right mouse button and jumping.
    boolean yo_hooks$isUsingCancelAfterJump();
    void yo_hooks$setUsingCancelAfterJump(boolean bl);
    
}
