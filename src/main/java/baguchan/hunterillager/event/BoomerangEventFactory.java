package baguchan.hunterillager.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;

public class BoomerangEventFactory {

    public static void boomerangSpeedTick(Entity entity, Vector3d motion) {
        MinecraftForge.EVENT_BUS.post(new BoomerangSpeedEvent(entity, motion));
    }
}
