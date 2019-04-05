package baguchan.hunterillager.client;

import baguchan.hunterillager.client.render.RenderHunterIllager;
import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HunterRenderingRegistry {
    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityHunterIllager.class, RenderHunterIllager::new);
    }
}
