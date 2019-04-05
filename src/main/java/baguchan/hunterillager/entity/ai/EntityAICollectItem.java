package baguchan.hunterillager.entity.ai;

import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;

import java.util.List;

public class EntityAICollectItem extends EntityAIBase {

    protected EntityHunterIllager illager;
    protected float moveSpeed;
    protected EntityItem targetItem;
    protected boolean lastAvoidWater;


    public EntityAICollectItem(EntityHunterIllager entityIllager, float pmoveSpeed) {
        illager = entityIllager;
        moveSpeed = pmoveSpeed;
        setMutexBits(3);
    }


    @Override
    public boolean shouldExecute() {

        if(illager.getAttackTarget() != null){
            return false;
        }

        if (findItem()) {
            List llist = illager.world.getEntitiesWithinAABB(EntityItem.class, illager.getEntityBoundingBox().grow(8F, 2D, 8F));
            if (!llist.isEmpty()) {
                int li = illager.getRNG().nextInt(llist.size());
                EntityItem ei = (EntityItem) llist.get(li);

                if(illager.canIllagerPickupItem(ei.getItem().getItem())) {
                    NBTTagCompound p = new NBTTagCompound();
                    ei.writeEntityToNBT(p);
                    if (!ei.isDead && ei.onGround && p.getShort("PickupDelay") <= 0 && !ei.isBurning()
                            && canEntityItemBeSeen(ei)) {
                        targetItem = ei;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean findItem() {
        ItemStack illagersstack;

        for (int i = 0; i < illager.getIllagerInventory().getSizeInventory(); ++i) {
            illagersstack = illager.getIllagerInventory().getStackInSlot(i);

            if (illagersstack == ItemStack.EMPTY) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();

    }

    @Override
    public boolean shouldContinueExecuting() {
        return !targetItem.isDead && findItem() && illager.getDistanceSq(targetItem) < 100D;
    }

    @Override
    public void resetTask() {
        targetItem = null;
        illager.getNavigator().clearPath();

    }

    @Override
    public void updateTask() {
        illager.getLookHelper().setLookPositionWithEntity(targetItem, 30F, illager.getVerticalFaceSpeed());

        PathNavigate lnavigater = illager.getNavigator();
        if (lnavigater.noPath()) {
            lnavigater.tryMoveToXYZ(targetItem.posX, targetItem.posY, targetItem.posZ, moveSpeed);
        }
    }

    public boolean canEntityItemBeSeen(Entity entity) {
        // アイテムの可視判定
        return true;
    }

}