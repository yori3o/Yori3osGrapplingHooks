package com.yori3o.yo_hooks.utils;

import com.yori3o.yo_hooks.entity.HookEntity;

public interface PlayerWithHookData {
    HookEntity getHook();

    void setHook(HookEntity hookEntity);

    Boolean isResetFallDistance();
}
