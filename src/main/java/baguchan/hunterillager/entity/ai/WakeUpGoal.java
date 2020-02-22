package baguchan.hunterillager.entity.ai;

import baguchan.hunterillager.entity.HunterIllagerEntity;
import net.minecraft.entity.ai.goal.Goal;

public class WakeUpGoal extends Goal {
    private final HunterIllagerEntity illager;

    public WakeUpGoal(HunterIllagerEntity houseillager) {
        this.illager = houseillager;
    }

    @Override
    public boolean shouldExecute() {
        if (this.illager.world.isDaytime()) {
            return this.illager.isSleeping();
        } else {
            return this.illager.isSleeping() && (illager.getPosY() < (double) this.illager.getBedPosition().get().getY() + 0.4D || this.illager.isSleeping() && !this.illager.getBedPosition().get().withinDistance(illager.getPositionVec(), 1.14D) || !this.illager.getBedPosition().isPresent());
        }
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.illager.wakeUp();
    }
}