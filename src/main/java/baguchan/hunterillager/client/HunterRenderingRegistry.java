package baguchan.hunterillager.client;

import baguchan.hunterillager.client.render.RenderHunterIllager;
import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class HunterRenderingRegistry {
    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityHunterIllager.class, RenderHunterIllager::new);
    }
}
