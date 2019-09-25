package baguchan.hunterillager.entity.ai;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.RangedAttackGoal;

public class RangedAggroedAttackGoal extends RangedAttackGoal {
    private MobEntity livingEntity;

    public RangedAggroedAttackGoal(IRangedAttackMob rangedAttackMob, double movespeed, int maxAttackTime, float maxAttackDistanceIn) {
        super(rangedAttackMob, movespeed, maxAttackTime, maxAttackDistanceIn);
        this.livingEntity = (MobEntity) rangedAttackMob;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();

        this.livingEntity.setAggroed(true);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.livingEntity.setAggroed(false);
    }
}
