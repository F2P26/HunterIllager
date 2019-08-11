package baguchan.hunterillager.client;

import baguchan.hunterillager.client.render.HunterIllagerRender;
import baguchan.hunterillager.entity.HunterIllagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class HunterRenderingRegistry {
    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(HunterIllagerEntity.class, HunterIllagerRender::new);
    }
}
