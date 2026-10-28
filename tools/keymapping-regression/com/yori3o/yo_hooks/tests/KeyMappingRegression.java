package com.yori3o.yo_hooks.tests;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.KeyMapping;
import java.nio.file.Files;
import java.nio.file.Path;

public final class KeyMappingRegression implements PreLaunchEntrypoint {
    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    @Override public void onPreLaunch() {
        try {
            InputConstants.Key space = InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_SPACE);
            InputConstants.Key mouse = InputConstants.Type.MOUSE.getOrCreate(1);
            KeyMapping vanilla = new KeyMapping("key.regression.vanilla", InputConstants.Type.KEYSYM, InputConstants.KEY_SPACE, "key.categories.misc");
            KeyMapping first = new KeyMapping("key.yo_hooks.regression.first", InputConstants.Type.KEYSYM, InputConstants.KEY_SPACE, "key.categories.misc");
            KeyMapping second = new KeyMapping("key.yo_hooks.regression.second", InputConstants.Type.KEYSYM, InputConstants.KEY_SPACE, "key.categories.misc");
            KeyMapping.set(space, true);
            require(vanilla.isDown() && first.isDown() && second.isDown(), "Constructor registration displaced a shared action");
            KeyMapping.click(space);
            require(vanilla.consumeClick() && first.consumeClick() && second.consumeClick(), "Click not delivered to all actions");
            require(!first.consumeClick() && !second.consumeClick(), "Click delivered twice");
            KeyMapping.set(space, false);
            require(!vanilla.isDown() && !first.isDown() && !second.isDown(), "Release not delivered to all actions");
            for (int i = 0; i < 3; i++) KeyMapping.resetMapping();
            KeyMapping.set(space, true);
            require(vanilla.isDown() && first.isDown() && second.isDown(), "Reset displaced a shared action");
            KeyMapping.click(space);
            require(vanilla.consumeClick() && first.consumeClick() && second.consumeClick(), "Reset lost a shared click");
            require(!first.consumeClick() && !second.consumeClick(), "Reset duplicated listeners");
            KeyMapping.releaseAll();
            KeyMapping mouseVanilla = new KeyMapping("key.regression.use", InputConstants.Type.MOUSE, 1, "key.categories.misc");
            first.setKey(mouse);
            KeyMapping.resetMapping();
            KeyMapping.set(space, true);
            require(vanilla.isDown() && second.isDown() && !first.isDown(), "Old key still activates rebound action");
            KeyMapping.set(mouse, true);
            require(mouseVanilla.isDown() && first.isDown(), "Mouse sharing failed");
            KeyMapping.click(mouse);
            require(mouseVanilla.consumeClick() && first.consumeClick(), "Mouse click sharing failed");
            KeyMapping.set(mouse, false);
            require(!mouseVanilla.isDown() && !first.isDown(), "Mouse release failed");
            KeyMapping.releaseAll();
            second.setKey(InputConstants.UNKNOWN);
            KeyMapping.resetMapping();
            KeyMapping.set(space, true);
            require(vanilla.isDown() && !first.isDown() && !second.isDown(), "Unbound action still receives old key");
            KeyMapping.releaseAll();
            System.out.println("PASS: transformed Fabric 1.21.1 KeyMapping: shared keyboard/mouse actions, clicks, release, reset, rebind and unbind");
            Files.writeString(Path.of("passed.txt"), "PASS");
            System.exit(0);
        } catch (Throwable error) {
            error.printStackTrace();
            System.exit(1);
        }
    }
}
