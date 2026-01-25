package com.yori3o.yo_hooks.common.client.render;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;

import net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer;


public class HookCustomGeometryRenderer implements CustomGeometryRenderer {

    private float length, ropeAA, ropeAB;
    private int packedLight;

    public HookCustomGeometryRenderer( float length, float ropeAA, float ropeAB, int packedLight) {
        this.length = length;
        this.ropeAA = ropeAA;
        this.ropeAB = ropeAB;
        this.packedLight = packedLight;
    }

    public void render(Pose entry, VertexConsumer vertexConsumer) {

        float x1 = 0.1f * Mth.cos((float)((float)Math.PI));
        float z1 = 0.1f * Mth.sin((float)((float)Math.PI));

        float x2 = 0.1f * Mth.cos((float)(0.0f));
        float z2 = 0.1f * Mth.sin((float)(0.0f));

        float x3 = 0.1f * Mth.cos((float)(1.5707964f));
        float z3 = 0.1f * Mth.sin((float)(1.5707964f));

        float x4 = 0.1f * Mth.cos((float)(4.712389f));
        float z4 = 0.1f * Mth.sin((float)(4.712389f));

        // 8. Код отрисовки 8 вершин (HookRenderer.vertex)
        vertex(vertexConsumer, entry, x1, length, z1, 0.4999f, ropeAB, packedLight );
        vertex(vertexConsumer, entry, x1, 0.0f, z1, 0.4999f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x2, 0.0f, z2, 0.0f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x2, length, z2, 0.0f, ropeAB, packedLight );

        vertex(vertexConsumer, entry, x3, length, z3, 1, ropeAB, packedLight );
        vertex(vertexConsumer, entry, x3, 0.0f, z3, 1, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x4, 0.0f, z4, 0.4999f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x4, length, z4, 0.4999f, ropeAB, packedLight );

    }

    private void vertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, float x, float y, float z, float u, float v, int packedLight) {
        vertexConsumer.addVertex(matrix.pose(), x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(matrix, 0.0f, 1.0f, 0.0f);
    }
    
}