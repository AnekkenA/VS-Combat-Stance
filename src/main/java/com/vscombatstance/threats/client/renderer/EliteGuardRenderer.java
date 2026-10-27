package com.vscombatstance.threats.client.renderer;

import com.vscombatstance.threats.client.model.EliteGuardModel;
import com.vscombatstance.threats.entity.EliteGuardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EliteGuardRenderer extends GeoEntityRenderer<EliteGuardEntity> {

    public EliteGuardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EliteGuardModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(EliteGuardEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Небольшое масштабирование для придания элитности и внушительности
        poseStack.pushPose();
        poseStack.scale(1.08f, 1.08f, 1.08f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
