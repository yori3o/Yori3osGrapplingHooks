package com.yori3o.yo_hooks.common.hookregistry;



public class HookDefinition {


    public final String id;

    public Integer durability;
    public Integer length;
    public boolean fireResistant;
    public int enchantability;
    public int damageOnHit;
    public String repairItemsTag;
    public boolean customVisual;
    public boolean doesNotConsumeHunger;
    public boolean doesNotBreakFragileBlocks;
    public int defaultAgilityLevel;

    public Integer durabilityOverlap = null;
    public Integer lengthOverlap = null;
    public Integer damageOverlap = null;


    public HookDefinition(String id) {
        this.id = id;
    }

    public void applyDefaults() {
        if (durability == null) durability = 100;
        if (length == null) length = 20;

        if (repairItemsTag == null) repairItemsTag = "";
    }

    public int getLength() {
        if (lengthOverlap == null) {
            return length;
        } else {
            return lengthOverlap;
        }
    }

    public int getDurability() {
        if (durabilityOverlap == null) {
            return durability;
        } else {
            return durabilityOverlap;
        }
    }

    public int getDamage() {
        if (damageOverlap == null) {
            return damageOnHit;
        } else {
            return damageOverlap;
        }
    }

    public Integer getDamageOverlap() {
        return damageOverlap;
    }

}