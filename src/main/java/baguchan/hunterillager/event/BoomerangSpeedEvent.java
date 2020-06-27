package baguchan.hunterillager.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.EntityEvent;

public class BoomerangSpeedEvent extends EntityEvent {
    private final Vector3d motion;

    public BoomerangSpeedEvent(Entity entity, Vector3d motion) {
        super(entity);
        this.motion = motion;
    }

    public Vector3d getMotion() {
        return motion;
    }
}
