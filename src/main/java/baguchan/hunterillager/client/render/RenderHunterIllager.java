package baguchan.hunterillager.client.render;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHunterIllager extends IllagerRenderer<EntityHunterIllager> {
    private static final ResourceLocation ILLAGER = new ResourceLocation(HunterIllagerCore.MODID, "textures/entity/illager/hunter_illager.png");

    public RenderHunterIllager(EntityRendererManager p_i47477_1_) {
        super(p_i47477_1_, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
        this.addLayer(new HeldItemLayer<EntityHunterIllager, IllagerModel<EntityHunterIllager>>(this) {
            public void func_212842_a_(EntityHunterIllager p_212842_1_, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
                if ( p_212842_1_.func_213398_dR()) {
                    super.func_212842_a_(p_212842_1_, p_212842_2_, p_212842_3_, p_212842_4_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
                }

            }
        });
        this.field_77045_g.func_205062_a().showModel = true;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityHunterIllager entity) {
        return ILLAGER;
    }
}