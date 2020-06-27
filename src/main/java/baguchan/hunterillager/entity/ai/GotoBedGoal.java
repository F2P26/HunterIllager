package baguchan.hunterillager.entity.ai;

import baguchan.hunterillager.entity.HunterIllagerEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class GotoBedGoal extends MoveToBlockGoal {
    private final HunterIllagerEntity illager;

    public GotoBedGoal(HunterIllagerEntity houseillager, double speed) {
        super(houseillager, speed, 14);
        this.illager = houseillager;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return !this.illager.isBeingRidden() && this.illager.getRaid() == null && !this.illager.world.isDaytime() && this.illager.getAttackTarget() == null && super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        BlockPos blockpos = new BlockPos(destinationBlock.getX(), destinationBlock.getY(), destinationBlock.getZ());

        return !this.illager.world.isDaytime() && this.illager.getRaid() == null && (illager.getPosY() > (double) blockpos.getY() + 0.4D && blockpos.withinDistance(illager.getPositionVec(), 1.14D) && this.illager.isSleeping() && this.illager.getBedPosition().isPresent());
    }


    public void startExecuting() {
        super.startExecuting();
    }

    public void resetTask() {
        super.resetTask();
        this.illager.wakeUp();
    }

    /*
     *  when moved Finded bed, he going to sleep
     */
    @Override
    public void tick() {
        super.tick();

        if (!this.illager.isSleeping()) {
            BlockPos pos = this.illager.func_233580_cy_();
            BlockState blockstate = this.illager.world.getBlockState(pos);

            if (this.getIsAboveDestination()) {
                this.illager.startSleeping(this.destinationBlock);
            }
        }
    }

    public double getTargetDistanceSq() {
        return 1.5D;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */

    /**
     * Return true to set given position as destination
     */
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        Block block = blockstate.getBlock();

        return block.isIn(BlockTags.BEDS) && blockstate.get(BedBlock.PART) == BedPart.HEAD && !blockstate.get(BedBlock.OCCUPIED);
    }
}