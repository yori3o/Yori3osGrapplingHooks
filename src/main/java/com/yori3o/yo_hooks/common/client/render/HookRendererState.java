package com.yori3o.yo_hooks.common.client.render;


import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;



public class HookRendererState extends EntityRenderState {


    public float length;
    public float pitch;
    public float yawAngle;
    public boolean shouldRender = false;
    //public int packedLight;
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
    public Identifier ropeTexture;


    public HookRendererState() {}
    
}