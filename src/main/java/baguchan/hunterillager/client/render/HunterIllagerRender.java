package baguchan.hunterillager.client.render;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.client.model.HunterIllagerModel;
import baguchan.hunterillager.client.render.layer.CrossArmHeldItemLayer;
import baguchan.hunterillager.entity.HunterIllagerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterIllagerRender<T extends HunterIllagerEntity> extends MobRenderer<T, HunterIllagerModel<T>> {
    private static final ResourceLocation ILLAGER = new ResourceLocation(HunterIllagerCore.MODID, "textures/entity/hunter_illager/hunter_illager_plain.png");

    public HunterIllagerRender(EntityRendererManager p_i47477_1_) {
        super(p_i47477_1_, new HunterIllagerModel<>(), 0.5F);
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new CrossArmHeldItemLayer<>(this));
        this.addLayer(new HeldItemLayer<T, HunterIllagerModel<T>>(this) {
            @Override
            public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (entitylivingbaseIn.isAggressive()) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    public ResourceLocation getEntityTexture(HunterIllagerEntity entity) {
        if (entity.getHunterType() != null) {
            return new ResourceLocation(entity.getHunterType().getRegistryName().getNamespace(), "textures/entity/hunter_illager/hunter_illager_" + entity.getHunterType().getRegistryName().getPath() + ".png");
        } else {
            return ILLAGER;
        }
    }
}