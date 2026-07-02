package com.yori3o.yo_hooks.common.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModConfig {
    public float decreaseSatiety = 1.1f;
    public boolean breakingFragileBlocks = true;
    public List<String> blocksBlacklist = new LinkedList<>();
    public boolean whitelistMode = false;

    public float climbSpeed = 0.115f;
    public boolean softHook = false;
    public float stiffness = 0.10f;
    public boolean funnyMode = false;

    public boolean holdHookTightly = false;
    public boolean climbWithMouseWheelScroll = false;
    
    public Map<String, Integer> durabilityOverlap = new HashMap<>();
    public Map<String, Integer> rangeOverlap = new HashMap<>();

    public ModConfig copy() {
        ModConfig copy = new ModConfig();

        copy.decreaseSatiety = decreaseSatiety;
        copy.breakingFragileBlocks = breakingFragileBlocks;
        copy.blocksBlacklist = new LinkedList<>(blocksBlacklist);
        copy.whitelistMode = whitelistMode;

        copy.climbSpeed = climbSpeed;
        copy.softHook = softHook;
        copy.stiffness = stiffness;
        copy.funnyMode = funnyMode;

        copy.holdHookTightly = holdHookTightly;
        copy.climbWithMouseWheelScroll = climbWithMouseWheelScroll;

        copy.durabilityOverlap = durabilityOverlap;
        copy.rangeOverlap = rangeOverlap;

        return copy;
    }
}
