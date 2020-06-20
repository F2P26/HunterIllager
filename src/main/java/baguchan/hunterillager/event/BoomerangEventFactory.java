package baguchan.hunterillager.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;

public class BoomerangEventFactory {

    public static void boomerangSpeedTick(Entity entity, Vec3d motion) {
        MinecraftForge.EVENT_BUS.post(new BoomerangSpeedEvent(entity, motion));
    }
}
