package baguchan.hunterillager.client.render.layer;

import baguchan.hunterillager.client.model.HunterIllagerModel;
import baguchan.hunterillager.entity.HunterIllagerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class CrossArmHeldItemLayer<T extends HunterIllagerEntity> extends LayerRenderer<T, HunterIllagerModel<T>> {
    public CrossArmHeldItemLayer(IEntityRenderer<T, HunterIllagerModel<T>> p_i50916_1_) {
        super(p_i50916_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getHeldItemOffhand();
        if (!entitylivingbaseIn.isAggressive()) {
            if (!itemstack.isEmpty()) {
                matrixStackIn.push();
                if ((this.getEntityModel()).isSitting) {
                    matrixStackIn.translate(0.0F, 0.625F, 0.0F);
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-20.0F));
                    float f = 0.5F;
                    matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                }

                this.getEntityModel().crossHand.translateRotate(matrixStackIn);
                matrixStackIn.translate(-0.0625F, 0.53125F, 0.21875F);
                Item item = itemstack.getItem();
                if (Block.getBlockFromItem(item).getDefaultState().getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) {
                    matrixStackIn.translate(0.0F, -0.2875F, -0.46F);
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(30.0F));
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-5.0F));
                    float f1 = 0.375F;
                    matrixStackIn.scale(0.375F, -0.375F, 0.375F);
                } else if (item instanceof net.minecraft.item.BowItem) {
                    matrixStackIn.translate(0.0F, -0.2875F, -0.46F);
                    float f2 = 0.625F;
                    matrixStackIn.scale(0.625F, -0.625F, 0.625F);
                } else {
                    matrixStackIn.translate(0.0F, -0.2875F, -0.46F);
                    float f3 = 0.875F;
                    matrixStackIn.scale(0.875F, 0.875F, 0.875F);
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-60.0F));
                }

                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-15.0F));
                Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.pop();
            }
        }
    }
}