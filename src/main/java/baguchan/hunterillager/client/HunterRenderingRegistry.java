package baguchan.hunterillager.client;

import baguchan.hunterillager.HunterEntityRegistry;
import baguchan.hunterillager.client.render.BoomerangRender;
import baguchan.hunterillager.client.render.HunterIllagerRender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class HunterRenderingRegistry {
    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(HunterEntityRegistry.HUNTERILLAGER, HunterIllagerRender::new);
        RenderingRegistry.registerEntityRenderingHandler(HunterEntityRegistry.BOOMERANG, BoomerangRender::new);
    }
}
