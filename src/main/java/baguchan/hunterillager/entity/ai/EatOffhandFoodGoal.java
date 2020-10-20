package baguchan.hunterillager.entity.ai;

import baguchan.hunterillager.entity.HunterIllagerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class EatOffhandFoodGoal<T extends HunterIllagerEntity> extends Goal {
    private final T entity;
    private ItemStack usedStack;
    private int eatTick;

    public EatOffhandFoodGoal(T p_i50319_1_) {
        this.entity = p_i50319_1_;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        return this.entity.isAlive() && this.entity.getHealth() < this.entity.getMaxHealth() && this.entity.getHeldItem(Hand.OFF_HAND).isEmpty() && this.entity.getRNG().nextFloat() < 0.0075 && !this.entity.findFood().isEmpty();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return this.eatTick > 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.eatTick > 0) {
            --this.eatTick;
        }

        if (this.eatTick % 4 == 0) {
            this.entity.playSound(this.entity.getEatSound(usedStack), 0.5F + 0.5F * (float) this.entity.getRNG().nextInt(2), (this.entity.getRNG().nextFloat() - this.entity.getRNG().nextFloat()) * 0.2F + 1.0F);
            for (int i = 0; i < 5; ++i) {
                Vector3d vector3d = new Vector3d(((double) this.entity.getRNG().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                vector3d = vector3d.rotatePitch(-this.entity.rotationPitch * ((float) Math.PI / 180F));
                vector3d = vector3d.rotateYaw(-this.entity.rotationYaw * ((float) Math.PI / 180F));
                double d0 = (double) (-this.entity.getRNG().nextFloat()) * 0.6D - 0.3D;
                Vector3d vector3d1 = new Vector3d(((double) this.entity.getRNG().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
                vector3d1 = vector3d1.rotatePitch(-this.entity.rotationPitch * ((float) Math.PI / 180F));
                vector3d1 = vector3d1.rotateYaw(-this.entity.rotationYaw * ((float) Math.PI / 180F));
                vector3d1 = vector3d1.add(this.entity.getPosX(), this.entity.getPosYEye(), this.entity.getPosZ());
                if (this.entity.world instanceof ServerWorld) //Forge: Fix MC-2518 spawnParticle is nooped on server, need to use server specific variant
                    ((ServerWorld) this.entity.world).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, usedStack), vector3d1.x, vector3d1.y, vector3d1.z, 1, vector3d.x, vector3d.y + 0.05D, vector3d.z, 0.0D);
                else
                    this.entity.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, usedStack), vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y + 0.05D, vector3d.z);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        ItemStack itemstack = this.entity.findFood().split(1);
        usedStack = itemstack.copy();
        this.entity.setItemStackToSlot(EquipmentSlotType.OFFHAND, itemstack);
        this.eatTick = 32;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.entity.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);

        if (usedStack.getItem().isFood()) {
            this.entity.heal(usedStack.getItem().getFood().getHealing());
        }
        usedStack = ItemStack.EMPTY;
    }
}