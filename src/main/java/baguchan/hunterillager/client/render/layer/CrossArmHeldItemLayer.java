package baguchan.hunterillager.client.render.layer;

import baguchan.hunterillager.client.model.HunterIllagerModel;
import baguchan.hunterillager.entity.EntityHunterIllager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CrossArmHeldItemLayer<T extends EntityHunterIllager> extends LayerRenderer<T, HunterIllagerModel<T>> {
    public CrossArmHeldItemLayer(IEntityRenderer<T, HunterIllagerModel<T>> p_i50916_1_) {
        super(p_i50916_1_);
    }

    public void render(T p_212842_1_, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        ItemStack itemstack = p_212842_1_.getHeldItemOffhand();
        if (!p_212842_1_.isAggressive()) {
            if (!itemstack.isEmpty()) {
                GlStateManager.color3f(1.0F, 1.0F, 1.0F);
                GlStateManager.pushMatrix();
                if ((this.getEntityModel()).isSitting) {
                    GlStateManager.translatef(0.0F, 0.625F, 0.0F);
                    GlStateManager.rotatef(-20.0F, -1.0F, 0.0F, 0.0F);
                    float f = 0.5F;
                    GlStateManager.scalef(0.5F, 0.5F, 0.5F);
                }

                this.getEntityModel().crossHand.postRender(0.0625F);
                GlStateManager.translatef(-0.0625F, 0.53125F, 0.21875F);
                Item item = itemstack.getItem();
                if (Block.getBlockFromItem(item).getDefaultState().getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) {
                    GlStateManager.translatef(0.0F, -0.2875F, -0.46F);
                    GlStateManager.rotatef(30.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                    float f1 = 0.375F;
                    GlStateManager.scalef(0.375F, -0.375F, 0.375F);
                } else if (item instanceof net.minecraft.item.BowItem) {
                    GlStateManager.translatef(0.0F, -0.2875F, -0.46F);
                    float f2 = 0.625F;
                    GlStateManager.scalef(0.625F, -0.625F, 0.625F);
                } else {
                    GlStateManager.translatef(0.0F, -0.2875F, -0.46F);
                    float f3 = 0.875F;
                    GlStateManager.scalef(0.875F, 0.875F, 0.875F);
                    GlStateManager.rotatef(-60.0F, 1.0F, 0.0F, 0.0F);
                }

                GlStateManager.rotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                Minecraft.getInstance().getFirstPersonRenderer().renderItem(p_212842_1_, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
                GlStateManager.popMatrix();
            }
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}