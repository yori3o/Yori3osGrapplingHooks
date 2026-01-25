package com.yori3o.yo_hooks.common.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class HookRendererState extends EntityRenderState {
    public Vec3 vectorCable;
    public float length;
    public float pitch;
    public float yawAngle;
    public ItemStack itemStack = ItemStack.EMPTY;
    public boolean shouldRender = false;
    public Vec3 lineOriginOffset;
    public Level level; 
    public int id; // ID сущности для рандомизации анимаций предмета
    public int packedLight; // Свет
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();

    public HookRendererState() {}
}