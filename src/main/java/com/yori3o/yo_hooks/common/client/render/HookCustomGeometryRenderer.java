package com.yori3o.yo_hooks.common.client.render;


import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;



public class HookCustomGeometryRenderer implements CustomGeometryRenderer {


    private float length, ropeAB;
    private int packedLight;


    public HookCustomGeometryRenderer(float length, float ropeAB, int packedLight) {
        this.length = length;
        this.ropeAB = ropeAB;
        this.packedLight = packedLight;
    }

    public void render(Pose entry, VertexConsumer vertexConsumer) {
        vertex(vertexConsumer, entry, -0.1f, length, 0, 0.5f, ropeAB, packedLight);
        vertex(vertexConsumer, entry, -0.1f, 0,0, 0.5f, -1.0f, packedLight);
        vertex(vertexConsumer, entry, 0.1f, 0, 0, 0, -1.0f, packedLight);
        vertex(vertexConsumer, entry, 0.1f, length, 0, 0, ropeAB, packedLight);

        vertex(vertexConsumer, entry, 0, length, 0.1f, 1, ropeAB, packedLight);
        vertex(vertexConsumer, entry, 0, 0, 0.1f, 1, -1.0f, packedLight);
        vertex(vertexConsumer, entry, 0, 0, -0.1f, 0.5f, -1.0f, packedLight);
        vertex(vertexConsumer, entry, 0, length, -0.1f, 0.5f, ropeAB, packedLight);
    }

    private void vertex(VertexConsumer vertexConsumer, Pose matrix, float x, float y, float z, float u, float v, int packedLight) {
        vertexConsumer.addVertex(matrix.pose(), x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(matrix, 0.0f, 1.0f, 0.0f);
    }
    
}