package baguchan.hunterillager.client.render;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.client.model.HunterIllagerModel;
import baguchan.hunterillager.client.render.layer.CrossArmHeldItemLayer;
import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHunterIllager<T extends EntityHunterIllager> extends MobRenderer<T, HunterIllagerModel<T>> {
    private static final ResourceLocation ILLAGER = new ResourceLocation(HunterIllagerCore.MODID, "textures/entity/illager/hunter_illager.png");

    public RenderHunterIllager(EntityRendererManager p_i47477_1_) {
        super(p_i47477_1_, new HunterIllagerModel<>(), 0.5F);
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new CrossArmHeldItemLayer(this));
        this.addLayer(new HeldItemLayer<T, HunterIllagerModel<T>>(this) {
            public void func_212842_a_(T p_212842_1_, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
                if ( p_212842_1_.func_213398_dR()) {
                    super.func_212842_a_(p_212842_1_, p_212842_2_, p_212842_3_, p_212842_4_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
                }

            }
        });
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityHunterIllager entity) {
        return ILLAGER;
    }
}