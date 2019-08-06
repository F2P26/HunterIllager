package baguchan.hunterillager.entity;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.HunterSounds;
import com.google.common.collect.Maps;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityHunterIllager extends AbstractIllagerEntity implements IRangedAttackMob {
    private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION)).setSaved(false);
    private static final DataParameter<Boolean> IS_EATING = EntityDataManager.createKey(EntityHunterIllager.class, DataSerializers.BOOLEAN);
    private int foodUseTimer;
    @Nullable
    private BlockPos homePosition;

    private final Inventory inventory = new Inventory(5);

    public static final Predicate<LivingEntity> animalTarget = (p_213440_0_) -> {
        EntityType<?> entitytype = p_213440_0_.getType();
        return entitytype == EntityType.CHICKEN || entitytype == EntityType.COW || entitytype == EntityType.PIG || entitytype == EntityType.RABBIT || entitytype == EntityType.SNOW_GOLEM;
    };

    protected int eattick = 0;
    private int cooldownTicks;

    public EntityHunterIllager(EntityType<EntityHunterIllager> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 6;
        ((GroundPathNavigator) this.getNavigator()).setBreakDoors(true);
        this.setDropChance(EquipmentSlotType.OFFHAND, 0.4F);
        this.inventory.addItem(new ItemStack(Items.COOKED_PORKCHOP, 4));
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(4, new RangedBowAttackGoal<>(this, 0.65D, 20, 15.0F));
        this.goalSelector.addGoal(6, new MoveToGoal(this, 8.0D, 0.8D));
        this.goalSelector.addGoal(7, new FindCampfireOrBed(this, 0.8D));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.75D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(4, (new NearestAttackableTargetGoal(this, AnimalEntity.class, 10, true, false, animalTarget) {
            @Override
            public boolean shouldExecute() {
                return super.shouldExecute() && !isRaidActive() && !isCooldown();
            }
        }).setUnseenMemoryTicks(300));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double) 0.3F);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(22.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }

    public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
        this.setEnchantmentBasedOnDifficulty(p_213386_2_);
        this.inventory.addItem(new ItemStack(Items.PORKCHOP,8));
        return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }


    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
        float f = difficulty.getClampedAdditionalDifficulty();
        if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.3F * f) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int) (5.0F + f * (float) this.rand.nextInt(18)), false));
        }

        for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            if (equipmentslottype.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
                if (!itemstack.isEmpty() && this.rand.nextFloat() < 0.5F * f) {
                    this.setItemStackToSlot(equipmentslottype, EnchantmentHelper.addRandomEnchantment(this.rand, itemstack, (int) (5.0F + f * (float) this.rand.nextInt(18)), false));
                }
            }
        }

    }

    protected void registerData() {
        super.registerData();
        this.getDataManager().register(IS_EATING, false);
    }

    @Override
    public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
        ItemStack itemstack = new ItemStack(Items.BOW);
        Raid raid = this.getRaid();
        int i = 1;
        if (p_213660_1_ > raid.getWaves(Difficulty.NORMAL)) {
            i = 2;
        }

        boolean flag = this.rand.nextFloat() <= raid.func_221308_w();
        boolean flag2 = this.rand.nextFloat() <= 0.25;
        if (flag) {
            Map<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.POWER, i);
            EnchantmentHelper.setEnchantments(map, itemstack);
            if (flag2) {
                Map<Enchantment, Integer> map2 = Maps.newHashMap();
                map2.put(Enchantments.FLAME, 1);
                EnchantmentHelper.setEnchantments(map2, itemstack);
            }
        }

        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

        compound.putInt("CooldownTicks", this.cooldownTicks);

        if (this.homePosition != null) {
            compound.put("HomeTarget", NBTUtil.writeBlockPos(this.homePosition));
        }

        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                listnbt.add(itemstack.write(new CompoundNBT()));
            }
        }

        compound.put("Inventory", listnbt);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);

        this.cooldownTicks = compound.getInt("CooldownTicks");

        if (compound.contains("HomeTarget")) {
            this.homePosition = NBTUtil.readBlockPos(compound.getCompound("HomeTarget"));
        }

        ListNBT listnbt = compound.getList("Inventory", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            ItemStack itemstack = ItemStack.read(listnbt.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.inventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
    }

    public Inventory func_213674_eg() {
        return this.inventory;
    }

    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem();
        Item item = itemstack.getItem();
        if (this.isFoods(item)) {
            ItemStack itemstack1 = this.inventory.addItem(itemstack);
            if (itemstack1.isEmpty()) {
                itemEntity.remove();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }else {
            super.updateEquipmentIfNeeded(itemEntity);
        }

    }

    private boolean isFoods(Item item) {
        return item.isFood() && item != Items.SPIDER_EYE&& item != Items.PUFFERFISH;
    }


    public void setMainHome(@Nullable BlockPos p_213726_1_) {
        this.homePosition = p_213726_1_;
    }


    @Nullable
    public BlockPos getMainHome() {
        return this.homePosition;
    }

    public boolean isCooldown() {
        return this.cooldownTicks > 0;
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    public void setCooldownTicks(int tick) {
        this.cooldownTicks = tick;
    }

    public void setEatFood(boolean drinkingPotion) {
        this.getDataManager().set(IS_EATING, drinkingPotion);
    }

    public boolean isEatFood() {
        return this.getDataManager().get(IS_EATING);
    }

    public void livingTick() {
        if (!this.world.isRemote && this.isAlive()) {

            if (this.isEatFood()) {
                if (this.foodUseTimer-- <= 0) {
                    this.setEatFood(false);
                    ItemStack itemstack = this.getHeldItemOffhand();
                    if (this.isFoods(itemstack.getItem())) {
                        this.heal((float)itemstack.getItem().getFood().getHealing());
                        ItemStack itemstack1 = itemstack.onItemUseFinish(this.world, this);
                        if (!itemstack1.isEmpty()) {
                            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, itemstack1);
                        }
                    }
                }else if(this.foodUseTimer>=0 && this.rand.nextFloat() < 0.1F){
                    this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EAT, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                }
            } else {
                if (this.rand.nextFloat() < 0.0046F && this.getHealth() < this.getMaxHealth()) {
                    if(this.getHeldItemOffhand().isEmpty()) {
                        ItemStack food = findFood();
                        this.setItemStackToSlot(EquipmentSlotType.OFFHAND, food);
                    }
                    this.foodUseTimer = this.getHeldItemOffhand().getUseDuration();
                    this.setEatFood(true);

                    IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                    iattributeinstance.removeModifier(MODIFIER);
                    iattributeinstance.applyModifier(MODIFIER);
                }
            }

            if (this.rand.nextFloat() < 7.5E-4F) {
                this.world.setEntityState(this, (byte) 15);
            }
        }

        super.livingTick();
    }

    public ItemStack findFood(){

        for(int i = 0; i<this.inventory.getSizeInventory(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);

            if(!stack.isEmpty()&&this.isFoods(stack.getItem())){
                return stack;
            }else {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public SoundEvent getRaidLossSound() {
        return HunterSounds.HUNTER_ILLAGER_LAUGH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VINDICATOR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VINDICATOR_HURT;
    }

    @Override
    public void onKillEntity(LivingEntity entity) {

        super.onKillEntity(entity);
        if (!(entity instanceof AbstractIllagerEntity)) {

            this.playSound(HunterSounds.HUNTER_ILLAGER_LAUGH, this.getSoundVolume() + 0.15F, this.getSoundPitch());

            this.setCooldownTicks(400);
        }
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory != null) {
            for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.inventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    this.entityDropItem(itemstack);
                }
            }

        }
    }

    @Override
    protected ResourceLocation getLootTable() {
        return new ResourceLocation(HunterIllagerCore.MODID, "entity/hunter_illager");
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
        AbstractArrowEntity abstractarrowentity = ProjectileHelper.func_221272_a(this, itemstack, distanceFactor);
        if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
            abstractarrowentity = ((net.minecraft.item.BowItem) this.getHeldItemMainhand().getItem()).customeArrow(abstractarrowentity);
        double d0 = target.posX - this.posX;
        double d1 = target.getBoundingBox().minY + (double) (target.getHeight() / 3.0F) - abstractarrowentity.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
        abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        world.addEntity(abstractarrowentity);
    }

    @OnlyIn(Dist.CLIENT)
    public AbstractIllagerEntity.ArmPose getArmPose() {
        return this.isAggressive() ? AbstractIllagerEntity.ArmPose.BOW_AND_ARROW : AbstractIllagerEntity.ArmPose.CROSSED;
    }

    class MoveToGoal extends Goal {
        final EntityHunterIllager hunterIllager;
        final double field_220848_b;
        final double speed;

        MoveToGoal(EntityHunterIllager hunterillager, double distance, double speed) {
            this.hunterIllager = hunterillager;
            this.field_220848_b = distance;
            this.speed = speed;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            EntityHunterIllager.this.navigator.clearPath();
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            BlockPos blockpos = this.hunterIllager.getMainHome();
            return blockpos != null && this.func_220846_a(blockpos, this.field_220848_b) && !isRaidActive();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            BlockPos blockpos = this.hunterIllager.getMainHome();
            if (blockpos != null && EntityHunterIllager.this.navigator.noPath()) {
                if (this.func_220846_a(blockpos, 10.0D)) {
                    Vec3d vec3d = (new Vec3d((double) blockpos.getX() - this.hunterIllager.posX, (double) blockpos.getY() - this.hunterIllager.posY, (double) blockpos.getZ() - this.hunterIllager.posZ)).normalize();
                    Vec3d vec3d1 = vec3d.scale(10.0D).add(this.hunterIllager.posX, this.hunterIllager.posY, this.hunterIllager.posZ);
                    EntityHunterIllager.this.navigator.tryMoveToXYZ(vec3d1.x, vec3d1.y, vec3d1.z, this.speed);
                } else {
                    EntityHunterIllager.this.navigator.tryMoveToXYZ((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), this.speed);
                }
            }

        }

        private boolean func_220846_a(BlockPos p_220846_1_, double p_220846_2_) {
            return !p_220846_1_.withinDistance(this.hunterIllager.getPositionVec(), p_220846_2_);
        }
    }

    private class FindCampfireOrBed extends Goal {
        protected final EntityHunterIllager creature;

        public FindCampfireOrBed(EntityHunterIllager creature, double speedIn) {
            this.creature = creature;
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (this.creature.getMainHome() != null || this.creature.getAttackTarget() != null || isRaidActive()) {
                return false;
            } else {
                return true;
            }
        }



        @Override
        public void tick() {
            super.tick();

            if (this.creature.ticksExisted % 120 == 0) {
                int range = 15;
                for (int x = -range; x <= range; x++) {
                    for (int y = -range / 2; y <= range / 2; y++) {
                        for (int z = -range; z <= range; z++) {
                            BlockPos pos = this.creature.getPosition().add(x, y, z);

                            BlockState state = world.getBlockState(pos);

                            if (state.getBlock() == Blocks.CAMPFIRE) {
                                this.creature.setMainHome(pos);

                                return;

                            } else if (state.getBlock() instanceof BedBlock) {
                                this.creature.setMainHome(pos);

                                return;
                            }

                        }

                    }

                }
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return !this.creature.getNavigator().noPath();
        }

    }
}
