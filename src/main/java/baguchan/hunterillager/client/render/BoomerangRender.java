package baguchan.hunterillager.client.render;

import baguchan.hunterillager.entity.projectile.BoomerangEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoomerangRender extends EntityRenderer<BoomerangEntity> {

    private ItemRenderer itemRenderer;

    public BoomerangRender(EntityRendererManager renderManager) {
        super(renderManager);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void doRender(BoomerangEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.enableRescaleNormal();

        GlStateManager.translated(0, (entity.getBoundingBox().maxY - entity.getBoundingBox().minY) / 2, 0);
        GlStateManager.rotatef(90, 1, 0, 0);
        GlStateManager.rotatef((entity.ticksExisted + partialTicks + (entity.getPiercingLevel() * 0.85F)) * ((float) entity.getVelocity() + 45), 0, 0, 1);
        GlStateManager.scaled(0.5, 0.5, 0.5);

        this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.itemRenderer.renderItem(entity.getBoomerang(), this.itemRenderer.getModelWithOverrides(entity.getBoomerang()));
        GlStateManager.disableBlend();

        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(BoomerangEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}