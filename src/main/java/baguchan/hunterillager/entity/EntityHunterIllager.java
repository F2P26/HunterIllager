package baguchan.hunterillager.entity;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.HunterSounds;
import com.google.common.base.Predicate;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class EntityHunterIllager extends AbstractIllager implements IRangedAttackMob {
    private final InventoryBasic illagerInventory;

    protected int eattick = 0;
    private int cooldownTicks;

    public EntityHunterIllager(World world) {
        super(world);
        this.setSize(0.6F, 1.95F);
        this.illagerInventory = new InventoryBasic("Items", false, 8);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        this.experienceValue = 4;
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackRangedBow<>(this, 0.95D, 20, 16.0F));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWander(this, 0.9D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {AbstractIllager.class}));
        this.targetTasks.addTask(2, (new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true)).setUnseenMemoryTicks(300));
        this.targetTasks.addTask(3, (new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetTasks.addTask(3, (new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, false)).setUnseenMemoryTicks(300));
        this.targetTasks.addTask(3, (new EntityAINearestAttackableTarget<>(this, EntityAnimal.class, 10, true, false, new Predicate<EntityAnimal>()
        {
            public boolean apply(@Nullable EntityAnimal p_apply_1_)
            {
                return !(p_apply_1_ instanceof EntityTameable) && !(p_apply_1_ instanceof EntityHorse) && !(p_apply_1_ instanceof EntityLlama) && getCooldownTicks() <= 0;
            }
        }).setUnseenMemoryTicks(400)));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(22.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(28.0D);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

        this.illagerInventory.addItem(new ItemStack(Items.COOKED_PORKCHOP,3 + this.rand.nextInt(3)));

        if(this.rand.nextInt(5) == 0){
            this.illagerInventory.addItem(new ItemStack(Items.GOLDEN_APPLE,1));
        }

        return livingdata;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

        this.cooldownTicks = compound.getInteger("CooldownTicks");

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.illagerInventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.illagerInventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                nbttaglist.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
            }
        }

        compound.setTag("Inventory", nbttaglist);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        compound.setInteger("CooldownTicks", this.cooldownTicks);

        NBTTagList nbttaglist = compound.getTagList("Inventory", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            ItemStack itemstack = new ItemStack(nbttaglist.getCompoundTagAt(i));

            if (!itemstack.isEmpty())
            {
                this.illagerInventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
    }

    public boolean isCooldown()
    {
        return this.cooldownTicks > 0;
    }

    public int getCooldownTicks()
    {
        return this.cooldownTicks;
    }

    public void setCooldownTicks(int tick)
    {
        this.cooldownTicks = tick;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.world.isRemote) {
            this.eattick = Math.max(0, this.eattick - 1);
        }

        if (this.cooldownTicks > 0)
        {
            --this.cooldownTicks;
        }

        if(this.eattick == 1){
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND,ItemStack.EMPTY);
        }

        super.onLivingUpdate();

        this.updateArmSwingProgress();

        //Eat food if there is min health
        if (this.rand.nextFloat() < 0.0048F && this.getHealth() < this.getMaxHealth()) {
            eatFood();
        }

    }

    private void eatFood() {
        ItemStack itemstack = findFood();

        if (!itemstack.isEmpty()) {
            //find food
            ItemFood itemfood = (ItemFood) itemstack.getItem();
            this.heal((float) itemfood.getHealAmount(itemstack));
            itemstack.shrink(1);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
            eattick = 20;
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND,itemstack);
        }
    }

    /**
     * Find the food illager can eat in illager inventory.
     */
    private ItemStack findFood() {
        ItemStack friendsstack;

        for (int i = 0; i < this.illagerInventory.getSizeInventory(); ++i) {
            friendsstack = this.illagerInventory.getStackInSlot(i);

            if (canIllagerPickupItem(friendsstack.getItem())) {
                return friendsstack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity)
    {
        ItemStack itemstack = itemEntity.getItem();
        Item item = itemstack.getItem();

        if (this.canIllagerPickupItem(item))
        {
            ItemStack itemstack1 = this.illagerInventory.addItem(itemstack);

            if (itemstack1.isEmpty())
            {
                itemEntity.setDead();
            }
            else
            {
                itemstack.setCount(itemstack1.getCount());
            }
        }
    }

    @Override
    public void onDeath(DamageSource cause)
    {
        super.onDeath(cause);

        if (!this.world.isRemote && this.illagerInventory != null)
        {
            for (int i = 0; i < this.illagerInventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.illagerInventory.getStackInSlot(i);

                if (!itemstack.isEmpty())
                {
                    this.entityDropItem(itemstack, 0.0F);
                }
            }
        }
    }

    private boolean canIllagerPickupItem(Item itemIn)
    {
        return itemIn == Items.BREAD || itemIn == Items.BEEF || itemIn == Items.COOKED_BEEF|| itemIn == Items.PORKCHOP || itemIn == Items.COOKED_PORKCHOP|| itemIn == Items.CHICKEN|| itemIn == Items.COOKED_CHICKEN|| itemIn == Items.MUTTON || itemIn == Items.COOKED_MUTTON|| itemIn == Items.RABBIT|| itemIn == Items.COOKED_RABBIT;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.VINDICATION_ILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.VINDICATION_ILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VINDICATION_ILLAGER_HURT;
    }

    @Override

    public void onKillEntity(EntityLivingBase entity) {

        super.onKillEntity(entity);
        if(!(entity instanceof AbstractIllager)) {

            this.playSound(HunterSounds.HUNTER_ILLAGER_LAUGH, this.getSoundVolume() + 0.15F, this.getSoundPitch());

            this.setCooldownTicks(600);
        }
    }

    @Override
    protected ResourceLocation getLootTable()
    {
        return new ResourceLocation(HunterIllagerCore.MODID,"entity/hunter_illager");
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn)
    {
        if (super.isOnSameTeam(entityIn))
        {
            return true;
        }
        else if (entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getCreatureAttribute() == EnumCreatureAttribute.ILLAGER)
        {
            return this.getTeam() == null && entityIn.getTeam() == null;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
    {
        EntityArrow entityarrow = this.createArrowEntity(distanceFactor);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entityarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.world.getDifficulty().getDifficultyId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entityarrow);
    }

    protected EntityArrow createArrowEntity(float p_193097_1_)
    {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
        entitytippedarrow.setEnchantmentEffectsFromEntity(this, p_193097_1_);
        return entitytippedarrow;
    }

    @SideOnly(Side.CLIENT)
    public boolean isAggressive()
    {
        return this.isAggressive(1);
    }

    public void setSwingingArms(boolean swingingArms)
    {
        this.setAggressive(1, swingingArms);
    }

    @SideOnly(Side.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose()
    {
        return this.isAggressive() ? AbstractIllager.IllagerArmPose.BOW_AND_ARROW : AbstractIllager.IllagerArmPose.CROSSED;

    }
}
