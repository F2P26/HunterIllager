package baguchan.hunterillager.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityEvent;

public class BoomerangSpeedEvent extends EntityEvent {
    private final Vec3d motion;

    public BoomerangSpeedEvent(Entity entity, Vec3d motion) {
        super(entity);
        this.motion = motion;
    }

    public Vec3d getMotion() {
        return motion;
    }
}
